package life.qbic

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import life.qbic.model.Address
import life.qbic.model.Contact

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Matcher
import javax.inject.Inject

@Controller("/locations")
class LocationsController {

  //@Inject
  //private final Connection userdbConnection

  private final QueryService dbService

  @Inject
  LocationsController(QueryService dbService) {
    this.dbService = dbService
  }

  @Get(uri = "/contacts/{email}", produces = MediaType.APPLICATION_JSON)
  HttpResponse<Contact> contacts(@Parameter('email') String email){
    if(!RegExValidator.isValidMail(email))
    {
      HttpResponse<Contact> res = HttpResponse.badRequest("Not a valid email address!")
      return res
    } else {
      Contact contact = dbService.searchPersonByEmail(email);
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
