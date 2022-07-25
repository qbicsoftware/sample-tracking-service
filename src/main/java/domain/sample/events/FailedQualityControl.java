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
public class FailedQualityControl extends SampleEvent {


  public static FailedQualityControl create(SampleCode sampleCode, Instant occurredOn) {
    return new FailedQualityControl(sampleCode, occurredOn);
  }

  private FailedQualityControl(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }
}
