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

class SamplesControllerIntegrationTest {
  private static DBTester db
  private static EmbeddedServer server
  private static HttpClient client

  @BeforeClass
  static void setupServer() {
    String port = ""
    String prefix = "jdbc:hsqldb"
    String host = "mem:mymemdb;shutdown=true"
    String dbName = "test"
    String driver = "org.hsqldb.jdbc.JDBCDriver"

    //    String databaseURL = prefix + ":" + host + "/" + dbName;
    //    Class.forName(driver);
    //    DriverManager.getConnection(databaseURL, "bob", "");

    PropertySource source = PropertySource.of("test", CollectionUtils.mapOf(
        "app.db.host", host,
        "app.db.port", port,
        "app.db.name", dbName,
        "app.db.user", "bob",
        "app.db.pw", "",
        "app.db.driver.class", driver,
        "app.db.driver.prefix", prefix
        ))

    db = new DBTester(host, port, dbName, "bob", "", driver, prefix)
    db.createTables()
    server = ApplicationContext.run(EmbeddedServer.class, source, "test")
    client = server
        .getApplicationContext()
        .createBean(HttpClient.class, server.getURL())
  }

  @AfterClass
  static void stopServer() {
    if (server != null) {
      server.stop()
    }
    if (client != null) {
      client.stop()
    }
  }

  @Test
  void testMalformedSample() throws Exception {
    HttpRequest request = HttpRequest.GET("/samples/wrong")
    String error = "";
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage();
    }
    assertEquals(error, "Bad Request")
  }

  @Test
  void testSample() throws Exception {
    String code = "QABCD001AB";
    
    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 3", "Person")
    Address adr = new Address(affiliation: "Location 3", country: "Germany", street: "Location 3 street", zipCode: 3)
    Location currentLocation = new Location(name: "Location 3", responsiblePerson: "Location 3 Person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    
    List<Location> pastLocations = new ArrayList<>();
    List<Person> pastPersons = new ArrayList<>();
    pastPersons.add(new Person("Location 1", "Person"))
    pastPersons.add(new Person("Location 2", "Person"))
    Address adr1 = new Address(affiliation: "Location 1", country: "Germany", street: "Location 1 street", zipCode: 1)
    Location past1 = new Location(name: "Location 1", responsiblePerson: "Location 1 Person", address: adr1, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    Address adr2 = new Address(affiliation: "Location 2", country: "Germany", street: "Location 2 street", zipCode: 2)
    Location past2 = new Location(name: "Location 2", responsiblePerson: "Location 2 Person", address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    pastLocations.add(past1)
    pastLocations.add(past2)
    
    db.addSampleWithHistory(code, currentLocation, currentPerson, pastLocations, pastPersons)
    
    HttpRequest request = HttpRequest.GET("/samples/"+code)
    String body = client.toBlocking().retrieve(request)
    JSONObject json = new JSONObject(body);

    assertNotNull(body)
    assertEquals(json.get("code"),code)
    assertNotNull(json.get("current_location").equals(currentLocation))
    assertNotNull(json.get("past_locations").equals(pastLocations))
  }

  @Test
  void testMissingSample() throws Exception {
    HttpRequest request = HttpRequest.GET("/samples/QABCD001AX")
    String error = "";
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage()
    }
    assertEquals(error, "Not Found")
  }


  @Test
  void testStatusMalformedSample() throws Exception {
    HttpRequest request = HttpRequest.PUT("/samples/wrong/currentLocation/WAITING","")
    String error = ""
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage()
    }
    assertEquals(error, "Bad Request")
  }

  @Test
  void testStatus() throws Exception {
    String code = "QSTAT001AB";
    
    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 4", "Person")
    Address adr = new Address(affiliation: "Location 4", country: "Germany", street: "Location 4 street", zipCode: 4)
    Location currentLocation = new Location(name: "Location 4", responsiblePerson: "Location 4 Person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    
    db.addSampleWithHistory(code, currentLocation, currentPerson, new ArrayList<>(), new ArrayList<>())
    
    HttpRequest request = HttpRequest.PUT("/samples/"+code+"/currentLocation/WAITING","")
    String body = client.toBlocking().retrieve(request)
    assertEquals(body, "Sample status updated.")

    request = HttpRequest.GET("/samples/"+code)
    body = client.toBlocking().retrieve(request)
    JSONObject json = new JSONObject(body);
    json = json.get("current_location")
    assertEquals(json.get("sample_status"), Status.WAITING.toString());

    request = HttpRequest.PUT("/samples/"+code+"/currentLocation/PROCESSED","")
    body = client.toBlocking().retrieve(request)
    assertEquals(body, "Sample status updated.")

    request = HttpRequest.GET("/samples/"+code)
    body = client.toBlocking().retrieve(request)
    json = new JSONObject(body);
    json = json.get("current_location")
    assertEquals(json.get("sample_status"), Status.PROCESSED.toString());
  }

  @Test
  void testWrongStatus() throws Exception {
    String code = "QSTAT002AB";
    
    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 5", "Person")
    Address adr = new Address(affiliation: "Location 5", country: "Germany", street: "Location 5 street", zipCode: 5)
    Location currentLocation = new Location(name: "Location 5", responsiblePerson: "Location 5 Person", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    
    db.addSampleWithHistory(code, currentLocation, currentPerson, new ArrayList<>(), new ArrayList<>())
    
    HttpRequest request = HttpRequest.PUT("/samples/"+code+"/currentLocation/TIRED","")
    boolean res = false
    try {
      String body = client.toBlocking().retrieve(request)
    } catch (HttpClientResponseException e) {
      res = e.getMessage().contains("No enum constant")
    }
    assert(res)
  }

  @Test
  void testStatusNoSample() throws Exception {
    HttpRequest request = HttpRequest.PUT("/samples/QNONE001AX/currentLocation/WAITING","")
    String error = ""
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage()
    }
    assertEquals(error, "Not Found")
  }
}
