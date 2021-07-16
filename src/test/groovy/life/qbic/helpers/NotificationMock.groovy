package life.qbic.helpers

import life.qbic.datamodel.samples.Status
import life.qbic.db.INotificationService

/**
 * <b>Mocks {@link INotificationService}/b>
 *
 * <p>A mock for the notification service interface</p>
 *
 * @since 1.2.0
 */
class NotificationMock implements INotificationService{
    /**
     * This method stores a change to a specific sample.
     * <p><b>Please note:</b> The stored data is not retrievable for sample tracking purposes.</p>
     * @param sampleCode the sample code of the sample being changed
     * @param sampleStatus the sample status the samples was moved to
     * @since 1.2.0
     */
    @Override
    void sampleChanged(String sampleCode, Status sampleStatus) {
        //no error thrown
    }
}
