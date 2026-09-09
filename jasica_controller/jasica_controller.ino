/*
 * ==========================================================
 * Project: J.A.R.V.I.S / JASICA Hardware Controller
 * Target Board: Arduino UNO
 * Description: 4-Channel Active-LOW Relay Controller
 * Supports: 
 *   1. Direct USB Serial (Serial Monitor / Web App)
 *   2. External Bluetooth Module (HC-05 / HC-06 via SoftwareSerial)
 * ==========================================================
 */

#include <SoftwareSerial.h>

// --- BLUETOOTH MODULE PINS (HC-05 / HC-06) ---
// Connect HC-05 TX to Arduino Pin 2 (RX)
// Connect HC-05 RX to Arduino Pin 3 (TX) (via voltage divider recommended)
const int BT_RX = 2;
const int BT_TX = 3;
SoftwareSerial SerialBT(BT_RX, BT_TX);

// --- PIN ASSIGNMENTS (Arduino UNO Digital Pins) ---
const int PC_PIN     = 4;   // Commands: a / A
const int RGB_PIN    = 5;   // Commands: b / B
const int ROOM_LIGHT = 6;   // Commands: c / C
const int PLUG_PIN   = 7;   // Commands: d / D

// --- ACTIVE-LOW RELAY LOGIC ---
// Active-LOW relays: LOW = Relay ON, HIGH = Relay OFF
const int RELAY_ON  = LOW;
const int RELAY_OFF = HIGH;

String incomingUSB = "";
String incomingBT  = "";

void setup() {
  // 1. Initialize USB Serial (for PC / Web interface)
  Serial.begin(9600);

  // 2. Initialize Bluetooth Serial (Default baud for HC-05/HC-06 is 9600)
  SerialBT.begin(9600);

  // 3. Set default states to OFF (HIGH) BEFORE setting them as OUTPUT
  // This prevents relay glitching/clicking during bootup
  digitalWrite(PC_PIN, RELAY_OFF);
  digitalWrite(RGB_PIN, RELAY_OFF);
  digitalWrite(ROOM_LIGHT, RELAY_OFF);
  digitalWrite(PLUG_PIN, RELAY_OFF);

  // 4. Initialize pins as outputs
  pinMode(PC_PIN, OUTPUT);
  pinMode(RGB_PIN, OUTPUT);
  pinMode(ROOM_LIGHT, OUTPUT);
  pinMode(PLUG_PIN, OUTPUT);

  Serial.println(F("=================================================="));
  Serial.println(F("SYSTEM.OS: J.A.R.V.I.S Hardware Interface Booted (Arduino UNO)"));
  Serial.println(F("AWAITING_UPLINK (Listening on USB Serial & Bluetooth)..."));
  Serial.println(F("=================================================="));
}

void loop() {
  // Listen for commands from USB Serial
  while (Serial.available() > 0) {
    char c = (char)Serial.read();
    if (c == '\n' || c == '\r') {
      if (incomingUSB.length() > 0) {
        incomingUSB.trim();
        executeCommand(incomingUSB, "USB");
        incomingUSB = "";
      }
    } else {
      incomingUSB += c;
    }
  }

  // Listen for commands from Bluetooth (HC-05 / HC-06)
  while (SerialBT.available() > 0) {
    char c = (char)SerialBT.read();
    if (c == '\n' || c == '\r') {
      if (incomingBT.length() > 0) {
        incomingBT.trim();
        executeCommand(incomingBT, "BLUETOOTH");
        incomingBT = "";
      }
    } else {
      incomingBT += c;
    }
  }
}

// --- COMMAND ROUTING & EXECUTION ---
void executeCommand(String cmd, String source) {
  Serial.print(F(">> ["));
  Serial.print(source);
  Serial.print(F("] INCOMING_CMD: "));
  Serial.println(cmd);

  // Send acknowledgment over Bluetooth as well
  SerialBT.print(F("ACK: "));
  SerialBT.println(cmd);

  // --- INDIVIDUAL TOGGLES ---
  if (cmd == "a") {
    digitalWrite(PC_PIN, RELAY_ON);
    Serial.println(F(">> PC: ON"));
  } 
  else if (cmd == "A") {
    digitalWrite(PC_PIN, RELAY_OFF);
    Serial.println(F(">> PC: OFF"));
  } 
  else if (cmd == "b") {
    digitalWrite(RGB_PIN, RELAY_ON);
    Serial.println(F(">> RGB: ON"));
  } 
  else if (cmd == "B") {
    digitalWrite(RGB_PIN, RELAY_OFF);
    Serial.println(F(">> RGB: OFF"));
  } 
  else if (cmd == "c") {
    digitalWrite(ROOM_LIGHT, RELAY_ON);
    Serial.println(F(">> ROOM_LIGHT: ON"));
  } 
  else if (cmd == "C") {
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    Serial.println(F(">> ROOM_LIGHT: OFF"));
  } 
  else if (cmd == "d") {
    digitalWrite(PLUG_PIN, RELAY_ON);
    Serial.println(F(">> PLUG: ON"));
  } 
  else if (cmd == "D") {
    digitalWrite(PLUG_PIN, RELAY_OFF);
    Serial.println(F(">> PLUG: OFF"));
  } 

  // --- MACRO / GROUP COMMANDS ---
  else if (cmd == "on" || cmd == "ON" || cmd == "all on") {
    digitalWrite(PC_PIN, RELAY_ON);
    digitalWrite(RGB_PIN, RELAY_ON);
    digitalWrite(ROOM_LIGHT, RELAY_ON);
    digitalWrite(PLUG_PIN, RELAY_ON);
    Serial.println(F(">> ALL DEVICES: ON"));
  } 
  else if (cmd == "off" || cmd == "OFF" || cmd == "all off") {
    digitalWrite(PC_PIN, RELAY_OFF);
    digitalWrite(RGB_PIN, RELAY_OFF);
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    digitalWrite(PLUG_PIN, RELAY_OFF);
    Serial.println(F(">> ALL DEVICES: OFF"));
  } 
  else if (cmd == "mood" || cmd == "MOOD") {
    // "Very Stark Tower." -> Main light off, aesthetic RGB on
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    digitalWrite(RGB_PIN, RELAY_ON);
    Serial.println(F(">> MOOD MODE ACTIVATED (Room Light OFF, RGB ON)"));
  } 
  else if (cmd == "status" || cmd == "STATUS") {
    printStatus();
  }
  else {
    Serial.println(F("ERR: UNKNOWN_SIG"));
    SerialBT.println(F("ERR: UNKNOWN_SIG"));
  }
}

void printStatus() {
  Serial.println(F("--- HARDWARE STATUS ---"));
  Serial.print(F("PC (Pin 4):         ")); Serial.println(digitalRead(PC_PIN) == RELAY_ON ? F("ON") : F("OFF"));
  Serial.print(F("RGB (Pin 5):        ")); Serial.println(digitalRead(RGB_PIN) == RELAY_ON ? F("ON") : F("OFF"));
  Serial.print(F("Room Light (Pin 6): ")); Serial.println(digitalRead(ROOM_LIGHT) == RELAY_ON ? F("ON") : F("OFF"));
  Serial.print(F("Plug (Pin 7):       ")); Serial.println(digitalRead(PLUG_PIN) == RELAY_ON ? F("ON") : F("OFF"));
  Serial.println(F("-----------------------"));
}
