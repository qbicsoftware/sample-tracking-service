package domain.sample.events;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;

/**
 * Metadata for a sample was registered in the system.
 *
 * @since 2.0.0
 */
public class MetadataRegistered extends SampleEvent {


  public static MetadataRegistered create(SampleCode samplecode, Instant occurredOn) {
    return new MetadataRegistered(samplecode, occurredOn);
  }

  private MetadataRegistered(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

  @Override
  public String toString() {
    return "MetadataRegistered{} " + super.toString();
  }
}
