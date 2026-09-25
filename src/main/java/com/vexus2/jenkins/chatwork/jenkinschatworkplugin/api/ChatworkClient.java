package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatworkClient {

  private final String apiKey;

  private final String proxySv;
  private final String proxyPort;

  private static final String API_URL = "https://api.chatwork.com/v2";

  private static final CachedResponse<List<Room>> CACHED_ROOMS = new CachedResponse<List<Room>>();

  private HttpHost proxyHost;

  public ChatworkClient(String apiKey, String proxySv, String proxyPort) {
    if ((apiKey == null || apiKey.trim().isEmpty())) {
      throw new IllegalArgumentException("API Key is blank");
    }

    this.apiKey = apiKey;
    this.proxySv = proxySv;
    this.proxyPort = proxyPort;
  }

  public void sendMessage(String roomId, String message) throws IOException {
    if ((roomId == null || roomId.isEmpty())) {
      throw new IllegalArgumentException("Room ID is empty");
    }

    Map<String, String> params = new HashMap<String, String>();
    params.put("body", message);
    post("/rooms/" + roomId + "/messages", params);
  }

  public List<Room> getRooms() throws IOException {
    String json = get("/rooms");
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, new TypeReference<List<Room>>() {});
  }

  public List<Room> getCachedRooms() throws IOException {
    return CACHED_ROOMS.fetch(new CachedResponse.Callback<List<Room>>() {
      @Override
      public List<Room> get() throws IOException {
        return getRooms();
      }
    });
  }

  public static void clearRoomCache(){
    CACHED_ROOMS.clear();
  }

  protected void post(String path, Map<String, String> params) throws IOException {
    HttpPost request = new HttpPost(API_URL + path);
    request.setEntity(new UrlEncodedFormEntity(params.entrySet().stream()
        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList()), StandardCharsets.UTF_8));
    execute(request);
  }

  protected String get(String path) throws IOException {
    return execute(new HttpGet(API_URL + path));
  }

  private String execute(HttpRequestBase request) throws IOException {
    request.setHeader("X-ChatWorkToken", apiKey);
    if (isEnabledProxy()) {
      setProxyHost(proxySv, Integer.parseInt(proxyPort));
    }
    request.setConfig(RequestConfig.custom().setProxy(proxyHost).build());

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(request)) {
      int statusCode = response.getStatusLine().getStatusCode();
      String body = response.getEntity() == null ? ""
          : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      if (statusCode != HttpStatus.SC_OK) {
        throw new ChatworkException("Response is not valid. Check your API Key or Chatwork API status. response_code = " + statusCode + ", message =" + body);
      }
      return body;
    }
  }

  public boolean isEnabledProxy(){
    if((proxySv == null || proxySv.trim().isEmpty()) || (proxyPort == null || proxyPort.trim().isEmpty()) || Objects.equals(proxySv, "NOPROXY")){
      return false;
    }

    try {
      Integer.parseInt(proxyPort);

    } catch (NumberFormatException e){
      // proxyPort is not number
      return false;
    }

    return true;
  }

  public void setProxyHost(String hostname, int port){
    proxyHost = new HttpHost(hostname, port);
  }
}
