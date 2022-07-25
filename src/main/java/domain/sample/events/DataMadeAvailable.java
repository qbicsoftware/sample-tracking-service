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
public class DataMadeAvailable extends SampleEvent {

  public static DataMadeAvailable create(SampleCode sampleCode, Instant occurredOn) {
    return new DataMadeAvailable(sampleCode, occurredOn);
  }

  public DataMadeAvailable(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }

}
