package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api

import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

@RunWith(Enclosed)
class ChatworkClientSpec{
  static String readFixture(String fileName){
    ChatworkClientSpec.class.getClassLoader().getResource("com/vexus2/jenkins/chatwork/jenkinschatworkplugin/${fileName}").getText()
  }

  static class sendMessage extends Specification {
    static final String TAPE_NAME = "ChatWork_v1_POST_rooms_messages"

    ChatworkClient client

    String roomId = "00000000"

    def setup(){
      String apiKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      String proxySv = "NOPROXY"
      String proxyPort = "80"
      client = new StubChatworkClient(apiKey, proxySv, proxyPort)
      client.path = "/rooms/00000000/messages"
    }

    def "sendMessage should send message"(){
      expect:
      client.sendMessage(roomId, "testMessage")
    }
  }

  static class enabledProxy extends Specification {
    @Unroll
    def "proxySv=#proxySv, proxyPort=#proxyPort: isEnabledProxy() == #expected"(){
      setup:
      ChatworkClient client = new ChatworkClient("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", proxySv, proxyPort)

      expect:
      client.isEnabledProxy() == expected

      where:
      proxySv     | proxyPort || expected
      "NOPROXY"   | ""        || false
      "NOPROXY"   | "80"      || false
      "localhost" | "80"      || true
      "localhost" | "str"     || false
      "localhost" | ""        || false
      ""          | "80"      || false
      "localhost" | " "       || false
      " "         | "80"      || false
      ""          | ""        || false
      null        | "80"      || false
      null        | ""        || false
    }
  }

  static class getRooms extends Specification {
    ChatworkClient client

    def setup(){
      String apiKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      String proxySv = "NOPROXY"
      String proxyPort = "80"
      client = new StubChatworkClient(apiKey, proxySv, proxyPort)
      client.path = "/rooms"
      client.response = readFixture("ChatWork_v1_GET_rooms.json")
    }

    def "getRooms should get response"(){
      setup:
      List<Room> expectedRooms = [
          new Room(roomId: "123", name: "Group Chat Name", type: "group"),
      ]

      expect:
      client.getRooms() == expectedRooms
    }
  }
}
