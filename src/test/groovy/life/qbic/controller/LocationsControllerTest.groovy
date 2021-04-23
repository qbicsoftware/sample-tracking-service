package life.qbic.controller


import io.micronaut.http.HttpResponse
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.people.Contact
import life.qbic.datamodel.samples.Location
import life.qbic.helpers.QueryMock
import life.qbic.service.LocationServiceCenter
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class LocationsControllerTest {

  private LocationsController locations

  @Before
  void setupMock() {
    locations = new LocationsController(new LocationServiceCenter(new QueryMock()));
  }

  @Test
  void testNonExistingContact() throws Exception {
    HttpResponse<Contact> response = locations.contacts("ian.banks@limitingfactor.com")
    assertEquals(404, response.getStatus().getCode())
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
    assertEquals(response.getStatus().getCode(),200)
    Contact c = response.body.orElse(null)

    assertEquals(c.fullName, first+" "+last)
    assertEquals(c.email,email)
    Address address = c.getAddress()
    assertEquals(address.affiliation,affName)
    assertEquals(address.street,street)
    assertEquals(address.zipCode,zip)
    assertEquals(address.country,country)
  }

  @Test
  void testMalformedContact() throws Exception {
    HttpResponse response = locations.contacts("justreadtheinstructions")
    assertEquals(response.getStatus().getCode(), 400)
  }
  
  @Test
  void testLocationsMail() throws Exception {
    HttpResponse response = locations.locations("right@right.de")
    assertEquals(200, response.getStatus().getCode())
    List<Location> loc = response.body.orElse(null)
    assertEquals(1, loc.size())
  }
  
  @Test
  void testMalformedLocationsMail() throws Exception {
    HttpResponse response = locations.locations("justreadtheinstructions")
    assertEquals(response.getStatus().getCode(), 400)
  }
  
  @Test
  void testLocations() throws Exception {
    HttpResponse response = locations.listLocations()
    assertEquals(response.getStatus().getCode(), 200)
    List<Location> loc = response.body.orElse(null)
    assertEquals(loc.size(),2)
  }

}
