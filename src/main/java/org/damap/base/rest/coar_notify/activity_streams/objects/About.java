package org.damap.base.rest.coar_notify.activity_streams.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;
import org.damap.base.rest.coar_notify.activity_streams.objects.items.Item;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class About extends ActivityStreamsObject {

    @JsonProperty("ietf:cite-as")
    private String citeAs;

    @JsonProperty("ietf:item")
    private Item item;

    @Override
    public List<EActivityStreamsType> getType() {
        return List.of(EActivityStreamsType.PAGE, EActivityStreamsType.ABOUT_PAGE);
    }
}
