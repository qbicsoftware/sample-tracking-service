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

import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status

import org.json.JSONObject
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class MyControllerTest {

//  private static EmbeddedServer server
//  private static HttpClient client
//
//  @BeforeClass
//  static void setupServer() {
//    Map<String, String> env = System.getenv();
//
//    PropertySource source = PropertySource.of("test", CollectionUtils.mapOf(
//        //        "app.db.host", env.get("MYSQL_HOST"),
//        //        "app.db.port", env.get("MYSQL_PORT"),
//        //        "app.db.name", env.get("MYSQL_DB"),
//        //        "app.db.user", env.get("MYSQL_USER"),
//        //        "app.db.pw", env.get("MYSQL_PW")
//        //        ,"app.db.driver.class", "org.mariadb.jdbc.Driver",
//        //        "app.db.driver.prefix", "jdbc:mariadb"
//        ))
//    server = ApplicationContext.run(EmbeddedServer.class, source, "test")
//    //    Environment environment = server.getEnvironment();
//
//    //    assertEquals(
//    //    environment.getProperty("micronaut.server.host", String.class).orElse("localhost"),
//    //    "foo"
//    //    );
//
//    //    server = ApplicationContext.run(EmbeddedServer.class)
//    client = server
//        .getApplicationContext()
//        .createBean(HttpClient.class, server.getURL())
//  }
//
//  @AfterClass
//  static void stopServer() {
//    if (server != null) {
//      server.stop()
//    }
//    if (client != null) {
//      client.stop()
//    }
//  }
//
//  @Test
//  void testMalformedSample() throws Exception {
//    HttpRequest request = HttpRequest.GET("/samples/wrong")
//    String error = "";
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage();
//    }
//    assertEquals(error, "Bad Request")
//  }
//
//  @Test
//  void testSample() throws Exception {
//    String code = "QABCD001AB";
//    HttpRequest request = HttpRequest.GET("/samples/"+code)
//    String body = client.toBlocking().retrieve(request)
//    JSONObject json = new JSONObject(body);
//
//    assertNotNull(body)
//    assertEquals(json.get("code"),code)
//    assertNotNull(json.get("current_location"))
//    assertNotNull(json.get("past_locations"))
//  }
//
//  @Test
//  void testMissingSample() throws Exception {
//    HttpRequest request = HttpRequest.GET("/samples/QABCD001AX")
//    String error = "";
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage()
//    }
//    assertEquals(error, "Not Found")
//  }
//  //
//  //  @Post("/{sampleId}/currentLocation/")
//  //  HttpResponse newLocation(@Parameter('sampleId') String sampleId, Location location) {
//  //
//  //    Connection connection = manager.getConnection()
//  //    connection.setAutoCommit(false);
//  //
//  //    try {
//  //      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
//  //      int locationId = getLocationIdFromName(location.getName(), connection);
//  //      if(isNewSampleLocation(sampleId, location)) {
//  //        setNewLocationAsCurrent(sampleId, personId, locationId, location, connection)
//  //        addOrUpdateSample(sampleId, locationId, connection)
//  //        connection.commit()
//  //      }
//  //    } catch (Exception ex) {
//  //      ex.printStackTrace();
//  //      connection.rollback()
//  //    }
//  //    connection.setAutoCommit(true)
//  //
//  //    HttpResponse.created(new URI("/"+sampleId));
//  //  }
//  //
//  //  /**
//  //   * update or create location of a specific sample
//  //   * @param sampleId sample code from the URL
//  //   * @param location location object, transferred via json body
//  //   * @return
//  //   */
//  //  @Put("/{sampleId}/currentLocation/")
//  //  HttpResponse<Location> updateLocation(@Parameter('sampleId') String sampleId, Location location) {
//  //    HttpResponse<Location> response = HttpResponse.accepted();
//  //
//  //    Connection connection = manager.getConnection()
//  //    connection.setAutoCommit(false);
//  //    try {
//  //      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
//  //      int locationId = getLocationIdFromName(location.getName(), connection);
//  //
//  //      // if the location changed, change the location of the sample
//  //      if(isNewSampleLocation(sampleId, location)) {
//  //        response = setNewLocationAsCurrent(sampleId, personId, locationId, location, connection)
//  //      } else {
//  //        // else: update information about the sample at the current location (times, status, etc.)
//  //        updateCurrentLocationObjectInDB(sampleId, personId, locationId, location, connection)
//  //      }
//  //
//  //      // update sample table current location id OR create new row
//  //      addOrUpdateSample(sampleId, locationId, connection)
//  //
//  //      connection.commit();
//  //    } catch (Exception e) {
//  //      e.printStackTrace()
//  //      connection.rollback();
//  //    }
//  //    connection.setAutoCommit(true)
//  //    return response;
//  //  }
//
//
//  @Test
//  void testNonExistingContact() throws Exception {
//    HttpRequest request = HttpRequest.GET("/locations/contacts/ian.banks@limitingfactor.com")
//    String error = "";
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage()
//    }
//    assertEquals(error, "Not Found")
//  }
//
//  @Test
//  void testContact() throws Exception {
//    String email = "jernau@hassease.gv"
//    String first = "Jernau"
//    String last ="Gurgeh"
//    String affName = "Gevantsa"
//    String street = "Hassease"
//    int zip = 0
//    String country = "Chiark"
//
//    int personID = db.addPerson("Morat", first, last, email, "")
//    int locationID = db.addLocationForPerson(affName, street, country, 0, personID)
//
//    HttpRequest request = HttpRequest.GET("/locations/contacts/"+email)
//    String body = client.toBlocking().retrieve(request)
//    JSONObject json = new JSONObject(body);
//
//    assertNotNull(body)
//    assertEquals(json.get("full_name"),first+" "+last)
//    assertEquals(json.get("email"),email)
//    JSONObject address = json.get("address")
//    assertEquals(address.get("affiliation"),affName)
//    assertEquals(address.get("street"),street)
//    assertEquals(address.get("zip_code"),zip)
//    assertEquals(address.get("country"),country)
//
//    db.removeLocationAndPerson(personID, locationID)
//  }
//
//  @Test
//  void testMalformedContact() throws Exception {
//    HttpRequest request = HttpRequest.GET("/locations/contacts/justreadtheinstructions")
//    String error = ""
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage()
//    }
//    assertEquals(error, "Bad Request")
//  }
//
//  @Test
//  void testStatusMalformedSample() throws Exception {
//    HttpRequest request = HttpRequest.PUT("/samples/wrong/currentLocation/WAITING","")
//    String error = ""
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage()
//    }
//    assertEquals(error, "Bad Request")
//  }
//
//  @Test
//  void testStatus() throws Exception {
//    String code = "QABCD001AB";
//
//    HttpRequest request = HttpRequest.PUT("/samples/"+code+"/currentLocation/WAITING","")
//    String body = client.toBlocking().retrieve(request)
//    assertEquals(body, "Sample status updated.")
//
//    request = HttpRequest.GET("/samples/"+code)
//    body = client.toBlocking().retrieve(request)
//    JSONObject json = new JSONObject(body);
//    json = json.get("current_location")
//    assertEquals(json.get("sample_status"), Status.WAITING.toString());
//
//    HttpRequest.PUT("/samples/"+code+"/currentLocation/PROCESSED","")
//    body = client.toBlocking().retrieve(request)
//    assertEquals(body, "Sample status updated.")
//
//    println body
//    request = HttpRequest.GET("/samples/"+code)
//    body = client.toBlocking().retrieve(request)
//    json = new JSONObject(body);
//    json = json.get("current_location")
//    assertEquals(json.get("sample_status"), Status.PROCESSED.toString());
//  }
//
//  @Test
//  void testWrongStatus() throws Exception {
//    HttpRequest request = HttpRequest.PUT("/samples/QABCD001AB/currentLocation/TIRED","")
//    boolean res = false
//    try {
//      String body = client.toBlocking().retrieve(request)
//    } catch (HttpClientResponseException e) {
//      res = e.getMessage().contains("No enum constant")
//    }
//    assert(res)
//  }
//
//  @Test
//  void testStatusNoSample() throws Exception {
//    HttpRequest request = HttpRequest.PUT("/samples/QABCD001AX/currentLocation/WAITING","")
//    String error = ""
//    try {
//      HttpResponse response = client.toBlocking().exchange(request)
//    } catch (HttpClientResponseException e) {
//      error = e.getMessage()
//    }
//    assertEquals(error, "Not Found")
//  }

}
