package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Parameter
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
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
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.db.MariaDBManager
import life.qbic.helpers.DBTester

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import javax.inject.Inject
import org.json.JSONObject
import org.junit.AfterClass
import org.junit.Before
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
    ApplicationContext ctx = ApplicationContext.run(EmbeddedServer.class)
    Environment environment = ctx.getEnvironment();

    String url = environment.getProperty("datasources.default.url", String.class).get()
    String user = environment.getProperty("datasources.default.username", String.class).get()
    String pw = environment.getProperty("datasources.default.password", String.class).get()
    String driver = environment.getProperty("datasources.default.driver-class-name", String.class).get()

    server = ctx

    db = new DBTester();
    db.loginWithCredentials(driver, url, user, pw);
    db.createTables()
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
