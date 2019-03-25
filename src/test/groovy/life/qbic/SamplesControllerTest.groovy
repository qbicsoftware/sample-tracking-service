package life.qbic

import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Parameter
import io.micronaut.context.env.Environment
import io.micronaut.context.env.PropertySource
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import life.qbic.model.Address
import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status

import org.json.JSONObject
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import com.fasterxml.jackson.databind.ObjectMapper

class SamplesControllerTest {

  private SamplesController samples

  @Before
  void setupMock() {
    samples = new SamplesController(new QueryMock());
  }

  @Test
  void testMalformedSample() throws Exception {
    HttpResponse response = samples.sample("wrong")
    assertEquals(response.status.getCode(), 400)
  }

  @Test
  void testSample() throws Exception {
    String code = "QABCD001AB";
    HttpResponse response = samples.sample(code);
    ObjectMapper mapper = new ObjectMapper();
    String jsons = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(response.body.orElse(null));
    JSONObject json = new JSONObject(jsons)
    assertEquals(json.get("code"),code)
    assertNotNull(json.get("current_location"))
    assertNotNull(json.get("past_locations"))
  }

  @Test
  void testMissingSample() throws Exception {
    String code = "QABCD001AB";
    HttpResponse response = samples.sample(code);
    assertEquals(response.getStatus().getCode(),200)    
  }


  @Test
  void testStatusMalformedSample() throws Exception {
    HttpResponse response = samples.sampleStatus("", Status.PROCESSED)
    assertEquals(response.getStatus().getCode(), 400)
  }

  @Test
  void testStatus() throws Exception {
    String code = "QABCD001AB";
    HttpResponse response = samples.sampleStatus(code, Status.PROCESSED)
    assertEquals(response.getStatus().getCode(), 201)
  }

  @Test
  void testWrongStatus() throws Exception {
    String code = "QABCD001AB";
    HttpResponse response = samples.sampleStatus(code, null)
    // error response is only thrown in integration test
  }

  @Test
  void testStatusNoSample() throws Exception {
    String code = "QABCD001AX";
    HttpResponse response = samples.sampleStatus(code, null)
    assertEquals(response.status.getCode(), 404)
  }
  
  @Test
  void testMalformedSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    String code = "QABCD001X";
    HttpResponse response = samples.newLocation(code, location)
    assertEquals(response.status.getCode(), 400)
  }
  
  @Test
  void testSampleNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    String code = "QABCD001BX";
    HttpResponse response = samples.newLocation(code, location)
    assertEquals(response.status.getCode(), 201)
  }
  
  @Test
  void testUpdateLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    String code = "QABCD001AX";
    samples.updateLocation(code, location)
    HttpResponse response = samples.sampleStatus(code, null)
    assertEquals(response.status.getCode(), 404)
  }
}
