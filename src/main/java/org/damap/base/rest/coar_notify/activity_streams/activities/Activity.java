package org.damap.base.rest.coar_notify.activity_streams.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;
import org.damap.base.rest.coar_notify.activity_streams.ObjectOrLink;
import org.damap.base.rest.coar_notify.activity_streams.actors.Person;
import org.damap.base.rest.coar_notify.activity_streams.actors.Service;
import org.damap.base.rest.coar_notify.activity_streams.objects.items.Document;
import org.damap.base.rest.coar_notify.activity_streams.relationship.Relationship;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = Activity.ActivityDeserializer.class)
public abstract class Activity extends ActivityStreamsObject {

  @JsonProperty("@context")
  private final List<String> jsonLdContext =
      List.of("https://www.w3.org/ns/activitystreams", "https://coar-notify.net");

  private Person actor;
  @JsonDeserialize(using = ActivityStreamsObjectDeserializer.class)
  private ActivityStreamsObject object;
  private Service target;
  private ObjectOrLink result;
  private Service origin;
  private ObjectOrLink instrument;
  private String inReplyTo;

  public static class ActivityDeserializer extends JsonDeserializer<Activity> {

    @Override
    public Activity deserialize(
            JsonParser parser,
            DeserializationContext context) throws IOException {

      ObjectMapper mapper = (ObjectMapper) parser.getCodec();
      JsonNode node = mapper.readTree(parser);

      JsonNode typeNode = node.get("type");

      if (typeNode != null) {
        String value = typeNode.toString();

        if (value.contains(EActivityStreamsType.REMOVE.getValue())) {
          return mapper.treeToValue(node, Remove.class);
        }

        if (value.contains(EActivityStreamsType.RELATIONSHIP_ACTION.getValue())) {
          return mapper.treeToValue(node, RelationshipAction.class);
        }

        if (value.contains(EActivityStreamsType.UPDATE.getValue())) {
          return mapper.treeToValue(node, Update.class);
        }

        if (value.contains(EActivityStreamsType.ANNOUNCE.getValue())) {
          return mapper.treeToValue(node, Announce.class);
        }
      }

      throw new JsonMappingException(parser, "Unknown Activity type");
    }
  }

  public static class ActivityStreamsObjectDeserializer
          extends StdDeserializer<ActivityStreamsObject> {

    public ActivityStreamsObjectDeserializer() {
      super(ActivityStreamsObject.class);
    }

    @Override
    public ActivityStreamsObject deserialize(
            JsonParser parser,
            DeserializationContext ctxt) throws IOException {

      ObjectMapper mapper = (ObjectMapper) parser.getCodec();
      JsonNode node = mapper.readTree(parser);

      JsonNode typeNode = node.get("type");

      if (typeNode != null) {

        String value = typeNode.toString();

        if (value.contains(EActivityStreamsType.RELATIONSHIP.getValue())) {
          return mapper.treeToValue(node, Relationship.class);
        }

        if (value.contains(EActivityStreamsType.DOCUMENT.getValue())) {
          return mapper.treeToValue(node, Document.class);
        }
      }

      throw JsonMappingException.from(
              parser,
              "Unknown ActivityStreamsObject type: " + typeNode);
    }
  }
}
