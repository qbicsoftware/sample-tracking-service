package life.qbic.controller

import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.INotificationService
import life.qbic.service.ISampleService
import spock.lang.Specification

/**
 * <b>Specifies when and where the INotificationService is to be called.</b>
 *
 * @since 1.2.0
 */
class INotificationServiceCalledSpec extends Specification {
    ISampleService sampleService = Stub ()
    INotificationService notificationService = Mock()
    def "SamplesController#newLocation calls the INotificationService"() {
        given: "a SamplesController with a notification service"
        SamplesController samplesController = new SamplesController(sampleService, notificationService)
        and: "a location and sample code"
        String sampleCode = "QSTTS001AT"
        Status status = Status.DATA_AVAILABLE
        Location location = new Location().status(status)
        when: "a new location is entered"
        samplesController.newLocation(sampleCode, location)
        then: "the INotificationService is called"
        1 * notificationService.sampleChanged(sampleCode, status)
    }

    def "SamplesController#updateLocation calls the INotificationService"() {
        given: "a SamplesController with a notification service"
        SamplesController samplesController = new SamplesController(sampleService, notificationService)
        and: "a location and sample code"
        String sampleCode = "QSTTS001AT"
        Status status = Status.DATA_AVAILABLE
        Location location = new Location().status(status)
        when: "a location is updated"
        samplesController.updateLocation(sampleCode, location)
        then: "the INotificationService is called"
        1 * notificationService.sampleChanged(sampleCode, status)
    }

    def "SamplesController#sampleStatus calls the INotificationService"() {
        given: "a SamplesController with a notification service"
        SamplesController samplesController = new SamplesController(sampleService, notificationService)
        and: "a sample status and sample code"
        String sampleCode = "QSTTS001AT"
        Status status = Status.DATA_AVAILABLE
        and: "a sample service returning a sample when checking for it"
        sampleService.searchSample(sampleCode) >> new Sample().code(sampleCode)
        when: "a sample status is updated"
        samplesController.sampleStatus(sampleCode, status)
        then: "the INotificationService is called"
        1 * notificationService.sampleChanged(sampleCode, status)
    }
}
