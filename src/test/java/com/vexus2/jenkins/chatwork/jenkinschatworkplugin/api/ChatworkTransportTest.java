package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatworkTransportTest {
  @Test
  void postsUtf8FormWithTokenAndProxy() throws Exception {
    try (var factory = mockStatic(HttpClients.class)) {
      var http = mock(CloseableHttpClient.class);
      var response = response(200, "{}");
      factory.when(HttpClients::createDefault).thenReturn(http);
      when(http.execute(any(HttpRequestBase.class))).thenReturn(response);

      var client = new ChatworkClient("test-api-key", "proxy.example", "8080");
      client.sendMessage("123", "日本語 &+=");

      var captured = ArgumentCaptor.forClass(HttpRequestBase.class);
      verify(http).execute(captured.capture());
      HttpPost request = assertInstanceOf(HttpPost.class, captured.getValue());
      assertEquals("https://api.chatwork.com/v2/rooms/123/messages", request.getURI().toString());
      assertEquals("test-api-key", request.getFirstHeader("X-ChatWorkToken").getValue());
      assertEquals("proxy.example", request.getConfig().getProxy().getHostName());
      assertEquals(8080, request.getConfig().getProxy().getPort());
      assertEquals("application/x-www-form-urlencoded; charset=UTF-8", request.getEntity().getContentType().getValue());
      assertEquals("body=%E6%97%A5%E6%9C%AC%E8%AA%9E+%26%2B%3D", EntityUtils.toString(request.getEntity()));
      verify(response).close();
      verify(http).close();
    }
  }

  @Test
  void getsUtf8BodyWithoutProxy() throws Exception {
    try (var factory = mockStatic(HttpClients.class)) {
      var http = mock(CloseableHttpClient.class);
      var response = response(200, "日本語");
      factory.when(HttpClients::createDefault).thenReturn(http);
      when(http.execute(any(HttpRequestBase.class))).thenReturn(response);

      assertEquals("日本語", new ChatworkClient("test-api-key", "NOPROXY", "").get("/rooms"));

      var captured = ArgumentCaptor.forClass(HttpRequestBase.class);
      verify(http).execute(captured.capture());
      var request = captured.getValue();
      assertEquals("GET", request.getMethod());
      assertEquals("https://api.chatwork.com/v2/rooms", request.getURI().toString());
      assertEquals("test-api-key", request.getFirstHeader("X-ChatWorkToken").getValue());
      assertNull(request.getConfig().getProxy());
      verify(response).close();
      verify(http).close();
    }
  }

  @Test
  void rejectsApiErrorsAndClosesResources() throws Exception {
    try (var factory = mockStatic(HttpClients.class)) {
      var http = mock(CloseableHttpClient.class);
      var response = response(429, "rate limit");
      factory.when(HttpClients::createDefault).thenReturn(http);
      when(http.execute(any(HttpRequestBase.class))).thenReturn(response);

      var error = assertThrows(ChatworkException.class,
          () -> new ChatworkClient("test-api-key", "NOPROXY", "").get("/rooms"));
      assertTrue(error.getMessage().contains("response_code = 429"));
      assertTrue(error.getMessage().contains("rate limit"));
      verify(response).close();
      verify(http).close();
    }
  }

  @Test
  void propagatesIoFailuresAndClosesClient() throws Exception {
    try (var factory = mockStatic(HttpClients.class)) {
      var http = mock(CloseableHttpClient.class);
      factory.when(HttpClients::createDefault).thenReturn(http);
      when(http.execute(any(HttpRequestBase.class))).thenThrow(new IOException("connection failed"));
      assertThrows(IOException.class,
          () -> new ChatworkClient("test-api-key", "NOPROXY", "").post("/rooms/123/messages", Map.of("body", "test")));
      verify(http).close();
    }
  }

  private static CloseableHttpResponse response(int status, String body) {
    var response = mock(CloseableHttpResponse.class);
    when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, status, ""));
    when(response.getEntity()).thenReturn(new StringEntity(body, StandardCharsets.UTF_8));
    return response;
  }
}
