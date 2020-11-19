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

import groovy.util.logging.Log4j2
import javax.annotation.Nullable
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import life.qbic.datamodel.services.Contact
import life.qbic.datamodel.services.Location
import life.qbic.service.ILocationService

@Log4j2
@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/locations")
class LocationsController {

  private final ILocationService locService

  @Inject
  LocationsController(ILocationService locService) {
    this.locService = locService
  }

  @Get(uri = "/contacts{?username,email}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<Contact> contacts(@Nullable String username, @Nullable String email){
    if(username && email) {
      log.warn("E-mail address and username received. Only e-mail address will be used to search for contact.");
    }
    if(email) {
      if(!RegExValidator.isValidMail(email)) {
        HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
        return res
      } else {
        Contact contact = locService.searchPersonByEmail(email)
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
    if(username) {
      Contact contact = locService.searchPersonByUsername(username)
      if(contact!=null) {
        HttpResponse<Contact> res = HttpResponse.ok(contact)
        return res
      }
      else {
        HttpResponse<Contact> res = HttpResponse.notFound("Username was not found in the system!")
        return res
      }
    }
    return HttpResponse.badRequest("E-mail address or username must be specified!")
  }

  @Get(uri = '{?username,email}', produces = MediaType.APPLICATION_JSON)
  @RolesAllowed(["READER", "WRITER"])
  HttpResponse<List<Location>> listLocations(@Nullable String username, @Nullable String email) {
    List<Location> res = new ArrayList<>()
    if(!username && !email) {
      res = locService.listLocations()
      return HttpResponse.ok(res)
    }
    if(username) {
      res.addAll(locService.getLocationsForUsername(username))
    }
    if(email) {
      if(!RegExValidator.isValidMail(email)) {
        return HttpResponse.badRequest("Not a valid email address!")
      }
      res.addAll(locService.getLocationsForEmail(email))
    }
    return HttpResponse.ok(res)
  }
}
