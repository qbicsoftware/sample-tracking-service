package domain;

import static domain.Status.DATA_AVAILABLE;
import static domain.Status.LIBRARY_PREP_FINISHED;
import static domain.Status.METADATA_REGISTERED;
import static domain.Status.SAMPLE_QC_FAILED;
import static domain.Status.SAMPLE_QC_PASSED;
import static domain.Status.SAMPLE_RECEIVED;
import static domain.Status.SEQUENCING_COMPLETED;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A sample in the context of sample-tracking.</p>
 *
 * @since 2.0.0
 */
public class Sample {

  private final SampleCode sampleCode;
  private Status status;

  private Instant latestProcessedEvent = Instant.MIN;

  private final SampleEventPublisher eventPublisher;

  public Sample(SampleCode sampleCode, SampleEventPublisher eventPublisher) {
    this.sampleCode = sampleCode;
    this.eventPublisher = eventPublisher;
  }


  public void handleEvents(SampleEvent... sampleEvents) {
    for (SampleEvent sampleEvent : sampleEvents) {
      handleEvent(sampleEvent);
    }
  }

  public void handleEvent(SampleEvent sampleEvent) {
    if (sampleEvent.occurredOn().isBefore(latestProcessedEvent)) {
      return; // do only handle events newer than the latest
    }
    if (sampleEvent instanceof SampleStatusChanged) {
      handleSampleStatusChanged((SampleStatusChanged) sampleEvent);
    }
  }

  private void handleSampleStatusChanged(SampleStatusChanged sampleStatusChanged) {
    Status status = sampleStatusChanged.status();
    if (status.equals(this.status)) {
      return; // no move necessary
    }
    if (canMoveToStatus(status)) {
      this.status = status;
    }
    this.latestProcessedEvent = sampleStatusChanged.occurredOn();
  }


  /**
   * Moves the sample status to the provided status at the provided time.
   *
   * @param status    the status to move to
   * @param changedAt the instant the status went into effect.
   * @throws IllegalArgumentException in case the sample cannot move.
   */
  public void moveToStatus(Status status, Instant changedAt) {
    //assume the status entry to be the newest one
    if (!canMoveToStatus(status)) {
      throw new IllegalArgumentException(
          String.format("Status %s cannot follow on %s", status, this.status));
    }
    this.status = status;
    SampleStatusChanged sampleStatusChanged = SampleStatusChanged.create(sampleCode, this.status,
        changedAt);
    eventPublisher.publish(sampleStatusChanged);
  }


  /**
   * Holding a status as key with all its direct predecessors.
   */
  private static final Map<Status, Status> STATUS_AND_PREDECESSORS = initPredecessorMap();

  /**
   * Determines whether the sample can move to a specific status based on the samples current
   * status.
   *
   * @param status the status to move to
   * @return true if the move is valid; false otherwise
   */
  private boolean canMoveToStatus(Status status) {
    return STATUS_AND_PREDECESSORS.get(status).equals(this.status);
  }

  /**
   * @return a map containing a Status as key and direct predecessors as value.
   */
  private static Map<Status, Status> initPredecessorMap() {
    Map<Status, Status> predecessor = new HashMap<>();
    predecessor.put(SAMPLE_QC_PASSED, SAMPLE_RECEIVED);
    predecessor.put(SAMPLE_QC_FAILED, SAMPLE_RECEIVED);
    predecessor.put(LIBRARY_PREP_FINISHED, SAMPLE_QC_PASSED);
    predecessor.put(SEQUENCING_COMPLETED, LIBRARY_PREP_FINISHED);
    predecessor.put(METADATA_REGISTERED, SEQUENCING_COMPLETED);
    predecessor.put(DATA_AVAILABLE, METADATA_REGISTERED);

    return predecessor;
  }

}
