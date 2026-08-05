package com.vexus2.jenkins.chatwork.jenkinschatworkplugin.api;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Room {
  @JsonProperty("room_id")
  public String roomId;

  @JsonProperty("name")
  public String name;

  @JsonProperty("type")
  public String type;

  @Override
  public int hashCode(){
    return Objects.hash(name, roomId, type);
  }

  @Override
  public boolean equals(final Object obj){
    if(obj instanceof Room){
      final Room other = (Room) obj;
      return Objects.equals(name, other.name)
          && Objects.equals(roomId, other.roomId)
          && Objects.equals(type, other.type);
    } else{
      return false;
    }
  }
}
