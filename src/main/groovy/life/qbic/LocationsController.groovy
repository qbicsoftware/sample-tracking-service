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

  private final DBManager manager

  @Inject LocationsController(DBManager manager) {
    this.manager = manager
  }

  @Get("/contacts/{email}")
  @Produces(MediaType.APPLICATION_JSON)
  HttpResponse<Contact> contacts(@Parameter('email') String email){
    if(!RegExValidator.isValidMail(email))
    {
      return HttpResponse.badRequest("Not a valid email address!");
    } else {
      Contact c = searchPersonByEmail(email);
      if(c!=null)
        return HttpResponse.ok(c);
      else
        return HttpResponse.notFound("Email address was not found in the system!");
    }
  }

  List<Contact> searchPersonByEmail(String email) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    List<Contact> foundContacts = new ArrayList<>()
    String sql = "SELECT * from persons WHERE UPPER(email) = UPPER(?)"
    manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
      statement.setString(1, email)
      statement.executeQuery().withCloseable { ResultSet resultSet ->
          while (resultSet.next()) {
              int id = resultSet.getInt("id");
              String firstName = resultSet.getString("first_name");
              String lastName = resultSet.getString("family_name");

              Address adr = getAddressByPerson(id);
              foundContacts.add(new Contact(fullName: firstName + " " + lastName, email: email, address: adr))
          }
          return foundContacts
      }
    }
  }

  Address getAddressByPerson(int personID) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Address res = null;
    String sql = "SELECT * from organizations inner join persons_organizations on organizations.id = persons_organizations.organization_id person_id = ?";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setInt(1, personID);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        //        logger.info("email found!");
        String affiliation = rs.getString("institute");
        String street = rs.getString("street");
        String country = rs.getString("country");
        int zip = rs.getInt("zip_code");

        res = new Address(affiliation: affiliation, street: street, zipCode: zip, country: country)
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }
}
