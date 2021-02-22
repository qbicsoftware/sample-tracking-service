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
    assertEquals(response.status.getCode(), 400)
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
    assertEquals(response.getStatus().getCode(),404)
  }


  @Test
  void testStatusMalformedSample() throws Exception {
    HttpResponse response = samples.sampleStatus("", Status.PROCESSED)
    assertEquals(response.getStatus().getCode(), 400)
  }

  @Test
  void testStatus() throws Exception {
    HttpResponse response = samples.sampleStatus(existingCode, Status.PROCESSED)
    assertEquals(response.getStatus().getCode(), 201)
  }

  @Test
  void testWrongStatus() throws Exception {
    HttpResponse response = samples.sampleStatus(existingCode, null)
    // error response is only thrown in integration test
  }

  @Test
  void testStatusNoSample() throws Exception {
    HttpResponse response = samples.sampleStatus(missingSampleCode, null)
    assertEquals(response.status.getCode(), 404)
  }

  @Test
  void testMalformedSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpResponse response = samples.newLocation("x", location)
    assertEquals(response.status.getCode(), 400)
  }

  @Test
  void testSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpResponse response = samples.newLocation(validMissingCode, location)
    assertEquals(response.status.getCode(), 201)
  }

  @Test
  void testUpdateLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    samples.updateLocation(validMissingCode, location)
    HttpResponse response = samples.sampleStatus(validMissingCode, null)
    assertEquals(response.status.getCode(), 404)
  }
}
