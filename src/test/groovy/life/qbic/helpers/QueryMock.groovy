package life.qbic.helpers


import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.List
import java.util.regex.Matcher
import javax.inject.Inject
import javax.inject.Singleton

import life.qbic.datamodel.people.*
import life.qbic.datamodel.services.*
import life.qbic.datamodel.samples.*
import life.qbic.datamodel.people.Contact
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.samples.Status
import life.qbic.db.IQueryService

@Requires(missingBeans=javax.sql.DataSource)
class QueryMock implements IQueryService {

  void addNewLocation(String sampleId, Location location) {
    //no error is thrown
  }

  void updateLocation(String sampleId, Location location) {
    //no error is thrown
  }

  Contact searchPersonByEmail(String email) {
    String mail = "jernau@hassease.gv"
    if(mail.equals(email)) {
      Address adr = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
      return new Contact(fullName: "Jernau Gurgeh", email: mail, address: adr)
    }
    return null
  }

//  @Override
  public Sample searchSample(String sampleId) {
    String code = "QABCD001A0"
    if(sampleId.equals(code)) {
      
      Date d = new java.sql.Date(new Date().getTime());

      Address adr1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
      Address adr2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
      List<Location> pastLocs = new ArrayList<>()
      Location currLoc = new Location(name: "Current", responsiblePerson: "Current Person", address: adr1, status: Status.WAITING, arrivalDate: d, forwardDate: d)
      pastLocs.add(new Location(name: "Past", responsiblePerson: "Old Person", address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d))
      Sample res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)

      return res
    }
    return null
  }

//  @Override
  void updateSampleStatus(String sampleId, Status status) {
    //no error thrown
  }

  @Override
  List<Location> listLocations() {
    Date d = new java.sql.Date(new Date().getTime());
    
    Address adr1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
    Address adr2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
    List<Location> locs = new ArrayList<>()
    Location loc1 = new Location(name: "Current", responsiblePerson: "Current Person", address: adr1, status: Status.WAITING, arrivalDate: d, forwardDate: d)
    locs.add(new Location(name: "Past", responsiblePerson: "Old Person", address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d))
    locs.add(loc1)
    return locs;
  }

  @Override
  List<Location> getLocationsForEmail(String email) {
    Date d = new java.sql.Date(new Date().getTime());
    
    Address adr1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
    Address adr2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
    List<Location> locs = new ArrayList<>()
    Location loc1 = new Location(name: "Current", responsiblePerson: "Current Person", responsibleEmail: "wrong@wrong.de", address: adr1, status: Status.WAITING, arrivalDate: d, forwardDate: d)
    locs.add(new Location(name: "Past", responsiblePerson: "Old Person", responsibleEmail: "right@right.de", address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d))
    locs.add(loc1)
    
    List<Location> res = new ArrayList<Location>()
    for(Location l : locs) {
      if(l.responsibleEmail.equals(email))
        res.add(l)
    }
    return res;
  }

  /**
   * This mocked method provides some example locations for the given identifier
   *
   * @return an optional containing two example locations for a Dummy user with the provided identifier
   * @InheritDoc
   */
  @Override  List<Location> getLocationsForPerson(String identifier) {
    Date date = new java.sql.Date(new Date().getTime());
    String responsiblePerson = "Dummy"

    Address address1 = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
    Address address2 = new Address(affiliation: "QBiC", country: "Germany", street: "Morgenstelle 10", zipCode: 72076)
    Location location1 = new Location(name: "Test Location 1", responsiblePerson: responsiblePerson, responsibleEmail: identifier, address: address1, status: Status.WAITING, arrivalDate: date, forwardDate: date)

    List<Location> locations = new ArrayList<>()
    locations.add(location1)

    return locations;
  }
}