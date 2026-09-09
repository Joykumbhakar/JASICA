import { NextRequest, NextResponse } from "next/server";

// In-memory pin & device status tracking for remote API and webhooks
let deviceStates = {
  pc: false,        // Pin 4: a / A
  rgb: false,       // Pin 5: b / B
  roomLight: false, // Pin 6: c / C
  plug: false,      // Pin 7: d / D
};

let commandLogs: Array<{ id: string; cmd: string; timestamp: string; source: string; status: string }> = [];

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const cmd = searchParams.get("cmd");
  
  if (!cmd) {
    return NextResponse.json({
      success: true,
      currentStates: deviceStates,
      recentLogs: commandLogs.slice(-10)
    });
  }

  return handleCommand(cmd, "GET_QUERY");
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const cmd = body.command || body.raw || body.cmd;
    const source = body.source || "REST_API";

    if (!cmd) {
      return NextResponse.json({ success: false, error: "Missing command parameter" }, { status: 400 });
    }

    return handleCommand(cmd, source);
  } catch (err: any) {
    return NextResponse.json({ success: false, error: "Invalid JSON payload" }, { status: 400 });
  }
}

function handleCommand(cmd: string, source: string) {
  const normalized = cmd.trim();
  let actionTaken = "";

  switch (normalized) {
    case "a":
      deviceStates.pc = true;
      actionTaken = "PC Power ON";
      break;
    case "A":
      deviceStates.pc = false;
      actionTaken = "PC Power OFF";
      break;
    case "b":
      deviceStates.rgb = true;
      actionTaken = "RGB Strip ON";
      break;
    case "B":
      deviceStates.rgb = false;
      actionTaken = "RGB Strip OFF";
      break;
    case "c":
      deviceStates.roomLight = true;
      actionTaken = "Room Light ON";
      break;
    case "C":
      deviceStates.roomLight = false;
      actionTaken = "Room Light OFF";
      break;
    case "d":
      deviceStates.plug = true;
      actionTaken = "Power Plug ON";
      break;
    case "D":
      deviceStates.plug = false;
      actionTaken = "Power Plug OFF";
      break;
    case "on":
    case "ON":
    case "all on":
      deviceStates = { pc: true, rgb: true, roomLight: true, plug: true };
      actionTaken = "All Devices ON";
      break;
    case "off":
    case "OFF":
    case "all off":
      deviceStates = { pc: false, rgb: false, roomLight: false, plug: false };
      actionTaken = "All Devices OFF";
      break;
    case "mood":
    case "MOOD":
      deviceStates.roomLight = false;
      deviceStates.rgb = true;
      actionTaken = "Mood Mode Active (Room OFF, RGB ON)";
      break;
    default:
      actionTaken = `Custom Signal: ${normalized}`;
  }

  const logEntry = {
    id: Math.random().toString(36).substring(2, 9),
    cmd: normalized,
    timestamp: new Date().toLocaleTimeString(),
    source,
    status: actionTaken
  };
  commandLogs.push(logEntry);
  if (commandLogs.length > 50) commandLogs.shift();

  return NextResponse.json({
    success: true,
    command: normalized,
    action: actionTaken,
    source,
    timestamp: new Date().toISOString(),
    deviceStates
  });
}

