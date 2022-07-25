package application;

import domain.EventStore;
import domain.sample.Sample;
import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;
import java.util.SortedSet;
import java.util.function.Consumer;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class MoveSampleStatus {

  private final EventStore eventStore;

  public MoveSampleStatus(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public void moveSample(String sampleCode, String sampleStatus, String instant) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(instant);
    // restore the status
    SortedSet<SampleEvent> sampleEvents = eventStore.findForSample(code);
    Sample sample = Sample.create(code);
    sampleEvents.forEach(sample::addEvent);
    // run the command
    determineCommand(performAt, sampleStatus).accept(sample);
    // store events
    sample.events().forEach(eventStore::store);
  }

  private Consumer<Sample> determineCommand(Instant performAt, String sampleStatus) {
    switch (sampleStatus) {
      case "METADATA_REGISTERED":
        return sample -> sample.registerMetadata(performAt);
      case "SAMPLE_RECEIVED":
        return sample -> sample.receive(performAt);
      case "SAMPLE_QC_FAIL":
        return sample -> sample.failQualityControl(performAt);
      case "SAMPLE_QC_PASS":
        return sample -> sample.passQualityControl(performAt);
      case "LIBRARY_PREP_FINISHED":
        return sample -> sample.prepareForSequencing(performAt);
      case "SEQUENCING_COMPLETE":
        return sample -> sample.sequence(performAt);
      case "DATA_AVAILABLE":
        return sample -> sample.provideData(performAt);
    }
    throw new ApplicationException(String.format("Unknown action on status %s", sampleStatus));
  }

}
