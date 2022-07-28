package application;

import domain.notification.INotificationRepository;
import domain.notification.SampleStatusNotification;
import domain.sample.Sample;
import domain.sample.SampleCode;
import domain.sample.SampleEventPublisher;
import domain.sample.SampleRepository;
import domain.sample.Status;
import java.time.Instant;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * An application service to interact with samples.
 *
 * @since 2.0.0
 */
public class SampleService {

  private final SampleRepository sampleRepository;
  private final INotificationRepository notificationRepository;

  @Inject
  public SampleService(SampleRepository sampleRepository, INotificationRepository notificationRepository) {
    this.sampleRepository = sampleRepository;
    this.notificationRepository = notificationRepository;
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
    SampleEventPublisher eventPublisher = new SampleEventPublisher();
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.failQualityControl(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    SampleStatusNotification statusNotification = SampleStatusNotification.create(
        sample.sampleCode(), performAt, sample.currentState().status());
    notificationRepository.store(statusNotification);
  }
  public void prepareLibrary(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    runSampleCommand(code, it -> it.prepareLibrary(performAt));
  }
  public void provideData(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.provideData(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    SampleStatusNotification statusNotification = SampleStatusNotification.create(
        sample.sampleCode(), performAt, sample.currentState().status());
    notificationRepository.store(statusNotification);
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
