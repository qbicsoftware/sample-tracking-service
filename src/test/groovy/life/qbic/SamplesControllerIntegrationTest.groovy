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
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class SamplesControllerIntegrationTest {

//  //  private static DBManager db
//  private static EmbeddedServer server
//  private static HttpClient client
//  private LocationsController locations
//
//  @Before
//  void setupMock() {
//    locations = new LocationsController(new QueryMock());
//  }
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
//    //    db = ApplicationContext.run(DBManager.class)
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
//
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
