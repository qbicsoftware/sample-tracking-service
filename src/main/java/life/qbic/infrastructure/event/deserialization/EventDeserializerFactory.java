package life.qbic.infrastructure.event.deserialization;

import life.qbic.domain.sample.SampleEvent;

/**
 * Provides factory methods for event deserializers.
 *
 * @since 2.0.0
 */
public class EventDeserializerFactory {

  public static EventDeserializer<SampleEvent> sampleEventDeserializer() {
    return new SampleEventDeserializer();
  }
}
