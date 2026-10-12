package org.damap.base.rest.coar_notify.activity_streams;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StringStreamsObject extends ActivityStreamsObject {

    @JsonValue
    String value;

    @JsonCreator
    public StringStreamsObject(String value) {
        this.value = value;
    }
}
