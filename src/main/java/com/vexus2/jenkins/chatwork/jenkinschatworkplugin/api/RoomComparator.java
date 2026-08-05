package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;


import java.util.Objects;
import java.io.Serializable;
import java.util.Comparator;

public class RoomComparator implements Comparator<Room>, Serializable {
  @Override
  public int compare(Room room1, Room room2) {
    // 1st sort key
    if(!Objects.equals(room1.type, room2.type)){
      return room1.type.compareTo(room2.type);
    }

    // 2nd sort key
    String name1 = (room1.name == null ? "" : room1.name.trim());
    String name2 = (room2.name == null ? "" : room2.name.trim());
    return name1.compareTo(name2);
  }
}
