package life.qbic.domain.sample.events;

import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;

/**
 * A sample failed quality control in the lab.
 *
 * @since 2.0.0
 */
public class FailedQualityControl extends SampleEvent {


  public static FailedQualityControl create(SampleCode sampleCode, Instant occurredOn) {
    return new FailedQualityControl(sampleCode, occurredOn);
  }

  private FailedQualityControl(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

  @Override
  public String toString() {
    return "FailedQualityControl{} " + super.toString();
  }
}
