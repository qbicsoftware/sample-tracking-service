package domain.sample;

import domain.InvalidDomainException;
import domain.sample.events.DataMadeAvailable;
import domain.sample.events.FailedQualityControl;
import domain.sample.events.LibraryPrepared;
import domain.sample.events.MetadataRegistered;
import domain.sample.events.PassedQualityControl;
import domain.sample.events.SampleReceived;
import domain.sample.events.SampleSequenced;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>A sample in the context of sample-tracking.</p>
 *
 * @since 2.0.0
 */
public class Sample {

  private final SampleCode sampleCode;

  private final List<SampleEvent> events = new ArrayList<>();

  private final CurrentState currentState;

  private Sample(SampleCode sampleCode) {
    this.sampleCode = sampleCode;
    currentState = new CurrentState();
  }

  public static Sample create(SampleCode sampleCode) {
    return new Sample(sampleCode);
  }

  public static Sample fromEvents(SampleEvent... events) {
    SampleCode sampleCode = sampleCodeFromEvents(events);
    Sample sample = new Sample(sampleCode);
    for (SampleEvent event : events) {
      sample.addEvent(event);
    }
    return sample;
  }

  private static SampleCode sampleCodeFromEvents(SampleEvent[] events) {
    SampleCode sampleCode = events[0].sampleCode();
    if (Arrays.stream(events).anyMatch(it -> !it.sampleCode().equals(sampleCode))) {
      throw new InvalidDomainException(
          "All events need to be of one stream. Found events not matching stream id " + sampleCode);
    }
    return sampleCode;
  }

  public void registerMetadata(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    MetadataRegistered event = MetadataRegistered.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void receive(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.METADATA_REGISTERED)) {
      throw new InvalidDomainException(
          String.format(
              "Metadata must be registered before sample %s can be received in the lab.",
              sampleCode));
    }
    SampleReceived event = SampleReceived.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void passQualityControl(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.SAMPLE_RECEIVED)) {
      throw new InvalidDomainException(
          String.format(
              "Sample %s must be received at the lab before quality control can be performed.",
              sampleCode));
    }
    PassedQualityControl event = PassedQualityControl.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void failQualityControl(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.SAMPLE_RECEIVED)) {
      throw new InvalidDomainException(
          String.format(
              "Sample %s must be received at the lab before quality control can be performed.",
              sampleCode));
    }
    FailedQualityControl event = FailedQualityControl.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void prepareForSequencing(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.SAMPLE_QC_PASSED)) {
      throw new InvalidDomainException(
          String.format("Sample %s must pass quality control before a library is prepared.",
              sampleCode));
    }
    LibraryPrepared event = LibraryPrepared.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void sequence(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.LIBRARY_PREP_FINISHED) && !currentState.status.equals(
        Status.SAMPLE_QC_FAILED)) {
      throw new InvalidDomainException(
          String.format(
              "Sample %s did not pass quality control or had a library prepared to be sequenced.",
              sampleCode));
    }
    SampleSequenced event = SampleSequenced.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void provideData(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    if (!currentState.status.equals(Status.SEQUENCING_COMPLETED)) {
      throw new InvalidDomainException(
          String.format("Sample %s did not complete sequencing.", sampleCode));
    }
    DataMadeAvailable event = DataMadeAvailable.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public <T extends SampleEvent> void addEvent(T event) {
    apply(event);
    events.add(event);
  }

  private boolean notAfterLastModification(Instant occurredOn) {
    SampleEvent lastEvent = events.get(events.size() - 1);
    return lastEvent.occurredOn().isAfter(occurredOn);
  }

  private <T extends SampleEvent> void apply(T event) {
    if (event instanceof MetadataRegistered) {
      apply((MetadataRegistered) event);
    } else if (event instanceof SampleReceived) {
      apply((SampleReceived) event);
    } else if (event instanceof PassedQualityControl) {
      apply((PassedQualityControl) event);
    } else if (event instanceof FailedQualityControl) {
      apply((FailedQualityControl) event);
    } else if (event instanceof LibraryPrepared) {
      apply((LibraryPrepared) event);
    } else if (event instanceof SampleSequenced) {
      apply((SampleSequenced) event);
    } else if (event instanceof DataMadeAvailable) {
      apply((DataMadeAvailable) event);
    } else {
      throw new InvalidDomainException("Unknown sample event: " + event.getClass().getName());
    }
  }

  public List<SampleEvent> events() {
    return Collections.unmodifiableList(events);
  }

  private void apply(DataMadeAvailable event) {
    currentState.status = Status.DATA_AVAILABLE;
  }

  private void apply(FailedQualityControl event) {
    currentState.status = Status.SAMPLE_QC_FAILED;
  }

  private void apply(PassedQualityControl event) {
    currentState.status = Status.SAMPLE_QC_PASSED;
  }

  private void apply(LibraryPrepared event) {
    currentState.status = Status.LIBRARY_PREP_FINISHED;
  }

  private void apply(MetadataRegistered event) {
    currentState.status = Status.METADATA_REGISTERED;
  }

  private void apply(SampleReceived event) {
    currentState.status = Status.SAMPLE_RECEIVED;
  }

  private void apply(SampleSequenced event) {
    currentState.status = Status.SEQUENCING_COMPLETED;
  }

  private static class CurrentState {

    Status status;
  }

  /**
   * <p>A status a sample can have. It denotes a distinct state of the sample life-cycle.</p>
   */
  private enum Status {
    METADATA_REGISTERED,
    SAMPLE_RECEIVED,
    SAMPLE_QC_PASSED,
    SAMPLE_QC_FAILED,
    LIBRARY_PREP_FINISHED,
    SEQUENCING_COMPLETED,
    DATA_AVAILABLE
  }
}
