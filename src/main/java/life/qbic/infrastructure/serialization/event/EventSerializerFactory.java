package life.qbic.infrastructure.serialization.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import life.qbic.domain.sample.SampleEvent;
import life.qbic.domain.sample.events.Version;
import life.qbic.exception.UnRecoverableException;

/**
 * Provides factory methods for event serialization.
 *
 * @since 2.0.0
 */
public class EventSerializerFactory {

  public static EventSerializer<SampleEvent> eventSerializer() {
    return new SampleEventSerializer();
  }

  private static class SampleEventSerializer implements EventSerializer<SampleEvent> {
    private static final ObjectMapper objectMapper = configureObjectMapper();

    @Override
    public String serialize(SampleEvent sampleEvent) {
      try {
        return objectMapper.writeValueAsString(sampleEvent);
      } catch (JsonProcessingException e) {
        throw new UnRecoverableException(e);
      }
    }
    private static class JacksonSerializer extends JsonSerializer<SampleEvent> {
      @Override
      public void serialize(SampleEvent sampleEvent, JsonGenerator jsonGenerator, SerializerProvider serializers)
          throws IOException {
        Version version = sampleEvent.version();
        String className = sampleEvent.getClass().getName();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("className", className);
        jsonGenerator.writeStringField("version", version.toString());
        jsonGenerator.writeStringField("sampleCode", sampleEvent.sampleCode().toString());
        jsonGenerator.writeObjectField("occurredOn", sampleEvent.occurredOn());
        jsonGenerator.writeEndObject();
      }

    }

    private static class SampleEventModule extends SimpleModule {
      public SampleEventModule() {
        super();
        addSerializer(SampleEvent.class, new JacksonSerializer());
      }
    }
    private static ObjectMapper configureObjectMapper() {
      return new ObjectMapper()
          .registerModule(new SampleEventModule())
          .findAndRegisterModules()
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
  }
}
