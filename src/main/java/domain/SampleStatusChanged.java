package domain;

import java.time.Instant;

/**
 * <p>Describes the move of a sample to a new sample status.</p>
 */
class SampleStatusChanged implements SampleEvent {

  private final SampleCode sampleCode;
  private final Status status;
  private final Instant occurredOn;

  public SampleStatusChanged(SampleCode sampleCode, Status status, Instant occurredOn) {
    this.sampleCode = sampleCode;
    this.status = status;
    this.occurredOn = occurredOn;
  }

  /**
   * Creates a new event with the information provide.
   * @param sampleCode the code of the sample for which an update happened
   * @param status the new status that is reported
   * @param occurredOn the point at which the status changed
   * @return a new sample status changed event
   */
  public static SampleStatusChanged create(SampleCode sampleCode, Status status,
      Instant occurredOn) {
    return new SampleStatusChanged(sampleCode, status, occurredOn);
  }

  @Override
  public SampleCode sampleCode() {
    return sampleCode;
  }

  public Status status() {
    return status;
  }

  @Override
  public Instant occurredOn() {
    return occurredOn;
  }

}
