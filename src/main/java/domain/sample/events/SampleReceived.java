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
public class SampleReceived extends SampleEvent {

  public static SampleReceived create(SampleCode sampleCode, Instant occurredOn) {
    return new SampleReceived(sampleCode, occurredOn);
  }

  private SampleReceived(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }
}
