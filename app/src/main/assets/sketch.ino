// ================================================================
//  Smart Home Controller - PRODUCTION CODE
//  Arduino UNO + HC-05 + Android App
//
//  WIRING
//  HC-05 VCC  -> Arduino 5V
//  HC-05 GND  -> Arduino GND
//  HC-05 TX   -> Arduino Pin 10
//  HC-05 RX   -> Arduino Pin 11 (via voltage divider, see below)
//
//  VOLTAGE DIVIDER (Pin 11 -> HC-05 RX):
//    Pin 11 -> [1k resistor] -> HC-05 RX pin
//                            -> [2k resistor] -> GND
//
//  OUTPUT DEVICES
//  Pin 2 -> 220ohm resistor -> LED anode -> LED cathode -> GND
//  Pin 3 -> 220ohm resistor -> LED anode -> LED cathode -> GND
//  Pin 4 -> 220ohm resistor -> LED anode -> LED cathode -> GND
//  Pin 5 -> 220ohm resistor -> LED anode -> LED cathode -> GND
//  (replace LEDs with relay modules for real lights/fans)
//
//  UPLOAD STEPS
//  1. Keep HC-05 connected (pins 10/11 do NOT block upload)
//  2. Upload this sketch via USB
//  3. Open Serial Monitor at 9600 baud to see debug output
//  4. Pair phone to HC-05 (PIN: 1234 or 0000)
//  5. Open app, tap Connect, start sending commands
// ================================================================

#include <SoftwareSerial.h>

// HC-05 connected on pin 10 (RX) and pin 11 (TX)
SoftwareSerial btSerial(10, 11);

// Output pin definitions
#define PIN_LEFT_LIGHT   2
#define PIN_RIGHT_LIGHT  3
#define PIN_LEFT_FAN     4
#define PIN_RIGHT_FAN    5
#define PIN_STATUS_LED   13   // built-in LED blinks on each command

String inputBuffer = "";

// ================================================================
void setup() {
  pinMode(PIN_LEFT_LIGHT,  OUTPUT);
  pinMode(PIN_RIGHT_LIGHT, OUTPUT);
  pinMode(PIN_LEFT_FAN,    OUTPUT);
  pinMode(PIN_RIGHT_FAN,   OUTPUT);
  pinMode(PIN_STATUS_LED,  OUTPUT);

  // All outputs OFF on power-up
  digitalWrite(PIN_LEFT_LIGHT,  LOW);
  digitalWrite(PIN_RIGHT_LIGHT, LOW);
  digitalWrite(PIN_LEFT_FAN,    LOW);
  digitalWrite(PIN_RIGHT_FAN,   LOW);
  digitalWrite(PIN_STATUS_LED,  LOW);

  Serial.begin(9600);    // USB debug monitor
  btSerial.begin(9600);  // HC-05 baud rate (default 9600)

  Serial.println("==============================");
  Serial.println("  Smart Home Ready");
  Serial.println("  Waiting for Bluetooth...");
  Serial.println("==============================");
}

// ================================================================
void loop() {
  // Commands arriving from Android app via HC-05
  while (btSerial.available()) {
    processChar((char)btSerial.read());
  }

  // Also accept commands from USB Serial Monitor (for testing)
  while (Serial.available()) {
    processChar((char)Serial.read());
  }
}

// ================================================================
//  Accumulate characters and trigger on newline
// ================================================================
void processChar(char c) {
  if (c == '\n' || c == '\r') {
    inputBuffer.trim();
    if (inputBuffer.length() > 0) {
      flashStatusLed();
      handleCommand(inputBuffer);
      inputBuffer = "";
    }
  } else {
    inputBuffer += c;
    if (inputBuffer.length() > 64) {
      inputBuffer = "";  // discard if too long (noise protection)
    }
  }
}

// ================================================================
//  Parse and execute command
//  The Android app sends exactly:
//    "turn on left light"
//    "turn off left light"
//    "turn on right light"
//    "turn off right light"
//    "turn on left fan"
//    "turn off left fan"
//    "turn on right fan"
//    "turn off right fan"
// ================================================================
void handleCommand(String cmd) {
  cmd.toLowerCase();
  cmd.trim();

  Serial.print("CMD: ");
  Serial.println(cmd);

  // Determine direction
  bool isOn  = (cmd.indexOf("turn on")  >= 0);
  bool isOff = (cmd.indexOf("turn off") >= 0);

  if (!isOn && !isOff) {
    Serial.println("  -> ignored (no turn on/off)");
    return;
  }

  // Match device
  int    pin  = -1;
  String name = "";

  if (cmd.indexOf("left light") >= 0) {
    pin  = PIN_LEFT_LIGHT;
    name = "Left Light";
  } else if (cmd.indexOf("right light") >= 0) {
    pin  = PIN_RIGHT_LIGHT;
    name = "Right Light";
  } else if (cmd.indexOf("left fan") >= 0) {
    pin  = PIN_LEFT_FAN;
    name = "Left Fan";
  } else if (cmd.indexOf("right fan") >= 0) {
    pin  = PIN_RIGHT_FAN;
    name = "Right Fan";
  }

  if (pin == -1) {
    Serial.println("  -> ignored (no device matched)");
    return;
  }

  // Set the pin
  digitalWrite(pin, isOn ? HIGH : LOW);

  // Log to USB monitor
  Serial.print("  -> ");
  Serial.print(name);
  Serial.println(isOn ? " ON" : " OFF");

  // Send acknowledgement back to Android app
  String ack = name + (isOn ? ":ON" : ":OFF") + "\n";
  btSerial.print(ack);

  printStatus();
}

// ================================================================
//  Print all pin states to USB Serial Monitor
// ================================================================
void printStatus() {
  Serial.println("  [2]LeftLight  [3]RightLight  [4]LeftFan  [5]RightFan");
  Serial.print("      ");
  Serial.print(digitalRead(PIN_LEFT_LIGHT)  ? "ON    " : "OFF   ");
  Serial.print("       ");
  Serial.print(digitalRead(PIN_RIGHT_LIGHT) ? "ON     " : "OFF    ");
  Serial.print("    ");
  Serial.print(digitalRead(PIN_LEFT_FAN)    ? "ON   " : "OFF  ");
  Serial.print("      ");
  Serial.println(digitalRead(PIN_RIGHT_FAN) ? "ON" : "OFF");
  Serial.println("------------------------------------------------------");
}

// ================================================================
//  Flash the built-in LED briefly on each received command
// ================================================================
void flashStatusLed() {
  digitalWrite(PIN_STATUS_LED, HIGH);
  delay(50);
  digitalWrite(PIN_STATUS_LED, LOW);
}
