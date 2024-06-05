#ifndef RFID_h
#define RFID_h

#include <Arduino.h>
#include <SoftwareSerial.h>


class RFID {
public:
    RFID(int rxPin, int txPin);
    void begin();
    String getVersion();
    String getReaderID();
    String readTagEPC();
    String readMemoryBank(int bank, int address, int length);
    bool writeMemoryBank(int bank, int address, int length, String data);
    bool killTag(String password, int recommission);
    bool lockMemory(String mask, String action);
    void setBaudRate(int baudRate);
    String readMultiTagEPC(int slotQ, int bank, int address, int length);
    bool overflow();
    

private:
    SoftwareSerial *rfidSerial;
    String sendCommand(String command);
};

#endif