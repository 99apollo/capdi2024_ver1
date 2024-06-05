#include "secret.h"
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"

#define AWS_IOT_PUBLISH_TOPIC   "ESP32/pub"
#define AWS_IOT_SUBSCRIBE_TOPIC "ESP32/sub"
HardwareSerial mySerial(2);
WiFiClientSecure net;
PubSubClient client(net);
int listcount = 0;
int percount = 0;

void connectAWS() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.println("Connecting to Wi-Fi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  // Configure WiFiClientSecure to use the AWS IoT device credentials
  net.setCACert(AWS_CERT_CA);
  net.setCertificate(AWS_CERT_CRT);
  net.setPrivateKey(AWS_CERT_PRIVATE);

  // Connect to the MQTT broker on the AWS endpoint we defined earlier
  client.setServer(AWS_IOT_ENDPOINT, 8883);

  // Create a message handler
  client.setCallback(messageHandler);

  Serial.println("Connecting to AWS IoT");

  while (!client.connect(THINGNAME)) {
    Serial.print(".");
    delay(100);
  }

  if (!client.connected()) {
    Serial.println("AWS IoT Timeout!");
    return;
  }

  // Subscribe to a topic
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC);
  if (client.connected()) {
    Serial.println("AWS IoT Connected!");
  }
}

void clearJsonBuffer(char* buffer, size_t size) {
  memset(buffer, 0, size);
}

void publishMessage() {
  StaticJsonDocument<512> doc;
  char jsonBuffer[512];
  serializeJson(doc, jsonBuffer); // print to client

  client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer);
}

void messageHandler(char *topic, byte *payload, unsigned int length) {
  Serial.print("incoming: ");
  Serial.println(topic);

  StaticJsonDocument<200> doc;
  deserializeJson(doc, payload);
  const char *message = doc["message"];
  Serial.println(message);
}

void setup() {
  Serial.begin(38400);
  //Serial2.begin(38400); // ESP32의 시리얼 통신 시작
  mySerial.begin(38400,SERIAL_8N1,21,22);
  connectAWS();
}

void loop() {
  if (mySerial.available()) {
    char incomingBuffer[512];
    size_t bytesRead = 0;
    unsigned long timeout = millis() + 500; // 500 밀리초 후 타임아웃
    
    // 타임아웃 내에 모든 사용 가능한 데이터 읽기
    while (millis() < timeout && bytesRead < sizeof(incomingBuffer) - 1) {
      if (mySerial.available()) {
        bytesRead += mySerial.readBytes(incomingBuffer + bytesRead, sizeof(incomingBuffer) - bytesRead - 1);
        timeout = millis() + 500; // 타임아웃 리셋
      }
    }
    incomingBuffer[bytesRead] = '\0'; // 문자열 끝을 null로 종료
    String incomingString = String(incomingBuffer);
    incomingString.trim(); // 문자열 끝의 제어 문자 제거

    listcount = listcount % 10;
    listcount++;
    Serial.println("listcount : " + String(listcount));
    Serial.println(incomingString);

    // 주어진 데이터를 줄 단위로 분할
    int startPos = 0;
    int endPos;

    while ((endPos = incomingString.indexOf('\n', startPos)) != -1) {
      String line = incomingString.substring(startPos, endPos);
      startPos = endPos + 1;

      // 쉼표를 기준으로 키와 값을 추출
      int commaPos = line.indexOf(',');
      if (commaPos != -1) {
        String key = line.substring(commaPos + 1); // TID 값
        String value = line.substring(0, commaPos); // EPC 값

        // 값의 끝에 있는 '\r' 문자 제거
        value.trim();
        key.trim();

        // 키 값이 비어 있지 않으면 데이터를 추가
        if (!key.isEmpty()) {
          StaticJsonDocument<512> doc;
          doc["cartid"] = 1;
          doc["listcount"] = listcount;

          // 객체를 생성하여 아이템을 저장할 공간을 할당
          JsonObject items = doc.createNestedObject("items");

          // 키와 값을 아이템 객체에 추가
          items[key] = value;

          // JSON을 serialize하여 MQTT 메시지로 전송
          char jsonBuffer[512];
          serializeJson(doc, jsonBuffer);
          client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer);
        }
      }
    }

    // 마지막 줄 처리
    if (startPos < incomingString.length()) {
      String line = incomingString.substring(startPos);

      // 쉼표를 기준으로 키와 값을 추출
      int commaPos = line.indexOf(',');
      if (commaPos != -1) {
        String key = line.substring(commaPos + 1); // TID 값
        String value = line.substring(0, commaPos); // EPC 값

        // 값의 끝에 있는 '\r' 문자 제거
        value.trim();
        key.trim();

        // 키 값이 비어 있지 않으면 데이터를 추가
        if (!key.isEmpty()) {
          StaticJsonDocument<512> doc;
          doc["cartid"] = 1;
          doc["listcount"] = listcount;

          // 객체를 생성하여 아이템을 저장할 공간을 할당
          JsonObject items = doc.createNestedObject("items");

          // 키와 값을 아이템 객체에 추가
          items[key] = value;

          // JSON을 serialize하여 MQTT 메시지로 전송
          char jsonBuffer[512];
          serializeJson(doc, jsonBuffer);
          client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer);
        }
      }
    }
  }

  client.loop();
}

