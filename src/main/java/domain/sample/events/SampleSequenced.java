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
public class SampleSequenced extends SampleEvent {

  public static SampleSequenced create(SampleCode sampleCode, Instant occurredOn) {
    return new SampleSequenced(sampleCode, occurredOn);
  }

  private SampleSequenced(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

}
