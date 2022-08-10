package life.qbic.domain.sample.events;

import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;

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
  public Version version() {
    return Version.create(1, 0);
  }

  @Override
  public String toString() {
    return "PassedQualityControl{} " + super.toString();
  }
}
