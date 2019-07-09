package life.qbic.controller

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
import life.qbic.controller.LocationsController
import life.qbic.datamodel.services.Contact
import life.qbic.helpers.QueryMock
import life.qbic.service.LocationServiceCenter

import org.json.JSONObject
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import com.fasterxml.jackson.databind.ObjectMapper

class LocationsControllerTest {

  private LocationsController locations

  @Before
  void setupMock() {
    locations = new LocationsController(new LocationServiceCenter(new QueryMock()));
  }

  @Test
  void testNonExistingContact() throws Exception {
    HttpResponse<Contact> response = locations.contacts("ian.banks@limitingfactor.com")
    assertEquals(response.getStatus().getCode(),404)
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

    HttpResponse response = locations.contacts(email)
    ObjectMapper mapper = new ObjectMapper();
    String jsons = mapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(response.body.orElse(null));
    JSONObject json = new JSONObject(jsons)
    assertEquals(json.get("full_name"),first+" "+last)
    assertEquals(json.get("email"),email)
    JSONObject address = json.get("address")
    assertEquals(address.get("affiliation"),affName)
    assertEquals(address.get("street"),street)
    assertEquals(address.get("zip_code"),zip)
    assertEquals(address.get("country"),country)
  }

  @Test
  void testMalformedContact() throws Exception {
    HttpResponse response = locations.contacts("justreadtheinstructions")
    assertEquals(response.getStatus().getCode(), 400)
  }

}
