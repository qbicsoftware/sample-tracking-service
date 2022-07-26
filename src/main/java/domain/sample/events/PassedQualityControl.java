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
