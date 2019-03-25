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
import life.qbic.model.Contact
import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status
import spock.lang.Specification

import org.json.JSONObject
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import com.fasterxml.jackson.databind.ObjectMapper

class LocationsControllerIntegrationTest {
  private static DBTester db
  private static EmbeddedServer server
  private static HttpClient client


  @BeforeClass
  static void setupServer() {
    Map<String, String> env = System.getenv();
    //
    PropertySource source = PropertySource.of("test", CollectionUtils.mapOf(
        //            "app.db.host", env.get("TEST_MYSQL_HOST"),
        //            "app.db.port", env.get("TEST_MYSQL_PORT"),
        //            "app.db.name", env.get("TEST_MYSQL_DB"),
        //            "app.db.user", env.get("TEST_MYSQL_USER"),
        //            "app.db.pw", env.get("TEST_MYSQL_PW")
        //            ,"app.db.driver.class", "org.mariadb.jdbc.Driver",
        //            "app.db.driver.prefix", "jdbc:mariadb"
        ))
    
    //        db = new DBTester(env.get("TEST_MYSQL_HOST"),env.get("TEST_MYSQL_PORT"),env.get("TEST_MYSQL_DB"),env.get("TEST_MYSQL_USER"),env.get("TEST_MYSQL_PW"),"org.mariadb.jdbc.Driver","jdbc:mariadb")
    server = ApplicationContext.run(EmbeddedServer.class, source, "test")

    //    assertEquals(
    //    environment.getProperty("micronaut.server.host", String.class).orElse("localhost"),
    //    "foo"
    //    );

    //    server = ApplicationContext.run(EmbeddedServer.class)
    //    Environment environment = server.getEnvironment();
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
  //
  @Test
  void testNonExistingContact() throws Exception {
    HttpRequest request = HttpRequest.GET("/locations/contacts/ian.banks@limitingfactor.com")
    String error = "";
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage()
    }
    assertEquals(error, "Not Found")
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

    int personID = db.addPerson("Morat", first, last, email, "")
    int locationID = db.addLocationForPerson(affName, street, country, 0, personID)

    HttpRequest request = HttpRequest.GET("/locations/contacts/"+email)
    String body = client.toBlocking().retrieve(request)
    JSONObject json = new JSONObject(body);

    assertNotNull(body)
    assertEquals(json.get("full_name"),first+" "+last)
    assertEquals(json.get("email"),email)
    JSONObject address = json.get("address")
    assertEquals(address.get("affiliation"),affName)
    assertEquals(address.get("street"),street)
    assertEquals(address.get("zip_code"),zip)
    assertEquals(address.get("country"),country)

    db.removeLocationAndPerson(personID, locationID)
  }

  @Test
  void testMalformedContact() throws Exception {
    HttpRequest request = HttpRequest.GET("/locations/contacts/justreadtheinstructions")
    String error = ""
    try {
      HttpResponse response = client.toBlocking().exchange(request)
    } catch (HttpClientResponseException e) {
      error = e.getMessage()
    }
    assertEquals(error, "Bad Request")
  }

}
