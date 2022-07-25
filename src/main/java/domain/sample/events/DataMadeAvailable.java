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
public class DataMadeAvailable implements SampleEvent {

  private final SampleCode sampleCode;
  private final Instant occurredOn;

  public static DataMadeAvailable create(SampleCode sampleCode, Instant occurredOn) {
    return new DataMadeAvailable(sampleCode, occurredOn);
  }

  public DataMadeAvailable(SampleCode sampleCode, Instant occurredOn) {
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
