package application;

import domain.sample.Sample;
import domain.sample.SampleCode;
import domain.sample.SampleRepository;
import domain.sample.Status;
import java.time.Instant;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleService {

  private final SampleRepository sampleRepository;

  @Inject
  public SampleService(SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  public void moveSample(String sampleCode, String sampleStatus, String instant) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(instant);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    determineCommand(performAt, sampleStatus).accept(sample);
    // store events
    sampleRepository.store(sample);
  }

  public Status getSampleStatus(String sampleCode) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Sample sample = sampleRepository.get(code).orElseThrow(() -> new ApplicationException(
        String.format("Sample %s was not found.", sampleCode)));
    return sample.currentState().status();
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
        return sample -> sample.prepareLibrary(performAt);
      case "DATA_AVAILABLE":
        return sample -> sample.provideData(performAt);
    }
    throw new ApplicationException(String.format("Unknown action on status %s", sampleStatus));
  }

}
