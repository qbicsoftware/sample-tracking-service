package life.qbic

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put
import life.qbic.model.Address
import life.qbic.model.Contact
import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Matcher
import javax.inject.Inject

class QueryMock implements QueryService {

  //  private final DBManager manager

  //  @Inject QueryMock(DBManager manager) {
  //    this.manager = manager
  //  }

  HttpResponse addNewLocation(String sampleId, Location location) {
    println sampleId
    println location
    HttpResponse<Location> response = HttpResponse.created(location)
    return response
  }

  HttpResponse<Location> updateLocation(String sampleId, Location location) {
    HttpResponse<Location> response = HttpResponse.accepted()
    return response
  }

  Contact searchPersonByEmail(String email) {
    String mail = "jernau@hassease.gv"
    if(mail.equals(email)) {
      Address adr = new Address(affiliation: "Gevantsa", country: "Chiark", street: "Hassease", zipCode: 0)
      return new Contact(fullName: "Jernau Gurgeh", email: mail, address: adr)
    }
    return null
  }

  @Override
  public Sample searchSample(String sampleId) {
    String code = "QABCD001AB"
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

  @Override
  public boolean updateSampleStatus(String sampleId, Status status) {
    return status!=null
  }
}