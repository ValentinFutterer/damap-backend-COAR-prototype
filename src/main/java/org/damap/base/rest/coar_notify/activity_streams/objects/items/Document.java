package org.damap.base.rest.coar_notify.activity_streams.objects.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class Document extends Item {

  @Override
  public List<EActivityStreamsType> getType() {
    return List.of(EActivityStreamsType.OBJECT, EActivityStreamsType.DOCUMENT);
  }
}
