/*
 * =========================================================================
 * Project: JASICA AI - 12-Pin Arduino UNO Bluetooth Controller
 * Target: Arduino UNO (ATmega328P)
 * Pins Used:
 *   - Pin 0 (RX) & Pin 1 (TX) : Hardware Serial for HC-05 / ESP32 Bluetooth
 *   - Pin 2 to Pin 13         : 12-Channel Relay / LED Digital Outputs
 * Baud Rate: 9600
 * =========================================================================
 */

// Set to 'true' if using Active-LOW relay modules (LOW = Relay ON, HIGH = Relay OFF)
// Set to 'false' if using Active-HIGH relays or direct LEDs (HIGH = ON, LOW = OFF)
#define ACTIVE_LOW true

#if ACTIVE_LOW
  const int STATE_ON  = LOW;
  const int STATE_OFF = HIGH;
#else
  const int STATE_ON  = HIGH;
  const int STATE_OFF = LOW;
#endif

// Array of 12 Output Pins (Pins 2 to 13)
const int NUM_PINS = 12;
const int PINS[NUM_PINS] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

// Command character mappings:
// Pin 2:  a (ON) / A (OFF)
// Pin 3:  b (ON) / B (OFF)
// Pin 4:  c (ON) / C (OFF)
// Pin 5:  d (ON) / D (OFF)
// Pin 6:  e (ON) / E (OFF)
// Pin 7:  f (ON) / F (OFF)
// Pin 8:  g (ON) / G (OFF)
// Pin 9:  h (ON) / H (OFF)
// Pin 10: i (ON) / I (OFF)
// Pin 11: j (ON) / J (OFF)
// Pin 12: k (ON) / K (OFF)
// Pin 13: l (ON) / L (OFF)

String inputBuffer = "";

void setup() {
  // 1. Initialize Hardware Serial at 9600 baud for HC-05 Bluetooth
  Serial.begin(9600);

  // 2. Set default states to OFF BEFORE declaring as OUTPUT (prevents bootup click/flicker)
  for (int i = 0; i < NUM_PINS; i++) {
    digitalWrite(PINS[i], STATE_OFF);
    pinMode(PINS[i], OUTPUT);
  }

  // 3. Boot message
  Serial.println(F("=================================================="));
  Serial.println(F("[JASICA] 12-Pin Bluetooth Controller Ready"));
  Serial.println(F("[JASICA] Pins 2-13 Active (Commands: a-l / A-L)"));
  Serial.println(F("=================================================="));
}

void loop() {
  while (Serial.available() > 0) {
    char c = (char)Serial.read();

    if (c == '\n' || c == '\r') {
      if (inputBuffer.length() > 0) {
        inputBuffer.trim();
        processCommand(inputBuffer);
        inputBuffer = "";
      }
    } else {
      inputBuffer += c;
    }
  }
}

void processCommand(String cmd) {
  // 1. Single-character Pin Commands ('a'-'l' for ON, 'A'-'L' for OFF)
  if (cmd.length() == 1) {
    char c = cmd.charAt(0);

    // Check lowercase 'a' to 'l' (Turn ON Pins 2 to 13)
    if (c >= 'a' && c <= 'l') {
      int index = c - 'a';
      int pin = PINS[index];
      digitalWrite(pin, STATE_ON);
      Serial.print(F("[JASICA] OK: Pin "));
      Serial.print(pin);
      Serial.println(F(" turned ON"));
      return;
    }

    // Check uppercase 'A' to 'L' (Turn OFF Pins 2 to 13)
    if (c >= 'A' && c <= 'L') {
      int index = c - 'A';
      int pin = PINS[index];
      digitalWrite(pin, STATE_OFF);
      Serial.print(F("[JASICA] OK: Pin "));
      Serial.print(pin);
      Serial.println(F(" turned OFF"));
      return;
    }
  }

  // 2. Macro Commands
  String upperCmd = cmd;
  upperCmd.toUpperCase();

  if (upperCmd == "ON" || upperCmd == "ALL ON") {
    for (int i = 0; i < NUM_PINS; i++) {
      digitalWrite(PINS[i], STATE_ON);
    }
    Serial.println(F("[JASICA] OK: All 12 Pins turned ON"));
  }
  else if (upperCmd == "OFF" || upperCmd == "ALL OFF") {
    for (int i = 0; i < NUM_PINS; i++) {
      digitalWrite(PINS[i], STATE_OFF);
    }
    Serial.println(F("[JASICA] OK: All 12 Pins turned OFF"));
  }
  else if (upperCmd == "MOOD") {
    // Turn off general lights (Pins 2-5), turn on accent RGB / LEDs (Pins 6-7)
    digitalWrite(PINS[0], STATE_OFF); // Pin 2 OFF
    digitalWrite(PINS[1], STATE_OFF); // Pin 3 OFF
    digitalWrite(PINS[2], STATE_OFF); // Pin 4 OFF
    digitalWrite(PINS[3], STATE_OFF); // Pin 5 OFF
    digitalWrite(PINS[4], STATE_ON);  // Pin 6 ON
    digitalWrite(PINS[5], STATE_ON);  // Pin 7 ON
    Serial.println(F("[JASICA] OK: Mood Mode Activated"));
  }
  else if (upperCmd == "STATUS") {
    printStatus();
  }
  else {
    Serial.print(F("[JASICA] ERR: Unknown command '"));
    Serial.print(cmd);
    Serial.println(F("'"));
  }
}

void printStatus() {
  Serial.println(F("--- 12-PIN HARDWARE STATUS ---"));
  for (int i = 0; i < NUM_PINS; i++) {
    int state = digitalRead(PINS[i]);
    bool isOn = (state == STATE_ON);
    char onChar = 'a' + i;
    char offChar = 'A' + i;

    Serial.print(F("Pin "));
    if (PINS[i] < 10) Serial.print(F(" "));
    Serial.print(PINS[i]);
    Serial.print(F(" ["));
    Serial.print(onChar);
    Serial.print(F("/"));
    Serial.print(offChar);
    Serial.print(F("]: "));
    Serial.println(isOn ? F("ON") : F("OFF"));
  }
  Serial.println(F("------------------------------"));
}
