package org.damap.base.rest.coar_notify.activity_streams.relationship;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class Relationship extends ActivityStreamsObject {

  @JsonProperty("as:subject")
  private String subject;
  @JsonProperty("as:object")
  private String object;
  @Override
  public List<EActivityStreamsType> getType() {
    return List.of(EActivityStreamsType.RELATIONSHIP);
  }

  @JsonProperty("as:relationship")
  // hardcoded to one type
  public String getRelationship() {
    return "https://purl.org/vocab/relationship/Relationship";
  }
}
