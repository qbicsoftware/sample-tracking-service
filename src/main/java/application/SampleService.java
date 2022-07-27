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

  public Status getSampleStatus(String sampleCode) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Sample sample = sampleRepository.get(code).orElseThrow(() -> new ApplicationException(
        String.format("Sample %s was not found.", sampleCode)));
    return sample.currentState().status();
  }

  public void registerMetadata(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.registerMetadata(performAt));
  }
  public void receiveSample(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.receive(performAt));
  }
  public void passQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.passQualityControl(performAt));
  }
  public void failQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.failQualityControl(performAt));
  }
  public void prepareLibrary(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.prepareLibrary(performAt));
  }
  public void provideData(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.provideData(performAt));
  }

  private void runSampleCommand(SampleCode sampleCode, Consumer<Sample> command) {
    // restore the status
    Sample sample = sampleRepository.get(sampleCode).orElse(Sample.create(sampleCode));
    // run the command
    command.accept(sample);
    // store events
    sampleRepository.store(sample);
  }
}
