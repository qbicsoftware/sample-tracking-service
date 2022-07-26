package domain.sample;

import java.io.Serializable;
import java.time.Instant;

/**
 * A domain event in the sample domain.
 */
public abstract class SampleEvent implements Serializable {
  private final SampleCode sampleCode;
  private final Instant occurredOn;

  protected SampleEvent(SampleCode sampleCode, Instant occurredOn) {
    this.sampleCode = sampleCode;
    this.occurredOn = occurredOn;
  }

  public SampleCode sampleCode() {
    return sampleCode;
  }

  public Instant occurredOn() {
    return occurredOn;
  }

  @Override
  public String toString() {
    return "SampleEvent{" +
        "sampleCode=" + sampleCode +
        ", occurredOn=" + occurredOn +
        '}';
  }
}
