package org.damap.base.rest.coar_notify.activity_streams;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ObjectOrLink {

  private String name;

  @JsonProperty(value = "type", required = true, access = JsonProperty.Access.READ_ONLY)
  @JsonSerialize(using = SingleValueListSerializer.class)
  public abstract List<EActivityStreamsType> getType();

  private static class SingleValueListSerializer extends JsonSerializer<List<?>> {

    @Override
    public void serialize(
            List<?> value,
            JsonGenerator gen,
            SerializerProvider serializers) throws IOException {

      if (value.size() == 1) {
        gen.writeObject(value.get(0));
      } else {
        gen.writeObject(value);
      }
    }
  }
}
