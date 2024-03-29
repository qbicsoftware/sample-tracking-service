package life.qbic.application;

import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import life.qbic.domain.notification.INotificationRepository;
import life.qbic.domain.notification.SampleStatusNotification;
import life.qbic.domain.sample.Sample;
import life.qbic.domain.sample.Sample.CurrentState;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleRepository;
import life.qbic.exception.ErrorCode;
import life.qbic.exception.ErrorParameters;
import life.qbic.exception.UnrecoverableException;

/**
 * An application service to interact with samples.
 *
 * @since 2.0.0
 */
@Singleton
public class SampleService {

  private final SampleRepository sampleRepository;
  private final INotificationRepository notificationRepository;

  @Inject
  public SampleService(SampleRepository sampleRepository,
      INotificationRepository notificationRepository) {
    this.sampleRepository = sampleRepository;
    this.notificationRepository = notificationRepository;
  }

  public CurrentState getSampleState(String sampleCode) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Sample sample = sampleRepository.get(code).orElseThrow(() ->
        new UnrecoverableException(String.format("Sample %s was not found.", sampleCode),
            ErrorCode.SAMPLE_NOT_FOUND, ErrorParameters.create().with("sampleCode", sampleCode)));
    return sample.currentState();
  }

  public void registerMetadata(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.registerMetadata(performAt);
    // store events idempotent
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  public void receiveSample(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.receive(performAt);
    // store events
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  public void passQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.passQualityControl(performAt);
    // store events
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  public void failQualityControl(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.failQualityControl(performAt);
    // store events
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  public void prepareLibrary(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.prepareLibrary(performAt);
    // store events
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  public void provideData(String sampleCode, String validFrom) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Instant performAt = Instant.parse(validFrom);
    // restore the status
    Sample sample = sampleRepository.get(code).orElse(Sample.create(code));
    // run the command
    boolean wasSuccessful = sample.provideData(performAt);
    // store events
    sampleRepository.store(sample);
    if (wasSuccessful) {
      // inform notification service
      updateNotificationTable(performAt, sample);
    }
  }

  private void updateNotificationTable(Instant performAt, Sample sample) {
    SampleStatusNotification statusNotification = SampleStatusNotification.create(
        sample.sampleCode(), performAt, sample.currentState().status());
    notificationRepository.store(statusNotification);
  }
}
