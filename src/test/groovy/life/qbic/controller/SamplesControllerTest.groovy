package life.qbic.controller

import io.micronaut.http.HttpResponse
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.IQueryService
import life.qbic.helpers.QueryMock
import life.qbic.service.ISampleService
import life.qbic.service.SampleServiceCenter
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class SamplesControllerTest {

  private SamplesController samples
  private String existingCode = "QABCD001A0";
  private String validMissingCode = "QABCD002A8";
  private String missingSampleCode = "QABCD002ME";

  @Before
  void setupMock() {
    IQueryService mock = new QueryMock()
    ISampleService serviceCenter = new SampleServiceCenter(mock)
    
    samples = new SamplesController(serviceCenter);
  }

  @Test
  void testMalformedSample() throws Exception {
    HttpResponse response = samples.sample("wrong")
    assertEquals(400, response.status.getCode())
  }

  @Test
  void testSample() throws Exception {
    HttpResponse response = samples.sample(existingCode);
    Sample s = response.body.orElse(null);
    assertEquals(s.code,existingCode)
    assertNotNull(s.currentLocation)
    assertNotNull(s.pastLocations)
  }

  @Test
  void testMissingSample() throws Exception {
    HttpResponse response = samples.sample(missingSampleCode);
    assertEquals(404, response.getStatus().getCode())
  }


  @Test
  void testStatusMalformedSample() throws Exception {
    HttpResponse response = samples.sampleStatus("", Status.PROCESSED)
    assertEquals(400, response.getStatus().getCode())
  }

  @Test
  void testStatus() throws Exception {
    HttpResponse response = samples.sampleStatus(existingCode, Status.PROCESSED)
    //fixme is this expected to be 200 or 201 (created)?
    assertEquals(201, response.getStatus().getCode())
  }

  @Test
  void testWrongStatus() throws Exception {
    HttpResponse response = samples.sampleStatus(existingCode, null)
    // error response is only thrown in integration test
  }

  @Test
  void testStatusNoSample() throws Exception {
    HttpResponse response = samples.sampleStatus(missingSampleCode, null)
    assertEquals(404, response.status.getCode())
  }

  @Test
  void testMalformedSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpResponse response = samples.newLocation("x", location)
    assertEquals(400, response.status.getCode())
  }

  // sample is unknown, but valid and is added to first location
  @Test
  void testMissingSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpResponse response = samples.newLocation(validMissingCode, location)
    assertEquals(200, response.status.getCode())
  }

  // sample exists and gets new location
  @Test
  void testExistingSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpResponse response = samples.newLocation(existingCode, location)
    assertEquals(200, response.status.getCode())
  }

  @Test
  void testUpdateLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    samples.updateLocation(validMissingCode, location)
    HttpResponse response = samples.sampleStatus(validMissingCode, null)
    assertEquals(404, response.status.getCode())
  }
}
