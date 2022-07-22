package domain;

import java.time.Instant;

/**
 * <p>A sample in the context of sample-tracking.</p>
 *
 * @since 2.0.0
 */
public class Sample {

  private final SampleCode sampleCode;
  private Status status;

  private final SampleEventPublisher eventPublisher;

  public Sample(SampleCode sampleCode, SampleEventPublisher eventPublisher) {
    this.sampleCode = sampleCode;
    this.eventPublisher = eventPublisher;
  }

  public void handleEvent(SampleEvent sampleEvent) {
    if (sampleEvent instanceof SampleStatusChanged) {
      handleSampleStatusChanged((SampleStatusChanged) sampleEvent);
    }
  }

  private void handleSampleStatusChanged(SampleStatusChanged sampleStatusChanged) {
    Status status = sampleStatusChanged.status();
    if (!status.equals(this.status)) {
      if (status.canFollow(this.status)) {
        this.status = status;
      }
    }
  }


  /**
   * Moves the sample status to the provided status at the provided time.
   * @param status the status to move to
   * @param changedAt the instant the status went into effect.
   * @throws IllegalArgumentException in case the sample cannot move.
   */
  public void moveToStatus(Status status, Instant changedAt) {
    if (!status.canFollow(this.status)) {
      throw new IllegalArgumentException(
          String.format("Status %s cannot follow on %s", status, this.status));
    }
    this.status = status;
    SampleStatusChanged sampleStatusChanged = SampleStatusChanged.create(sampleCode, this.status,
        changedAt);
    eventPublisher.publish(sampleStatusChanged);
  }
}
