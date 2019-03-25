package life.qbic

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put
import life.qbic.model.Address
import life.qbic.model.Contact
import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Date

import javax.inject.Inject

@Controller("/samples")
class SamplesController {

  private final QueryService dbService

  @Inject
  SamplesController(QueryService dbService) {
    this.dbService = dbService
  }

  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
  HttpResponse<Sample> sample(@Parameter('sampleId') String code){
    if(!RegExValidator.isValidSampleCode(code)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      Sample s = dbService.searchSample(code);
      if(s!=null) {
        return HttpResponse.ok(s);
      } else {
        return HttpResponse.notFound("Sample was not found in the system!");
      }
    }
  }

  @Post("/{sampleId}/currentLocation/")
  HttpResponse<Location> newLocation(@Parameter('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      if(dbService.addNewLocation(sampleId, location)) {
        return HttpResponse.created(location)
      } else {
        return HttpResponse.badRequest("Unknown error.")
      }
    }
  }

  /**
   * update or create location of a specific sample
   * @param sampleId sample code from the URL
   * @param location location object, transferred via json body
   * @return
   */
  @Put("/{sampleId}/currentLocation/")
  HttpResponse<Location> updateLocation(@Parameter('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      return dbService.updateLocation(sampleId, location)
    }
  }

  //  private boolean isNewSampleLocation(String sampleId, Location location) {
  //    String locationIDQuery = "SELECT id FROM locations WHERE name = ?;"
  //    Connection connection = manager.getConnection()
  //    boolean res = true;
  //    try {
  //      connection.prepareStatement(locationIDQuery).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, location.getName());
  //
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            int id = rs.getInt("id");
  //
  //            statement.close()
  //            String currentLocationQuery = "SELECT * from samples_locations WHERE location_id = ? AND sample_id = ?";
  //            connection.prepareStatement(currentLocationQuery).withCloseable { PreparedStatement statement2 ->
  //              statement2.setInt(1, id);
  //              statement2.setString(2, sampleId);
  //
  //              statement2.executeQuery().withCloseable { ResultSet rs2 ->
  //                if (rs2.next()) {
  //                  res = false;
  //                }
  //              }
  //            }
  //          }
  //        }
  //      }
  //    } catch (Exception ex) {
  //      ex.printStackTrace();
  //    }
  //    return res;
  //  }
  //
  //  private void updateCurrentLocationObjectInDB(String sampleId, int personId, int locationId, Location location, Connection connection) {
  //    String sql = "UPDATE samples_locations SET arrival_time=?, forwarded_time=?, sample_status=?, responsible_person_id=? WHERE sample_id=? AND location_id=?"
  //    connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //      statement.setDate(1, location.getArrivalDate());
  //      statement.setDate(2, location.getforwardDate());
  //      statement.setString(3, location.getStatus().toString());
  //      statement.setInt(4, personId);
  //      statement.setString(5, sampleId);
  //      statement.setInt(6, locationId);
  //      statement.execute();
  //    }
  //  }
  //
  //  private HttpResponse setNewLocationAsCurrent(String sampleId, int personId, int locationId, Location location, Connection connection) {
  //    HttpResponse response = HttpResponse.accepted();
  //    String sql = "INSERT INTO samples_locations (sample_id, location_id, arrival_time, forwarded_time, sample_status, responsible_person_id) VALUES (?,?,?,?,?,?)"
  //    try {
  //      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, sampleId);
  //        statement.setInt(2, locationId);
  //        statement.setDate(3, location.getArrivalDate());
  //        statement.setDate(4, location.getforwardDate());
  //        statement.setString(5, location.getStatus().toString());
  //        statement.setInt(6, personId);
  //        statement.execute();
  //      }
  //      //                logger.error("Project has been successfully added to all related tables.");
  //      //                success = true;
  //    } catch (Exception ex) {
  //      ex.printStackTrace();
  //      //                logger.error("Exception occured while adding project. Rolling back.");
  //      connection.rollback();
  //      response = HttpResponse.badRequest();
  //    }
  //    return response;
  //  }
  //
  //  private void addOrUpdateSample(String sampleId, int locationId, Connection connection) {
  //    String search = "SELECT * FROM samples where id = ?"
  //    try{
  //      connection.prepareStatement(search).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, sampleId);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if(!rs.next()) {
  //            String create = "INSERT into samples (id, current_location_id) VALUES(?,?)"
  //            connection.prepareStatement(create).withCloseable { PreparedStatement statement2 ->
  //              statement2.setString(1, sampleId);
  //              statement2.setInt(2, locationId);
  //              statement2.execute();
  //            }
  //          } else {
  //            String update = "UPDATE samples SET current_location_id = ? WHERE id = ?"
  //            connection.prepareStatement(update).withCloseable { PreparedStatement statement3 ->
  //              statement3.setInt(1, locationId);
  //              statement3.setString(2, sampleId);
  //              statement3.execute();
  //            }
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //  }

  @Put("/{sampleId}/currentLocation/{status}")
  HttpResponse sampleStatus(@Parameter('sampleId') String sampleId, @Parameter('status') Status status) {
    if(!RegExValidator.isValidSampleCode(sampleId))
    {
      return HttpResponse.badRequest("Not a valid sample code!");
    }
    boolean found = dbService.searchSample(sampleId)!=null;
    if(found) {
      dbService.updateSampleStatus(sampleId, status);
      return HttpResponse.created("Sample status updated.");
    } else {
      return HttpResponse.notFound("Sample was not found in the system!");
    }
  }
  //
  //  private Sample searchSample(String code) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    Sample res = null;
  //    String sql = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
  //        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
  //        "WHERE UPPER(samples.id) = UPPER(?)";
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, code);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          List<Location> pastLocs = new ArrayList<>()
  //          Location currLoc = null;
  //          while (rs.next()) {
  //            int currID = rs.getInt("current_location_id");
  //            int locID = rs.getInt("location_id");
  //            Date arrivalDate = rs.getDate("arrival_time");
  //            Date forwardedDate = rs.getDate("forwarded_time");
  //            Status status = rs.getString("sample_status");
  //            String name = rs.getString("name");
  //            String street = rs.getString("street");
  //            String country = rs.getString("country");
  //            int zip = rs.getInt("zip_code");
  //
  //            Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
  //            int personID = rs.getInt("responsible_person_id");
  //            String responsiblePerson = getPersonNameByID(personID)
  //
  //            if(currID == locID) {
  //              currLoc = new Location(name: name, responsiblePerson: responsiblePerson, address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
  //            } else {
  //              pastLocs.add(new Location(name: name, responsiblePerson: responsiblePerson, address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
  //            }
  //          }
  //          if(currLoc!=null) {
  //            res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //    return res
  //  }
  //
  //  private String getPersonNameByID(int id) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    String res = null;
  //    String sql = "SELECT * from persons WHERE id = ?";
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setInt(1, id);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            //        logger.info("email found!");
  //            String firstName = rs.getString("first_name")
  //            String lastName = rs.getString("family_name")
  //            res = firstName+" "+lastName
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //    return res
  //  }
  //
  //  private boolean updateSampleStatus(String sampleId, Status status) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    boolean res = false;
  //    String sql = "SELECT * from samples WHERE UPPER(id) = UPPER(?)";
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, sampleId);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            int locationID = rs.getInt("current_location_ID");
  //            //        String oldStatus = getStatus(sampleID, locationID);
  //            //        int currIndex = stati.indexOf(oldStatus);
  //            //        if(currIndex+1 < stati.size()) {
  //            setStatus(sampleId, locationID, status);
  //            res = true;
  //            //        }
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //    return res
  //  }
  //
  //  private void setStatus(String sampleID, int locationID, Status status) {
  //    String sql = "UPDATE samples_locations SET sample_status = ? where sample_id = ? and location_id = ?";
  //    try {
  //      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, status.toString());
  //        statement.setString(2, sampleID);
  //        statement.setInt(3, locationID);
  //        statement.execute();
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //  }
  //
  //  private int getLocationIdFromName(String locationName, Connection connection) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    int res = -1;
  //    String sql = "SELECT * from locations WHERE UPPER(name) = UPPER(?)";
  //    try {
  //      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, locationName);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            //        logger.info("email found!");
  //            res = rs.getInt("id");
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //    return res
  //  }
  //
  //  private int getPersonIdFromEmail(String email, Connection connection) {
  //    //    logger.info("Looking for user with email " + email + " in the DB");
  //    println email
  //    int res = -1;
  //    String sql = "SELECT * from persons WHERE UPPER(email) = UPPER(?)";
  //    try {
  //      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
  //        statement.setString(1, email);
  //        statement.executeQuery().withCloseable { ResultSet rs ->
  //          if (rs.next()) {
  //            //        logger.info("email found!");
  //            res = rs.getInt("id");
  //          }
  //        }
  //      }
  //    } catch (SQLException e) {
  //      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
  //      e.printStackTrace();
  //    }
  //    println res
  //    return res
  //  }
}