package life.qbic.domain.sample;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import life.qbic.domain.sample.events.DataMadeAvailable;
import life.qbic.domain.sample.events.FailedQualityControl;
import life.qbic.domain.sample.events.LibraryPrepared;
import life.qbic.domain.sample.events.MetadataRegistered;
import life.qbic.domain.sample.events.PassedQualityControl;
import life.qbic.domain.sample.events.SampleReceived;
import life.qbic.exception.UnrecoverableException;

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
    Optional<SampleCode> containedSampleCode = events.stream()
        .findAny()
        .map(SampleEvent::sampleCode);
    SampleCode sampleCode = containedSampleCode.orElseThrow(() ->
        new UnrecoverableException("Could not identify sample code from events: " + events));
    if (events.stream().anyMatch(it -> !it.sampleCode().equals(sampleCode))) {
      throw new UnrecoverableException(
          String.format("Not all events are of the same stream. Expected %s", sampleCode));
    }
    Sample sample = new Sample(sampleCode);
    events.stream()
        .sorted(Comparator.comparing(SampleEvent::occurredOn))
        .forEachOrdered(sample::handle);
    return sample;
  }

  public void registerMetadata(Instant occurredOn) {
    MetadataRegistered event = MetadataRegistered.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public void receive(Instant occurredOn) {
    SampleReceived event = SampleReceived.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public void passQualityControl(Instant occurredOn) {
    PassedQualityControl event = PassedQualityControl.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public void failQualityControl(Instant occurredOn) {
    FailedQualityControl event = FailedQualityControl.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public void prepareLibrary(Instant occurredOn) {
    LibraryPrepared event = LibraryPrepared.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public void provideData(Instant occurredOn) {
    DataMadeAvailable event = DataMadeAvailable.create(sampleCode, occurredOn);
    // put domain validation here e.g. if sample in state xyz can it change to abc?
    handle(event);
  }

  public <T extends SampleEvent> void handle(T event) {
    if (events.contains(event)) {
      return;
    }
    if (events.isEmpty()) {
      applyAndAdd(event);
      return;
    }
    findEventAt(event.occurredOn()).ifPresent(
        it -> {
          throw new UnrecoverableException(
              String.format(
                  "Modification conflict: The sample (%s) was modified at %s by %s. Modification with %s event at %s not possible.",
                  sampleCode,
                  it.occurredOn(),
                  it.getClass().getSimpleName(),
                  event.getClass().getSimpleName(),
                  event.occurredOn()));
        }
    );
    SampleEvent lastEvent = events.get(events.size() - 1);
    if (event.occurredOn().isBefore(lastEvent.occurredOn())) {
      adjustHistory(event);
    } else if (event.occurredOn().isAfter(lastEvent.occurredOn())) {
      applyAndAdd(event);
    }
  }

  private Optional<SampleEvent> findEventAt(Instant occurredOn) {
    return events.stream().filter(it -> it.occurredOn().equals(occurredOn)).findAny();
  }

  private <T extends SampleEvent> void adjustHistory(T event) {
    List<SampleEvent> eventsBeforeModification = events.stream()
        .filter(it -> it.occurredOn().isBefore(event.occurredOn())).collect(Collectors.toList());
    List<SampleEvent> eventsAfterModification = events.stream()
        .filter(it -> it.occurredOn().isAfter(event.occurredOn())).collect(Collectors.toList());
    this.events.clear();
    this.currentState.clear();
    eventsBeforeModification.forEach(this::applyAndAdd);
    applyAndAdd(event);
    eventsAfterModification.forEach(this::applyAndAdd);
  }


  private <T extends SampleEvent> void applyAndAdd(T event) {
    apply(event);
    events.add(event);
  }

  public <T extends SampleEvent> void apply(T event) {
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
      throw new UnrecoverableException("Unknown sample event: " + event.getClass().getName());
    }
  }

  /**
   * A history of events leading to the current state.
   *
   * @return all events leading to the current state of the sample
   * @since 2.0.0
   */
  public List<SampleEvent> events() {
    return Collections.unmodifiableList(events);
  }

  private void apply(MetadataRegistered event) {
    currentState.status = Status.METADATA_REGISTERED;
    currentState.statusValidSince = event.occurredOn();
  }

  private void apply(SampleReceived event) {
    currentState.status = Status.SAMPLE_RECEIVED;
    currentState.statusValidSince = event.occurredOn();
  }

  private void apply(FailedQualityControl event) {
    currentState.status = Status.SAMPLE_QC_FAILED;
    currentState.statusValidSince = event.occurredOn();
  }

  private void apply(PassedQualityControl event) {
    currentState.status = Status.SAMPLE_QC_PASSED;
    currentState.statusValidSince = event.occurredOn();
  }

  private void apply(LibraryPrepared event) {
    currentState.status = Status.LIBRARY_PREP_FINISHED;
    currentState.statusValidSince = event.occurredOn();
  }

  private void apply(DataMadeAvailable event) {
    currentState.status = Status.DATA_AVAILABLE;
    currentState.statusValidSince = event.occurredOn();
  }

  /**
   * Represents the current state of a sample.
   */
  public static class CurrentState {

    private Status status;
    private Instant statusValidSince;

    /**
     * The status the sample is in.
     *
     * @return the current status
     */
    public Status status() {
      return status;
    }

    /**
     * The instant from which the current state is valid from.
     *
     * @return the instant of this state
     */
    public Instant statusValidSince() {
      return statusValidSince;
    }

    private void clear() {
      this.status = null;
      this.statusValidSince = null;
    }
  }

}
