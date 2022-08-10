package life.qbic.infrastructure.serialization.event;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;
import life.qbic.domain.sample.events.DataMadeAvailable;
import life.qbic.domain.sample.events.FailedQualityControl;
import life.qbic.domain.sample.events.LibraryPrepared;
import life.qbic.domain.sample.events.MetadataRegistered;
import life.qbic.domain.sample.events.PassedQualityControl;
import life.qbic.domain.sample.events.SampleReceived;
import life.qbic.exception.UnRecoverableException;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class EventDeserializerFactory {

  private static final ObjectMapper objectMapper = configureObjectMapper();

  public static <T extends SampleEvent> EventDeserializer<? extends SampleEvent> eventDeserializer(
      Class<T> clazz) {
    // decision on deserializer can be made based on clazz
    // currently all events contain the same information so a generalization can be made.
    return new SampleEventDeserializer();
  }

  private static class SampleEventDeserializer extends JsonDeserializer<SampleEvent> implements
      EventDeserializer<SampleEvent> {

    @Override
    public SampleEvent deserialize(String json) {
      try {
        return objectMapper.readValue(json, SampleEvent.class);
      } catch (JsonProcessingException e) {
        throw new UnRecoverableException(e);
      }
    }

    @Override
    public SampleEvent deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException, JacksonException {
      JsonNode rootNode = jsonParser.readValueAsTree();
      String className = rootNode.get("className").textValue();
      Instant occurredOn = ctxt.readTreeAsValue(rootNode.get("occurredOn"), Instant.class);
      SampleCode sampleCode = SampleCode.fromString(rootNode.get("sampleCode").textValue());
      return createEvent(className, occurredOn, sampleCode);
    }

    private static SampleEvent createEvent(String className, Instant occurredOn,
        SampleCode sampleCode) {
      if (MetadataRegistered.class.getName().equals(className)) {
        return MetadataRegistered.create(sampleCode, occurredOn);
      } else if (SampleReceived.class.getName().equals(className)) {
        return SampleReceived.create(sampleCode, occurredOn);
      } else if (FailedQualityControl.class.getName().equals(className)) {
        return FailedQualityControl.create(sampleCode, occurredOn);
      } else if (PassedQualityControl.class.getName().equals(className)) {
        return PassedQualityControl.create(sampleCode, occurredOn);
      } else if (LibraryPrepared.class.getName().equals(className)) {
        return LibraryPrepared.create(sampleCode, occurredOn);
      } else if (DataMadeAvailable.class.getName().equals(className)) {
        return DataMadeAvailable.create(sampleCode, occurredOn);
      } else {
        throw new UnRecoverableException(
            String.format("Event class %s not known.", className));
      }
    }
  }

  private static ObjectMapper configureObjectMapper() {
    return new ObjectMapper()
        .registerModule(new SampleEventModule())
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  private static class SampleEventModule extends SimpleModule {

    public SampleEventModule() {
      super();
      addDeserializer(SampleEvent.class, new SampleEventDeserializer());
    }
  }

}
