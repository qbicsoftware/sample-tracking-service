package domain.notification;

/**
 * Data source to access persistence for sample status notifications.
 * @since 2.0.0
 */
public interface SampleStatusNotificationDatasource {

  /**
   * Stores a notification to persistence.
   * @param notification the notification to store.
   */
  void store(SampleStatusNotification notification);

}
