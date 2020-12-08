package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import life.qbic.datamodel.services.*
import life.qbic.helpers.DBTester

import org.json.JSONObject
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import com.fasterxml.jackson.databind.ObjectMapper

class SamplesControllerIntegrationTest {

  private static DBTester db
  private static EmbeddedServer server;
  private static ObjectMapper mapper = new ObjectMapper();
  private static HttpClient client

  private static String existingLocation = "Existing Location"
  private static String existingPersonMail = "existing@mail.test"
  private String validCode1 = "QABCD001A0";
  private String validCode2 = "QABCD002A8";
  private String validCode3 = "QABCD003A4";
  private String validCode4 = "QABCD004AO";
  private String validCode5 = "QABCD005AW";
  private String validCode6 = "QABCD006A6";
  private String missingValidCode = "QABCD002ME";
  private String missingValidCode2 = "QAAAA001A8";

  @BeforeClass
  static void setupServer() {
    server = ApplicationContext.run(EmbeddedServer.class)
    ApplicationContext ctx = server.getApplicationContext()
    client = ctx
        .createBean(HttpClient.class, server.getURL())

    Environment environment = ctx.getEnvironment()
    String url = environment.getProperty("datasources.default.url", String.class).get()
    String user = environment.getProperty("datasources.default.username", String.class).get()
    String pw = environment.getProperty("datasources.default.password", String.class).get()
    String driver = environment.getProperty("datasources.default.driver-class-name", String.class).get()

    db = new DBTester();
    db.loginWithCredentials(driver, url, user, pw);

    db.createTables()
    db.addPerson("a", "b", "c", existingPersonMail)
    db.addLocation(existingLocation, "a", "b", 123)
  }

  @AfterClass
  static void stopServer() {
    db.dropTables()
    if (server != null) {
      server.stop()
    }
    if (client != null) {
      client.stop()
    }
  }

  @Test
  void testMalformedSample() throws Exception {
    HttpRequest request = HttpRequest.GET("/samples/wrong").basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      reason = e.getMessage()
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.BAD_REQUEST)
    assertEquals(reason, "Bad Request")
  }

  @Test
  void testAuthenticationRequired() throws Exception {
    HttpRequest request = HttpRequest.GET("/samples/" + validCode1)
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.UNAUTHORIZED)
  }

  @Test
  void testSample() throws Exception {
    String email1 = "person1@mail.de"
    String email2 = "person2@mail.de"
    String email3 = "person3@mail.de"

    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 3", "Person", email3)
    Address adr = new Address(affiliation: "Location 3", country: "Germany", street: "Location 3 street", zipCode: 3)
    Location currentLocation = new Location(name: "Location 3", responsiblePerson: "Location 3 Person", responsibleEmail: email3, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);

    List<Location> pastLocations = new ArrayList<>();
    List<Person> pastPersons = new ArrayList<>();
    pastPersons.add(new Person("Location 1", "Person", email1))
    pastPersons.add(new Person("Location 2", "Person", email2))
    Address adr1 = new Address(affiliation: "Location 1", country: "Germany", street: "Location 1 street", zipCode: 1)
    Location past1 = new Location(name: "Location 1", responsiblePerson: "Location 1 Person", responsibleEmail: email1, address: adr1, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    Address adr2 = new Address(affiliation: "Location 2", country: "Germany", street: "Location 2 street", zipCode: 2)
    Location past2 = new Location(name: "Location 2", responsiblePerson: "Location 2 Person", responsibleEmail: email2, address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    pastLocations.add(past1)
    pastLocations.add(past2)

    db.addSampleWithHistory(validCode1, currentLocation, currentPerson, pastLocations, pastPersons)

    HttpRequest request = HttpRequest.GET("/samples/"+validCode1).basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)

    Sample s = mapper.readValue(body, Sample.class);
    assertNotNull(s)
    assertEquals(s.code,validCode1)
    assertEquals(s.currentLocation,currentLocation)
    assertEquals(s.pastLocations,pastLocations)
  }

  @Test void testReadSample() {
    Date d = new java.sql.Date(new Date().getTime());

    String email1 = "person1@mail.de"
    Address adr1 = new Address(affiliation: "Location 1", country: "Germany", street: "Location 1 street", zipCode: 1)
    Location l1 = new Location(name: "Location 1", responsiblePerson: "Location 1 Person", responsibleEmail: email1, address: adr1, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    db.addSampleWithHistory("QTRAK005A9", l1, new Person("Location 1", "Person", email1), new ArrayList<Location>(), new ArrayList<Person>())


    for(int i = 0; i < 40;i++) {
      HttpRequest request = HttpRequest.GET("/samples/QTRAK005A9").basicAuth("servicewriter", "123456!")
      String body = client.toBlocking().retrieve(request)
      Sample s = mapper.readValue(body, Sample.class);
      assertNotNull(s)
      assertEquals(s.code,"QTRAK005A9")
      assertEquals(s.currentLocation,l1)
      assertEquals(s.pastLocations,null)
    }
  }

  @Test void testManyQueries() {
    List<String> codes = new ArrayList<>(Arrays.asList("QTRAK006AH","QTRAK007AP","QTRAK008AX","QTRAK009A7"))
    Date d = new java.sql.Date(new Date().getTime());
    String email1 = "person1@mail.de"
    String email2 = "person2@mail.de"
    Address adr1 = new Address(affiliation: "Location 1", country: "Germany", street: "Location 1 street", zipCode: 1)
    Location l1 = new Location(name: "Location 1", responsiblePerson: "Location 1 Person", responsibleEmail: email1, address: adr1, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    Address adr2 = new Address(affiliation: "Location 2", country: "Germany", street: "Location 2 street", zipCode: 2)
    Location l2 = new Location(name: "Location 2", responsiblePerson: "Location 2 Person", responsibleEmail: email2, address: adr2, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    db.addPerson("u1","Location 1", "Person",email1)
    db.addPerson("u2","Location 2", "Person",email2)
    db.addLocation(l1)
    db.addLocation(l2)

    for(String code : codes) {
      HttpRequest request = HttpRequest.POST("/samples/"+code+"/currentLocation/", l1).basicAuth("servicewriter", "123456!")
      HttpResponse response = client.toBlocking().exchange(request)
      assertEquals(response.status.getCode(), 200)

      Location testLocation = db.searchSample(code).currentLocation
      assertEquals(l1.address,testLocation.address)
      assertEquals(l1.name,testLocation.name)
      assertEquals(l1.status,testLocation.status)
      assertEquals(l1.responsiblePerson,testLocation.responsiblePerson)
      assertEquals(l1.responsibleEmail,testLocation.responsibleEmail)
    }

    for(String code : codes) {
      HttpRequest request = HttpRequest.GET("/samples/"+code).basicAuth("servicewriter", "123456!")
      String body = client.toBlocking().retrieve(request)
      Sample s = mapper.readValue(body, Sample.class);
      assertNotNull(s)
      assertEquals(s.code,code)
      assertEquals(l1.address,s.currentLocation.address)
      assertEquals(l1.name,s.currentLocation.name)
      assertEquals(l1.status,s.currentLocation.status)
      assertEquals(l1.responsiblePerson,s.currentLocation.responsiblePerson)
      assertEquals(l1.responsibleEmail,s.currentLocation.responsibleEmail)
      assertEquals(s.pastLocations,null)
    }

    for(String code : codes) {
      HttpRequest request = HttpRequest.POST("/samples/"+code+"/currentLocation/", l2).basicAuth("servicewriter", "123456!")
      HttpResponse response = client.toBlocking().exchange(request)
      assertEquals(response.status.getCode(), 200)

      Location testLocation = db.searchSample(code).currentLocation
      assertEquals(l2.address,testLocation.address)
      assertEquals(l2.name,testLocation.name)
      assertEquals(l2.status,testLocation.status)
      assertEquals(l2.responsiblePerson,testLocation.responsiblePerson)
      assertEquals(l2.responsibleEmail,testLocation.responsibleEmail)
    }

    for(String code : codes) {
      HttpRequest request = HttpRequest.GET("/samples/"+code).basicAuth("servicewriter", "123456!")
      String body = client.toBlocking().retrieve(request)
      Sample s = mapper.readValue(body, Sample.class);
      assertNotNull(s)
      assertEquals(s.code,code)
      assertEquals(l2.address,s.currentLocation.address)
      assertEquals(l2.name,s.currentLocation.name)
      assertEquals(l2.status,s.currentLocation.status)
      assertEquals(l2.responsiblePerson,s.currentLocation.responsiblePerson)
      assertEquals(l2.responsibleEmail,s.currentLocation.responsibleEmail)
      assertEquals(s.pastLocations.size(),1)
    }
  }

  @Test
  void testMissingSample() throws Exception {
    HttpRequest request = HttpRequest.GET("/samples/"+missingValidCode).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      reason = e.getMessage()
      status = e.getStatus()
    }
    assertEquals(reason, "Not Found")
    assertEquals(status, HttpStatus.NOT_FOUND)
  }


  @Test
  void testStatusMalformedSample() throws Exception {
    HttpRequest request = HttpRequest.PUT("/samples/wrong/currentLocation/WAITING","").basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
          
    reason = e.getMessage()
    status = e.getStatus()
    }
    assertEquals(reason, "Bad Request")
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }

  @Test
  void testStatus() throws Exception {
    String email = "person4@mail.de"

    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 4", "Person",email)
    Address adr = new Address(affiliation: "Location 4", country: "Germany", street: "Location 4 street", zipCode: 4)
    Location currentLocation = new Location(name: "Location 4", responsiblePerson: "Location 4 Person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);

    db.addSampleWithHistory(validCode2, currentLocation, currentPerson, new ArrayList<>(), new ArrayList<>())

    HttpRequest request = HttpRequest.PUT("/samples/"+validCode2+"/currentLocation/WAITING","").basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    assertEquals(body, "Sample status updated.")

    request = HttpRequest.GET("/samples/"+validCode2).basicAuth("servicewriter", "123456!")
    body = client.toBlocking().retrieve(request)
    JSONObject json = new JSONObject(body);
    json = json.get("current_location")
    assertEquals(json.get("sample_status"), Status.WAITING.toString());

    request = HttpRequest.PUT("/samples/"+validCode2+"/currentLocation/PROCESSED","").basicAuth("servicewriter", "123456!")
    body = client.toBlocking().retrieve(request)
    assertEquals(body, "Sample status updated.")

    request = HttpRequest.GET("/samples/"+validCode2).basicAuth("servicewriter", "123456!")
    body = client.toBlocking().retrieve(request)
    json = new JSONObject(body);
    json = json.get("current_location")
    assertEquals(json.get("sample_status"), Status.PROCESSED.toString());
  }

  @Test
  void testWrongStatus() throws Exception {
    String email = "person5@mail.de"

    Date d = new java.sql.Date(new Date().getTime());
    Person currentPerson = new Person("Location 5", "Person",email)
    Address adr = new Address(affiliation: "Location 5", country: "Germany", street: "Location 5 street", zipCode: 5)
    Location currentLocation = new Location(name: "Location 5", responsiblePerson: "Location 5 Person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);

    db.addSampleWithHistory(validCode3, currentLocation, currentPerson, new ArrayList<>(), new ArrayList<>())

    HttpRequest request = HttpRequest.PUT("/samples/"+validCode3+"/currentLocation/TIRED","").basicAuth("servicewriter", "123456!")
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
    String code = "QNONE001AC"
    HttpRequest request = HttpRequest.PUT("/samples/"+code+"/currentLocation/WAITING","").basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      reason = e.getMessage()
      status = e.getStatus()
    }
    assertEquals(reason, "Not Found")
    assertEquals(status, HttpStatus.NOT_FOUND)
  }

  @Test
  void testMalformedSampleNewLocation() throws Exception {
    String malformedCode = "QMALF001X";
    Date d = new java.sql.Date(new Date().getTime());
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 11", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: "some@person.de", address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);

    HttpRequest request = HttpRequest.POST("/samples/"+malformedCode+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      reason = e.getMessage()
      status = e.getStatus()
    }
    assertEquals(reason, "Bad Request")
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }


  @Test
  void testDBTesterAddSample() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "test@person.de"
    Person currentPerson = new Person("test", "person",email)
    Address adr = new Address(affiliation: "testloc", country: "Germany", street: "somestreet 5", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    int locID = db.addLocation(location.name, adr.street, adr.country, adr.zipCode)
    db.addSample("Testcode", locID)

    assert(db.findSample("Testcode", locID))
  }

  @Test
  void testNewLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "some@person.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 1", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    int locID = db.addLocation(location.name, adr.street, adr.country, adr.zipCode)
    db.addPerson("u", currentPerson.firstName, currentPerson.lastName, email)

    HttpRequest request = HttpRequest.POST("/samples/"+validCode4+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    HttpResponse response = client.toBlocking().exchange(request)
    assertEquals(response.status.getCode(), 200)
    Location testLocation = db.searchSample(validCode4).currentLocation
    assertEquals(location,testLocation)
  }

  @Test
  void testAddNewSample() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "some@person.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 1", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.METADATA_REGISTERED, arrivalDate: d);
    int locID = db.addLocation(location.name, adr.street, adr.country, adr.zipCode)
    db.addPerson("u", currentPerson.firstName, currentPerson.lastName, email)
    println location

    HttpRequest request = HttpRequest.POST("/samples/"+missingValidCode2+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    HttpResponse response = client.toBlocking().exchange(request)
    assertEquals(response.status.getCode(), 200)

    Location testLocation = db.searchSample(missingValidCode2).currentLocation
    assertEquals(location,testLocation)
  }

  @Test
  void testUpdateLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "some@person.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 1", zipCode: 213)
    Location location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    db.addSampleWithHistory(validCode5, location, currentPerson, new ArrayList<>(), new ArrayList<>())
    HttpRequest request = HttpRequest.PUT("/samples/"+validCode5+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    HttpResponse response = client.toBlocking().exchange(request)
    assertEquals(response.status.getCode(), 200)

    Location testLocation = db.searchSample(validCode5).currentLocation

    assertEquals(location,testLocation)

    d = new java.sql.Date(new Date().getTime());
    location = new Location(name: "locname", responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.PROCESSED, arrivalDate: d, forwardDate: d);
    request = HttpRequest.PUT("/samples/"+validCode5+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    response = client.toBlocking().exchange(request)
    assertEquals(response.status.getCode(), 200)

    testLocation = db.searchSample(validCode5).currentLocation
    assertEquals(location,testLocation)
  }

  @Test
  void testNewUnknownLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "some@person.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 2", zipCode: 213)
    Location location = new Location(name: "unknown", responsiblePerson: "some person", responsibleEmail: existingPersonMail, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpRequest request = HttpRequest.POST("/samples/"+validCode6+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {          
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }

  @Test
  void testUpdateUnknownLocation() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "some@person.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 3", zipCode: 213)
    Location location = new Location(name: "unknown", responsiblePerson: "some person", responsibleEmail: existingPersonMail, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpRequest request = HttpRequest.PUT("/samples/"+validCode6+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {          
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }

  @Test
  void testNewLocationUnknownUser() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "unknown@person1.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 4", zipCode: 213)
    Location location = new Location(name: existingLocation, responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpRequest request = HttpRequest.POST("/samples/"+validCode6+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }

  @Test
  void testUpdateLocationUnknownUser() throws Exception {
    Date d = new java.sql.Date(new Date().getTime());
    String email = "unknown@person2.de"
    Person currentPerson = new Person("some", "person",email)
    Address adr = new Address(affiliation: "locname", country: "Germany", street: "somestreet 5", zipCode: 213)
    Location location = new Location(name: existingLocation, responsiblePerson: "some person", responsibleEmail: email, address: adr, status: Status.WAITING, arrivalDate: d, forwardDate: d);
    HttpRequest request = HttpRequest.PUT("/samples/"+validCode6+"/currentLocation/", location).basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      status = e.getStatus()
    }
    assertEquals(status, HttpStatus.BAD_REQUEST)
  }
}
