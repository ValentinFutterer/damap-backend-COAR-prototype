package org.damap.base.rest.coar_notify.activity_streams.actors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Actor extends ActivityStreamsObject {
  @JsonProperty(value = "inbox", required = true)
  private String inbox;
}
