package org.damap.base.rest.coar_notify.activity_streams.actors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service extends Actor {
  @Override
  public List<EActivityStreamsType> getType() {
    return List.of(EActivityStreamsType.SERVICE);
  }
}
