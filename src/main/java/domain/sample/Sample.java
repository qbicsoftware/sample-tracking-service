package domain.sample;

import domain.InvalidDomainException;
import domain.sample.events.DataMadeAvailable;
import domain.sample.events.FailedQualityControl;
import domain.sample.events.LibraryPrepared;
import domain.sample.events.MetadataRegistered;
import domain.sample.events.PassedQualityControl;
import domain.sample.events.SampleReceived;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

  public SampleCode sampleCode() {
    return sampleCode;
  }

  public CurrentState currentState() {
    return currentState;
  }

  public static Sample create(SampleCode sampleCode) {
    return new Sample(sampleCode);
  }

  public static Sample fromEvents(Collection<SampleEvent> events) {
    if (events.isEmpty()) {
      throw new IllegalArgumentException(
          "Sample creation from events not possible without provided events.");
    }
    Optional<SampleCode> containedSampleCode =  events.stream().findAny().map(SampleEvent::sampleCode);
    SampleCode sampleCode = containedSampleCode.orElseThrow(() ->
        new InvalidDomainException("Could not identify sample code from events: " + events));
    Sample sample = new Sample(sampleCode);
    events.stream()
        .sorted(Comparator.comparing(SampleEvent::occurredOn))
        .forEachOrdered(sample::addEvent);
    return sample;
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
    SampleReceived event = SampleReceived.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void passQualityControl(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    PassedQualityControl event = PassedQualityControl.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void failQualityControl(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    FailedQualityControl event = FailedQualityControl.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void prepareLibrary(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
    }
    LibraryPrepared event = LibraryPrepared.create(sampleCode, occurredOn);
    addEvent(event);
  }

  public void provideData(Instant occurredOn) {
    if (notAfterLastModification(occurredOn)) {
      throw new InvalidDomainException(
          String.format("The sample (%s) was modified after %s", sampleCode, occurredOn));
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
    if (events.contains(event)) {
      return;
    }
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

  public static class CurrentState {

    private Status status;

    public Status status() {
      return status;
    }
  }

}