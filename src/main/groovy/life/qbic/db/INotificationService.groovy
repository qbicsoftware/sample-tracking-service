package life.qbic.db

import life.qbic.datamodel.samples.Status

/**
 * <b>Stores data related to the notification service used to notify about sample changes</b>
 *
 * @since 1.2.0
 */
interface INotificationService {

    /**
     * This method stores a change to a specific sample.
     * <p><b>Please note:</b> The stored data is not retrievable for sample tracking purposes.</p>
     * @param sampleCode the sample code of the sample being changed
     * @param sampleStatus the sample status the samples was moved to
     * @since 1.2.0
     */
    void sampleChanged(String sampleCode, Status sampleStatus)
}