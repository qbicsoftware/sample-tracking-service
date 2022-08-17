package life.qbic.domain.notification;

import javax.inject.Singleton;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@Singleton
public interface INotificationRepository {

  void store(SampleStatusNotification statusNotification);

}
