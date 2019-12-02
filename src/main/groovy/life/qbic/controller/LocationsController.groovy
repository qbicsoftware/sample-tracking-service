package life.qbic.controller

import io.micronaut.context.annotation.Requires
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import life.qbic.micronaututils.auth.Authentication

import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import life.qbic.datamodel.services.Contact
import life.qbic.datamodel.services.Location
import life.qbic.service.ILocationService

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/locations")
class LocationsController {

  private final ILocationService locService

  @Inject
  LocationsController(ILocationService locService) {
    this.locService = locService
  }

  @Get(uri = "/contacts/{email}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<Contact> contacts(@Parameter('email') String email){
    if(!RegExValidator.isValidMail(email)) {
      HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
      return res
    } else {
      Contact contact = locService.searchPersonByEmail(email);
      if(contact!=null) {
        HttpResponse<Contact> res = HttpResponse.ok(contact)
        return res
      }
      else {
        HttpResponse<Contact> res = HttpResponse.notFound("Email address was not found in the system!")
        return res
      }
    }
  }

  @Get(uri = "/{contact_email}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> locations(@Parameter('contact_email') String contact_email){
    if(!RegExValidator.isValidMail(contact_email)) {
      HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
      return res
    } else {
      List<Location> locations = locService.getLocationsForEmail(contact_email);
      return HttpResponse.ok(locations)
    }
  }

  @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> listLocations() {
    List<Location> res = locService.listLocations()
    return HttpResponse.ok(res)
  }
}
