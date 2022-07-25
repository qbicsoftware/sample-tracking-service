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
public class SampleSequenced implements SampleEvent {

  private final SampleCode sampleCode;
  private final Instant occurredOn;

  public static SampleSequenced create(SampleCode sampleCode, Instant occurredOn) {
    return new SampleSequenced(sampleCode, occurredOn);
  }

  public SampleSequenced(SampleCode sampleCode, Instant occurredOn) {
    this.sampleCode = sampleCode;
    this.occurredOn = occurredOn;
  }

  @Override
  public Instant occurredOn() {
    return occurredOn;
  }

  @Override
  public SampleCode sampleCode() {
    return sampleCode;
  }

}
