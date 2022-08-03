package life.qbic.domain.sample.events;

import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;

/**
 * Whenever data is made available for a sample this domain event is fired.
 *
 * @since 2.0.0
 */
public class DataMadeAvailable extends SampleEvent {

  public static DataMadeAvailable create(SampleCode sampleCode, Instant occurredOn) {
    return new DataMadeAvailable(sampleCode, occurredOn);
  }

  public DataMadeAvailable(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

  @Override
  public String toString() {
    return "DataMadeAvailable{} " + super.toString();
  }
}
