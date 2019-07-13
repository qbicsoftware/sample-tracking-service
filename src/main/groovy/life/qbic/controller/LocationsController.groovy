package life.qbic.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse

import javax.inject.Inject
import life.qbic.datamodel.services.Contact
import life.qbic.service.ILocationService

@Controller("/locations")
class LocationsController {

  private final ILocationService locService

  @Inject
  LocationsController(ILocationService locService) {
    this.locService = locService
  }

  @Get(uri = "/contacts/{email}", produces = MediaType.APPLICATION_JSON)
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
}
