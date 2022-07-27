package domain.sample;

import java.io.Serializable;
import java.time.Instant;

/**
 * A domain event in the sample domain. Contains information on the sample it refers to as well as
 * the instant of occurrence.
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SampleEvent)) {
      return false;
    }

    SampleEvent that = (SampleEvent) o;

    if (!sampleCode.equals(that.sampleCode)) {
      return false;
    }
    return occurredOn.equals(that.occurredOn);
  }

  @Override
  public int hashCode() {
    int result = sampleCode.hashCode();
    result = 31 * result + occurredOn.hashCode();
    return result;
  }
}
