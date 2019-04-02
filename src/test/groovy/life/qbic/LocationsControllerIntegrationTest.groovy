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
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

import life.qbic.DBManager.DatabaseCredentials
import life.qbic.model.Contact
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

class LocationsControllerIntegrationTest {
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
