package life.qbic.domain.sample.events;

import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;

/**
 * A sample was received in the lab.
 *
 * @since 2.0.0
 */
public class SampleReceived extends SampleEvent {

  public static SampleReceived create(SampleCode sampleCode, Instant occurredOn) {
    return new SampleReceived(sampleCode, occurredOn);
  }

  private SampleReceived(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }
  @Override
  public Version version() {
    return Version.create(1, 0);
  }

  @Override
  public String toString() {
    return "SampleReceived{} " + super.toString();
  }
}
