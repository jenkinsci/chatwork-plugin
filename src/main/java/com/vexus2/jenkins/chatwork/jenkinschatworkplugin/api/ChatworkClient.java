package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatworkClient {

  private final String apiKey;

  private final String proxySv;
  private final String proxyPort;

  private static final String API_URL = "https://api.chatwork.com/v1";

  private static final CachedResponse<List<Room>> CACHED_ROOMS = new CachedResponse<List<Room>>();

  private final HttpClient httpClient = new HttpClient();

  public ChatworkClient(String apiKey, String proxySv, String proxyPort) {
    if (StringUtils.isBlank(apiKey)) {
      throw new IllegalArgumentException("API Key is blank");
    }

    this.apiKey = apiKey;
    this.proxySv = proxySv;
    this.proxyPort = proxyPort;
  }

  public void sendMessage(String roomId, String message) throws IOException {
    if (StringUtils.isEmpty(roomId)) {
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
    PostMethod method = new PostMethod(API_URL + path);

    try {
      method.addRequestHeader("X-ChatWorkToken", apiKey);
      method.addRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");

      for(Map.Entry<String, String> entry : params.entrySet()) {
        method.setParameter(entry.getKey(), entry.getValue());
      }

      if(isEnabledProxy()){
        setProxyHost(proxySv, Integer.parseInt(proxyPort));
      }

      int statusCode = httpClient.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        String response = method.getResponseBodyAsString();
        throw new ChatworkException("Response is not valid. Check your API Key or Chatwork API status. response_code = " + statusCode + ", message =" + response);
      }

    } finally {
      method.releaseConnection();
    }
  }

  protected String get(String path) throws IOException {
    GetMethod method = new GetMethod(API_URL + path);

    try {
      method.addRequestHeader("X-ChatWorkToken", apiKey);

      if(isEnabledProxy()){
        setProxyHost(proxySv, Integer.parseInt(proxyPort));
      }

      int statusCode = httpClient.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        String response = method.getResponseBodyAsString();
        throw new ChatworkException("Response is not valid. Check your API Key or Chatwork API status. response_code = " + statusCode + ", message =" + response);
      }

      return method.getResponseBodyAsString();

    } finally {
      method.releaseConnection();
    }
  }

  public boolean isEnabledProxy(){
    if(StringUtils.isBlank(proxySv) || StringUtils.isBlank(proxyPort) || StringUtils.equals(proxySv, "NOPROXY")){
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
    httpClient.getHostConfiguration().setProxyHost(new ProxyHost(hostname, port));
  }
}
