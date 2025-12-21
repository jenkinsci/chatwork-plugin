package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import org.apache.commons.lang.time.DateUtils;

import java.io.IOException;
import java.util.Date;

public class CachedResponse<T> {
  private T cachedResponse = null;

  private Date beforeTime = null;

  private final long expirationMilliseconds;

  public CachedResponse(){
    // default expiration is 5 minutes
    this.expirationMilliseconds = 5 * DateUtils.MILLIS_PER_MINUTE;
  }

  public CachedResponse(long expirationMilliseconds){
    this.expirationMilliseconds = expirationMilliseconds;
  }

  public T fetch(Callback<T> callback) throws IOException {
    if (isExpired()){
      cachedResponse = callback.get();
      beforeTime = new Date();
    }

    return cachedResponse;
  }

  private boolean isExpired(){
    if(beforeTime == null){
      return true;
    }

    long currentTime = new Date().getTime();
    return currentTime - beforeTime.getTime() > expirationMilliseconds;
  }

  public void clear(){
    cachedResponse = null;
    beforeTime = null;
  }

  public interface Callback<T>{
    T get() throws IOException;
  }
}
