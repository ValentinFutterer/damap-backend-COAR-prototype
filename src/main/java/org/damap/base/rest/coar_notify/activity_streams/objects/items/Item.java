package org.damap.base.rest.coar_notify.activity_streams.objects.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;
import org.damap.base.rest.coar_notify.activity_streams.StringStreamsObject;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Item extends ActivityStreamsObject {

    private String mediaType;
    private StringStreamsObject context;
}
