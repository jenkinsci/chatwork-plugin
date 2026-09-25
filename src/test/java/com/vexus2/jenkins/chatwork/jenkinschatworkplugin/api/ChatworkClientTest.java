package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatworkClientTest {
  @Test
  void sendsMessage() throws Exception {
    var client = new ChatworkClient("test-api-key", "NOPROXY", "80") {
      @Override
      protected void post(String path, Map<String, String> params) {
        assertEquals("/rooms/00000000/messages", path);
        assertEquals(Map.of("body", "testMessage"), params);
      }
    };
    client.sendMessage("00000000", "testMessage");
  }

  @Test
  void readsRooms() throws Exception {
    var client = new ChatworkClient("test-api-key", "NOPROXY", "80") {
      @Override
      protected String get(String path) throws IOException {
        assertEquals("/rooms", path);
        try (var stream = getClass().getResourceAsStream("../ChatWork_v1_GET_rooms.json")) {
          return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
      }
    };
    Room room = new Room();
    room.roomId = "123";
    room.name = "Group Chat Name";
    room.type = "group";
    assertEquals(List.of(room), client.getRooms());
  }

  static Stream<Arguments> proxies() {
    return Stream.of(
        Arguments.of("NOPROXY", "", false),
        Arguments.of("NOPROXY", "80", false),
        Arguments.of("localhost", "80", true),
        Arguments.of("localhost", "str", false),
        Arguments.of("localhost", "", false),
        Arguments.of("", "80", false),
        Arguments.of("localhost", " ", false),
        Arguments.of(" ", "80", false),
        Arguments.of("", "", false),
        Arguments.of(null, "80", false),
        Arguments.of(null, "", false));
  }

  @ParameterizedTest
  @MethodSource("proxies")
  void detectsProxy(String host, String port, boolean expected) {
    assertEquals(expected, new ChatworkClient("test-api-key", host, port).isEnabledProxy());
  }
}
