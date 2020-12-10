package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import life.qbic.helpers.DBTester
import org.json.JSONArray
import org.json.JSONObject
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class LocationsControllerIntegrationTest {

  private static DBTester db
  private static EmbeddedServer server
  private static HttpClient client

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
  void testLocationsUserID() throws Exception {
    String user_id = "Morat"
    String email = "jernau@hassease.gv"
    String first = "Jernau"
    String last ="Gurgeh"
    String affName = "Gevantsa"
    String street = "Hassease"
    int zip = 0
    String country = "Chiark"

    int personID = db.addPerson(user_id, first, last, email)
    int locationID = db.addLocationForPerson(affName, street, country, 0, personID)

    HttpRequest request = HttpRequest.GET("/locations/"+user_id).basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    JSONArray arr = new JSONArray(body)
    assertEquals(1, arr.size())
    JSONObject json = arr.getJSONObject(0)

    assertEquals(affName, json.get("name"))

    db.removeLocationAndPerson(personID, locationID)
  }
  
  @Test
  void testLocationsForUnknownUser() throws Exception {
    HttpRequest request = HttpRequest.GET("/locations/justreadtheinstructions").basicAuth("servicewriter", "123456!")
    HttpStatus exceptionStatus
    HttpResponse response
    try {
      response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException responseException) {
      exceptionStatus = responseException.getStatus()
    }
    assertEquals(HttpStatus.BAD_REQUEST, exceptionStatus)
  }
  
  @Test
  void testLocationsForUserWithoutLocations() throws Exception {
    String user_id = "lonely"
    String first = "A"
    String last = "Hermit"
    String email = "hermit@bugmenot.no"
    int personID = db.addPerson(user_id, first, last, email)
    HttpRequest request = HttpRequest.GET("/locations/"+user_id).basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    JSONArray arr = new JSONArray(body)
    assertEquals(0, arr.size())
    
    db.removePerson(personID)
  }

  @Test
  void testLocations() throws Exception {
    List<String> locNames = Arrays.asList("loc1","loc2","loc3")
    int personID = db.addPerson("u1", "Paul", "Panther", "abc1@bla.de")
    int locationID = db.addLocationForPerson("loc1", "street 1", "germany", 0, personID)

    int personID2 = db.addPerson("u2", "Peter", "Parker", "abc2@bla.de")
    int locationID2 = db.addLocationForPerson("loc2", "street 2", "united kingdom", 0, personID2)

    int personID3 = db.addPerson("u3", "Markus", "Meier", "abc3@bla.de")
    int locationID3 = db.addLocationForPerson("loc3", "street 3", "france", 0, personID3)

    HttpRequest request = HttpRequest.GET("/locations/").basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    JSONArray arr = new JSONArray(body)
    println arr
    assertEquals(3, arr.size())
    for(int i = 0; i<arr.size();i++) {
      JSONObject json = arr.getJSONObject(i)
      assert(locNames.contains(json.get("name")))
    }

    db.removeLocationAndPerson(personID, locationID)
    db.removeLocationAndPerson(personID2, locationID2)
    db.removeLocationAndPerson(personID3, locationID3)
  }

  @Test
  void testEmptyLocations() throws Exception {
    HttpRequest request = HttpRequest.GET("/locations/").basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    JSONArray arr = new JSONArray(body)
    assert(arr.empty)
  }

  @Test
  void testNonExistingContact() throws Exception {
    String expectedReason = "Email address was not found in the system!"
    String reason
    HttpStatus status
    HttpRequest request = HttpRequest.GET("/locations/contacts/ian.banks@limitingfactor.com").basicAuth("servicewriter", "123456!")
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException responseException) {
      reason = responseException.getMessage()
      status = responseException.getStatus()
    }
    assertEquals(HttpStatus.NOT_FOUND, status)
    assertEquals(expectedReason, reason)
  }

  @Test
  void testContact() throws Exception {
    String email = "jernau@hassease.gv"
    String first = "Jernau"
    String last ="Gurgeh"
    String affName = "Gevantsa"
    String street = "Hassease"
    int zip = 0
    String country = "Chiark"

    int personID = db.addPerson("Morat", first, last, email)
    int locationID = db.addLocationForPerson(affName, street, country, 0, personID)

    HttpRequest request = HttpRequest.GET("/locations/contacts/" + email).basicAuth("servicewriter", "123456!")
    String body = client.toBlocking().retrieve(request)
    JSONObject json = new JSONObject(body);
    assertNotNull(body)
    assertEquals(first + " " + last, json.get("full_name"))
    assertEquals(email, json.get("email"))
    JSONObject address = json.get("address") as JSONObject
    assertEquals(affName, address.get("affiliation"))
    assertEquals(street, address.get("street"))
    assertEquals(zip, address.get("zip_code"))
    assertEquals(country, address.get("country"))

    db.removeLocationAndPerson(personID, locationID)
  }

  @Test
  void testMalformedContact() throws Exception {
    HttpRequest request = HttpRequest.GET("/locations/contacts/justreadtheinstructions").basicAuth("servicewriter", "123456!")
    String reason
    HttpStatus status
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      reason = e.getMessage()
      status = e.getStatus()
    }
    assertEquals("Not a valid email address!", reason)
    assertEquals(HttpStatus.BAD_REQUEST, status)
  }
}
