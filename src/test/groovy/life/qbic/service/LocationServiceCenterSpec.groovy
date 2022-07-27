package life.qbic.service


import life.qbic.datamodel.people.Address
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Status
import life.qbic.db.IQueryService
import spock.lang.Specification

/**
 * Specifies how the LocationServiceCenter should behave
 *
 * @since: 1.1.0
 */
class LocationServiceCenterSpec extends Specification {


    def "GetLocationsForPerson returns locations from IQueryService"() {
        given: "a user identifier"
        String userId = "test@qbic.de"

        and: "a list of locations for the id"
        Date date = new Date(new Date().getTime())
        Address address1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
        Address address2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
        Location location1 = new Location(name: "Test Location 1", responsiblePerson: userId, responsibleEmail: "example1", address: address1, status: Status.WAITING, arrivalDate: date, forwardDate: date)
        Location location2 = new Location(name: "Test Location 2", responsiblePerson: userId, responsibleEmail: "example2", address: address2, status: Status.PROCESSED, arrivalDate: date, forwardDate: date)
        Location location3 = new Location(name: "Test Location 3", responsiblePerson: userId, responsibleEmail: "example3", address: address2, status: Status.PROCESSED, arrivalDate: date, forwardDate: date)

        List<Location> locations = [location1, location2, location3]

        and: "an IQueryService returning these locations"
        IQueryService queryService = Mock(IQueryService, {
            getLocationsForPerson(userId) >> locations
        })

        and: "a LocationServiceCenter with this queryService"
        ILocationService locationService = new LocationServiceCenter(queryService)

        when: "the LocationServiceCenter is asked to supply the locations for a given person"
        List<Location> result = locationService.getLocationsForPerson(userId)

        then: "the resulting list should contain all locations for the provided person"
        result.size() == 3
        and: "the content should be as expected"
        result == locations
    }
}
