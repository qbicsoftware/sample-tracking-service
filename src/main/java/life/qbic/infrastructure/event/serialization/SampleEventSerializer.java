package life.qbic.infrastructure.event.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.time.Instant;
import life.qbic.domain.sample.SampleEvent;
import life.qbic.domain.sample.events.Version;
import life.qbic.exception.UnRecoverableException;

/**
 * Adapter for Jackson Json serialization
 *
 * @since 2.0.0
 */
class SampleEventSerializer implements EventSerializer<SampleEvent> {

  private static final ObjectMapper objectMapper = configureObjectMapper();

  @Override
  public String serialize(SampleEvent sampleEvent) {
    try {
      return objectMapper.writeValueAsString(sampleEvent);
    } catch (JsonProcessingException e) {
      throw new UnRecoverableException(e);
    }
  }

  /**
   * Implementation of Jackson serialization
   */
  private static class JacksonSerializer extends JsonSerializer<SampleEvent> {

    @Override
    public void serialize(SampleEvent sampleEvent, JsonGenerator jsonGenerator,
        SerializerProvider serializers)
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

  /**
   * Jackson module used to register the {@link JacksonSerializer} to the ObjectMapper
   */
  private static class SampleEventModule extends SimpleModule {

    public SampleEventModule() {
      super();
      addSerializer(SampleEvent.class, new JacksonSerializer());
    }
  }

  /**
   * Creates an {@link ObjectMapper} that is able to handle {@link Instant} and {@link SampleEvent} objects
   * @return a configured ObjectMapper
   */
  private static ObjectMapper configureObjectMapper() {
    return new ObjectMapper()
        .registerModule(new SampleEventModule())
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
