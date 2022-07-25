package domain.sample.events;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class MetadataRegistered extends SampleEvent {


  public static MetadataRegistered create(SampleCode samplecode, Instant occurredOn) {
    return new MetadataRegistered(samplecode, occurredOn);
  }

  private MetadataRegistered(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

}
