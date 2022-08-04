package life.qbic.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Status
import life.qbic.service.ILocationService
import spock.lang.Specification

/**
 * This class tests some methods of the LocationsController
 *
 *
 * @since: 1.1.0
 */
class LocationsControllerSpec extends Specification {


    def "The locations endpoint retuns a correct number of locations"() {
        given: "a user identifier"
        String exampleUId = "test@qbic.de"

        and: "a list of locations for these user identifiers"
        Date date = new Date(new Date().getTime());
        Address address1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
        Address address2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
        Location location1 = new Location(name: "Test Location 1", responsiblePerson: exampleUId, responsibleEmail: "example1", address: address1, status: Status.WAITING, arrivalDate: date, forwardDate: date)
        Location location2 = new Location(name: "Test Location 2", responsiblePerson: exampleUId, responsibleEmail: "example2", address: address2, status: Status.PROCESSING, arrivalDate: date, forwardDate: date)
        Location location3 = new Location(name: "Test Location 3", responsiblePerson: exampleUId, responsibleEmail: "example2", address: address2, status: Status.PROCESSED, arrivalDate: date, forwardDate: date)
        List<Location> locations = [location1, location2, location3]

        and: "an ILocationService returning these locations"
        ILocationService locationService = Mock(ILocationService, {
            getLocationsForPerson(exampleUId) >> locations
        })

        and: "a LocationsController (under test) with this location service"
        LocationsController locationsController = new LocationsController(locationService)

        when: "the LocationsController is accessed through the /{user_id} endpoint"
        HttpResponse response = locationsController.locations(exampleUId)

        then: "the HttpResponse succeeds"
        response.status() == HttpStatus.OK
        and: "there is a body returned"
        response.body.isPresent()
        and: "the response returns a list of locations"
        response.body() instanceof List<Location>
        and: "the list is as expected"
        response.body() == locations
    }
}
