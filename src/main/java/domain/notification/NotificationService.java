package domain.notification;

import domain.sample.SampleCode;
import domain.sample.Status;
import java.time.Instant;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class NotificationService implements INotificationService2 {

  private final SampleStatusNotificationDatasource sampleStatusNotificationDatasource;

  public NotificationService(SampleStatusNotificationDatasource sampleStatusNotificationDatasource) {
    this.sampleStatusNotificationDatasource = sampleStatusNotificationDatasource;
  }

  @Override
  public void sampleChanged(SampleCode sampleCode, Status sampleStatus, Instant recordedAt) {
    SampleStatusNotification statusNotification = SampleStatusNotification.create(sampleCode,
        recordedAt, sampleStatus);
    sampleStatusNotificationDatasource.store(statusNotification);
  }
}
