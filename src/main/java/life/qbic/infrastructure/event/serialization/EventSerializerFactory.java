package life.qbic.infrastructure.event.serialization;

import life.qbic.domain.sample.SampleEvent;

/**
 * Provides factory methods for event serialization.
 *
 * @since 2.0.0
 */
public class EventSerializerFactory {

  public static EventSerializer<SampleEvent> eventSerializer() {
    return new SampleEventSerializer();
  }
}
