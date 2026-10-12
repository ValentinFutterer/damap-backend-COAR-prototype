package org.damap.base.rest.coar_notify.activity_streams;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum EActivityStreamsType {
  OBJECT("Object"),
  LINK("Link"),
  DOCUMENT("sorg:DigitalDocument"),
  DATASET("sorg:Dataset"),
  PAGE("Page"),
  ABOUT_PAGE("sorg:AboutPage"),
  RELATIONSHIP("Relationship"),
  PERSON("Person"),
  SERVICE("Service"),
  ACTIVITY("Activity"),
  DELETE("Delete"),
  RELATIONSHIP_ACTION("http://coar-notify.net/specification/vocabulary/RelationshipAction"),
  REMOVE("Remove"),
  UPDATE("Update"),
  ANNOUNCE("Announce");

  private final String value;

  EActivityStreamsType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static EActivityStreamsType fromValue(String value) {
    return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst().orElseThrow();
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
