package life.qbic.application;

import life.qbic.domain.notification.INotificationRepository;
import life.qbic.domain.notification.SampleStatusNotification;
import life.qbic.domain.sample.Sample;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleRepository;
import life.qbic.domain.sample.Status;
import java.time.Instant;
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
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.registerMetadata(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    updateNotificationTable(performAt, sample);
  }
  public void receiveSample(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.receive(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    updateNotificationTable(performAt, sample);
  }
  public void passQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.passQualityControl(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    updateNotificationTable(performAt, sample);
  }
  public void failQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.failQualityControl(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    updateNotificationTable(performAt, sample);
  }
  public void prepareLibrary(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    sample.prepareLibrary(performAt);
    // store events
    sampleRepository.store(sample);
    // inform notification service
    updateNotificationTable(performAt, sample);
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
    updateNotificationTable(performAt, sample);
  }

  private void updateNotificationTable(Instant performAt, Sample sample) {
    SampleStatusNotification statusNotification = SampleStatusNotification.create(
        sample.sampleCode(), performAt, sample.currentState().status());
    notificationRepository.store(statusNotification);
  }
}
