package life.qbic.api.rest.v2.samples

import life.qbic.domain.notification.INotificationRepository
import life.qbic.domain.notification.SampleStatusNotification

class NotificationRepoMock implements INotificationRepository {

    @Override
    void store(SampleStatusNotification statusNotification) {

    }
}
