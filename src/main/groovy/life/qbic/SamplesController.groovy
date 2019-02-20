package life.qbic

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put
import life.qbic.model.Sample
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

import javax.inject.Inject

@Controller("/samples")
class SamplesController {

  //@Inject
  //private final Connection userdbConnection

  private final DBManager manager
  private final List<String> stati = new ArrayList<>(Arrays.asList("WAITING","PROCESSING","PROCESSED"));  //TODO replace by ordered enum or use parameter to update

  @Inject SamplesController(DBManager manager) {
    this.manager = manager
  }

  @Get("/{sampleId}/currentLocation/status")
  HttpResponse samples(@Parameter('sampleId') String code) {
    if(!RegExValidator.isValidSampleCode(code))
    {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      boolean found = searchSample(code);
      if(found) {
        String newStatus = updateSampleStatus(code);
        if(newStatus!=null) {
          return HttpResponse.ok("Sample status updated to "+newStatus);
        } else {
          return HttpResponse.badRequest("Sample was already processed at this location. Status cannot be updated.")
        }
      }
      else {
        return HttpResponse.notFound("Sample was not found in the system!");
      }
    }
  }


  //  @Get("/samples/{sampleId}")
  //  @Produces(MediaType.APPLICATION_JSON)
  //  Contact contacts(@Parameter('email') String email){
  //    Contact c = searchPersonByEmail(email);
  //    if(c!=null)
  //      return c;
  //    return new Contact(fullName: "Sven Fillinger", email: email, address: address)
  //  }

  boolean searchSample(String code) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    boolean res = false;
    String sql = "SELECT * from samples WHERE UPPER(id) = UPPER(?)";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, code);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private boolean updateSampleStatus(String code) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    boolean res = false;
    String sql = "SELECT * from samples WHERE UPPER(id) = UPPER(?)";
    String status = null;
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, code);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        int sampleID = rs.getInt("sample_id");
        int locationID = rs.getInt("location_ID");
//        String oldStatus = getStatus(sampleID, locationID);
//        int currIndex = stati.indexOf(oldStatus);
//        if(currIndex+1 < stati.size()) {
          setStatus(sampleID, locationID, stati.get(currIndex+1));
          res = true;
//        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private void setStatus(int sampleID, int locationID, String status) {
    String sql = "UPDATE samples_locations SET sample_status = ? where sample_id = ? and location_id = ?";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, status);
      statement.setInt(2, sampleID);
      statement.setInt(3, locationID);
      statement.executeQuery();
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
//  private Location getLocationByID(int id) {
//    
//  }
//  
//  private Location getLocationByPerson(int personID) {
//    
//  }
//
//  private String getStatus(int sampleID, int locationID) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    String res = "";
//    String sql = "SELECT sample_status from samples_locations where sample_id = ? and location_id = ?";
//    try {
//      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
//      statement.setInt(1, sampleID);
//      statement.setInt(2, locationID);
//      ResultSet rs = statement.executeQuery();
//      if (rs.next()) {
//        res = rs.getString("sample_status");
//      }
//    } catch (SQLException e) {
//      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
//      e.printStackTrace();
//    }
//    return res
//  }
}