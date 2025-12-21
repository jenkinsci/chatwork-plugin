package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Comparator;

public class RoomComparator implements Comparator<Room>, Serializable {
  @Override
  public int compare(Room room1, Room room2) {
    // 1st sort key
    if(!StringUtils.equals(room1.type, room2.type)){
      return room1.type.compareTo(room2.type);
    }

    // 2nd sort key
    String name1 = StringUtils.stripToEmpty(room1.name);
    String name2 = StringUtils.stripToEmpty(room2.name);
    return name1.compareTo(name2);
  }
}
