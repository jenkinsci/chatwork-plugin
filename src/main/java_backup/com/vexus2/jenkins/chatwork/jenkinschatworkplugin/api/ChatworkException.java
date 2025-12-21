package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

public class ChatworkException extends RuntimeException{
    public ChatworkException(){
    }

    public ChatworkException(String message){
        super(message);
    }
}
