package domain.sample.events;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;

/**
 * A sample passed quality control in the lab.
 *
 * @since 2.0.0
 */
public class PassedQualityControl extends SampleEvent {

  public static PassedQualityControl create(SampleCode sampleCode, Instant occurredOn) {
    return new PassedQualityControl(sampleCode, occurredOn);
  }

  private PassedQualityControl(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

  @Override
  public String toString() {
    return "PassedQualityControl{} " + super.toString();
  }
}
