#include <SoftwareSerial.h>
#include "RFID.h"

#define RX_PIN 52   // ESP32의 TX(22) 핀 주황
#define TX_PIN 53    // ESP32의 RX(21) 핀 노랑

// RFID 모듈이 연결된 Rx, Tx 핀 설정
const int RFID_RX_PIN = 11;  // Rx 핀
const int RFID_TX_PIN = 10;  // Tx 핀
int listcount = 0; // 리스트 구분용 인자
// gnd - 파랑, en - 초록, rx - 노랑 10, tx - 검/흰 11 , 5v - 빨강/주황
RFID rfid(RFID_RX_PIN, RFID_TX_PIN);
SoftwareSerial espSerial(RX_PIN, TX_PIN); // 소프트웨어 시리얼 객체 생성  

void setup() {
  Serial.begin(38400);     // 시리얼 통신 시작
  espSerial.begin(38400);  // 소프트웨어 시리얼 통신 시작
  rfid.begin(); // RFID 모듈 초기화
}

bool isTagUnique(String tag, String* tags, int count) {
  for (int i = 0; i < count; ++i) {
    if (tags[i] == tag) {
      return false;
    }
  }
  return true;
}

bool isValidTagFormat(const String& tag) {
  int commaPos = tag.indexOf(',');
  if (commaPos == -1) {
    return false;
  }
  
  String part1 = tag.substring(0, commaPos);
  String part2 = tag.substring(commaPos + 1);
  
  // "U"와 "RE"로 시작하고 길이가 적절한지 확인
  return part1.startsWith("U") && part1.length() == 33 && part2.startsWith("RE") && part2.length() == 25;
}

void loop() {
  Serial.println("태그 읽기 시작, listcount : " + String(listcount));

  unsigned long startTime = millis();
  String tags[100]; // 최대 100개의 태그를 저장할 수 있는 배열
  int tagCount = 0;

  for (int i = 0; i <20; ++i) {
    String multiTagEPC = rfid.readMultiTagEPC(9, 2, 0, 6); // 슬롯 Q 값 9로 설정
    if (rfid.overflow()) {
      Serial.println("오버플로우");
    }

    // 각 태그를 분리하여 uniqueTags에 저장
    int startPos = 0;
    int endPos;
    while ((endPos = multiTagEPC.indexOf('\n', startPos)) != -1) {
      String tag = multiTagEPC.substring(startPos, endPos);
      tag.trim();
      if (isValidTagFormat(tag) && isTagUnique(tag, tags, tagCount)) {
        tags[tagCount++] = tag;
      }
      startPos = endPos + 1;
    }
    
    delay(70); // 10ms 딜레이 추가
  }

  // tags 배열에 저장된 모든 태그를 하나의 문자열로 연결
  String combinedTags;
  for (int i = 0; i < tagCount; ++i) {
    combinedTags += tags[i] + "\n";
  }

  unsigned long endTime = millis();
  unsigned long readDuration = endTime - startTime;

  combinedTags.trim();
  combinedTags.replace("\nU\r\n", "");
  combinedTags.replace("\r\n", "\r");

  Serial.print("Multi-Tag EPC: " + combinedTags);
  Serial.println("\n태그 읽기 끝");
  Serial.println("읽기 시간: " + String(readDuration) + "ms");

  listcount++;
  listcount = listcount % 10;

  // 태그 정보를 ESP32로 전송
  espSerial.println(combinedTags);

  delay(300);
}
