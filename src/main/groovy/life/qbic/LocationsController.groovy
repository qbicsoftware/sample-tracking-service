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

  //  Contact searchPersonByEmail(String email) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    Contact contact = null;
  //    String sql = "SELECT * from persons WHERE UPPER(email) = UPPER(?)"
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, email)
  //        statement.executeQuery().withCloseable { ResultSet resultSet ->
  //          if (resultSet.next()) {
  //            int id = resultSet.getInt("id");
  //            String firstName = resultSet.getString("first_name");
  //            String lastName = resultSet.getString("family_name");
  //
  //            Address adr = getAddressByPerson(id);
  //            contact = new Contact(fullName: firstName + " " + lastName, email: email, address: adr)
  //          }
  //        }
  //      }
  //    } catch (Exception e) {
  //      e.printStackTrace()
  //    }
  //    return contact
  //  }
  //
  //  Address getAddressByPerson(int personID) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    Address res = null;
  //    String sql = "SELECT * from locations inner join persons_locations on locations.id = persons_locations.location_id WHERE person_id = ?";
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setInt(1, personID);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            //        logger.info("email found!");
  //            String affiliation = rs.getString("name");
  //            String street = rs.getString("street");
  //            int zip = rs.getInt("zip_code");
  //            String country = rs.getString("country");
  //
  //            res = new Address(affiliation: affiliation, street: street, zipCode: zip, country: country)
  //          }
  //        }
  //      }
  //    } catch (Exception e) {
  //      e.printStackTrace()
  //    }
  //    return res
  //  }
}
