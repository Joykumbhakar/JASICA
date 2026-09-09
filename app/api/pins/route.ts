import { NextRequest, NextResponse } from "next/server";

// Comprehensive 12-pin matrix state (Pins 2 to 13)
let pinMatrix: Record<number, { pin: number; label: string; mode: string; state: boolean; isRelay: boolean }> = {
  2: { pin: 2, label: "BT RX / Pin 2", mode: "OUTPUT", state: false, isRelay: false },
  3: { pin: 3, label: "BT TX / Pin 3", mode: "OUTPUT", state: false, isRelay: false },
  4: { pin: 4, label: "PC Relay (Active-LOW)", mode: "OUTPUT", state: false, isRelay: true },
  5: { pin: 5, label: "RGB Relay (Active-LOW)", mode: "OUTPUT", state: false, isRelay: true },
  6: { pin: 6, label: "Room Light (Active-LOW)", mode: "OUTPUT", state: false, isRelay: true },
  7: { pin: 7, label: "Plug Relay (Active-LOW)", mode: "OUTPUT", state: false, isRelay: true },
  8: { pin: 8, label: "Aux Relay 5", mode: "OUTPUT", state: false, isRelay: false },
  9: { pin: 9, label: "PWM Motor / LED", mode: "OUTPUT", state: false, isRelay: false },
  10: { pin: 10, label: "Aux Digital 10", mode: "OUTPUT", state: false, isRelay: false },
  11: { pin: 11, label: "PWM Aux 11", mode: "OUTPUT", state: false, isRelay: false },
  12: { pin: 12, label: "Sensor Echo / Pin 12", mode: "INPUT", state: false, isRelay: false },
  13: { pin: 13, label: "Built-in LED / Pin 13", mode: "OUTPUT", state: false, isRelay: false },
};

export async function GET() {
  return NextResponse.json({
    success: true,
    pins: Object.values(pinMatrix),
    relayLogic: "ACTIVE_LOW (LOW = ON, HIGH = OFF)",
    baudRate: 9600
  });
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { pin, state, label } = body;

    if (pin === undefined || !pinMatrix[pin]) {
      return NextResponse.json({ success: false, error: "Invalid pin number (2-13)" }, { status: 400 });
    }

    if (state !== undefined) {
      pinMatrix[pin].state = Boolean(state);
    }
    if (label) {
      pinMatrix[pin].label = label;
    }

    return NextResponse.json({
      success: true,
      pin: pinMatrix[pin],
      message: `Pin ${pin} updated to ${pinMatrix[pin].state ? "HIGH/ON" : "LOW/OFF"}`
    });
  } catch (err) {
    return NextResponse.json({ success: false, error: "Invalid request payload" }, { status: 400 });
  }
}

