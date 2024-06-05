#include "RFID.h"

RFID::RFID(int rxPin, int txPin) {
    rfidSerial = new SoftwareSerial(rxPin, txPin);
}


bool RFID::overflow(){
  return rfidSerial->overflow();
}

void RFID::begin() {
    rfidSerial->begin(38400);
}
String RFID::sendCommand(String command) {
    rfidSerial->println(command);
    String response = "";
    unsigned long timeout = millis() + 100; // Timeout after 1 second
    
    // Read all available data within timeout
    while (millis() < timeout) {
        if (rfidSerial->available()) {
            response += rfidSerial->readString();
            timeout = millis() + 100; // Reset timeout
        }
    }

    return response;
}





String RFID::getVersion() {
    return sendCommand("V");
}

String RFID::getReaderID() {
    return sendCommand("S");
}

String RFID::readTagEPC() {
    return sendCommand("Q");
}

String RFID::readMemoryBank(int bank, int address, int length) {
    String command = "R" + String(bank) + "," + String(address) + "," + String(length);
    return sendCommand(command);
}

bool RFID::writeMemoryBank(int bank, int address, int length, String data) {
    String command = "W" + String(bank) + "," + String(address) + "," + String(length) + "," + data;
    String response = sendCommand(command);
    return response.indexOf("<OK>") != -1;
}

bool RFID::killTag(String password, int recommission) {
    String command = "K" + password + "," + String(recommission);
    String response = sendCommand(command);
    return response.indexOf("<OK>") != -1;
}

bool RFID::lockMemory(String mask, String action) {
    String command = "L" + mask + "," + action;
    String response = sendCommand(command);
    return response.indexOf("<OK>") != -1;
}

void RFID::setBaudRate(int baudRate) {
    String command = "NA," + String(baudRate);
    sendCommand(command);
}

String RFID::readMultiTagEPC(int slotQ, int bank, int address, int length) {
    String command = "U" + String(slotQ) +","+"R" + String(bank) + "," + String(address) + "," + String(length);
    return sendCommand(command);
}