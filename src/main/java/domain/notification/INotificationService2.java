package domain.notification;

import domain.sample.SampleCode;
import domain.sample.Status;
import java.time.Instant;

/**
 * Stores data related to the notification service used to notify about sample changes.
 *
 * @since 2.0.0
 */
public interface INotificationService2 {

  /**
   * This method stores a change to a specific sample.
   * <p><b>Please note:</b> The stored data is not retrievable for sample tracking purposes.</p>
   *
   * @param sampleCode   sample code identifying the sample this notification is about
   * @param sampleStatus the sample status
   * @param recordedAt the time for which the sample status was recorded
   * @since 2.0.0
   */
  void sampleChanged(SampleCode sampleCode, Status sampleStatus, Instant recordedAt);

}
