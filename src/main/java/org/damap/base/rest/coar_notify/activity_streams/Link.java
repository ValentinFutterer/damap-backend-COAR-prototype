package org.damap.base.rest.coar_notify.activity_streams;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Link extends ObjectOrLink {
  @JsonProperty(value = "href", required = true)
  private String href;

  @Override
  public List<EActivityStreamsType> getType() {
    return List.of(EActivityStreamsType.LINK);
  }
}
