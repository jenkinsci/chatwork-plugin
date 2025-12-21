package com.vexus2.jenkins.chatwork.jenkinschatworkplugin;

public final class EscapeUtil {
    private EscapeUtil(){
    }

    public static String sanitize(String source){
        return source.replaceAll("[\\x00-\\x1f]+", "");
    }
}
