package com.vexus2.jenkins.chatwork.jenkinschatworkplugin;

public class ChatworkPublisherBuilder {
  private String rid;

  private String defaultMessage;

  private String successMessage;
  private String failureMessage;
  private String unstableMessage;
  private String notBuiltMessage;
  private String abortedMessage;

  private Boolean notifyOnSuccess = false;
  private Boolean notifyOnFail = false;
  private Boolean notifyOnUnstable = false;
  private Boolean notifyOnNotBuilt = false;
  private Boolean notifyOnAborted = false;

  public ChatworkPublisherBuilder rid(String value){
    rid = value;
    return this;
  }

  public ChatworkPublisherBuilder defaultMessage(String value){
    defaultMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder successMessage(String value){
    successMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder failureMessage(String value){
    failureMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder unstableMessage(String value){
    unstableMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder notBuiltMessage(String value){
    notBuiltMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder abortedMessage(String value){
    abortedMessage = value;
    return this;
  }

  public ChatworkPublisherBuilder notifyOnSuccess(Boolean value){
    notifyOnSuccess = value;
    return this;
  }

  public ChatworkPublisherBuilder notifyOnFail(Boolean value){
    notifyOnFail = value;
    return this;
  }

  public ChatworkPublisherBuilder notifyOnUnstable(Boolean value){
    notifyOnUnstable = value;
    return this;
  }

  public ChatworkPublisherBuilder notifyOnNotBuilt(Boolean value){
    notifyOnSuccess = value;
    return this;
  }

  public ChatworkPublisherBuilder notifyOnAborted(Boolean value){
    notifyOnAborted = value;
    return this;
  }

  public ChatworkPublisher build(){
    return new ChatworkPublisher(rid, defaultMessage, notifyOnSuccess, notifyOnFail, unstableMessage, notBuiltMessage, abortedMessage, successMessage, failureMessage, notifyOnUnstable, notifyOnNotBuilt, notifyOnAborted);
  }

}
