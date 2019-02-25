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
import java.util.Date

import javax.inject.Inject

@Controller("/samples")
class SamplesController {

  //@Inject
  //private final Connection userdbConnection

  private final DBManager manager

  @Inject SamplesController(DBManager manager) {
    this.manager = manager
  }

  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
  HttpResponse<Sample> sample(@Parameter('sampleId') String code){
    if(!RegExValidator.isValidSampleCode(code))
    {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      Sample s = searchSample(code);
      if(s!=null) {
        return HttpResponse.ok(s);
      } else {
        return HttpResponse.notFound("Sample was not found in the system!");
      }
    }
  }

  @Post("/{sampleId}/currentLocation/")
  HttpResponse newLocation(@Parameter('sampleId') String sampleId, Location location) {

    Connection connection = manager.getConnection()
    connection.setAutoCommit(false);

    try {
      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
      int locationId = getLocationIdFromName(location.getName(), connection);
      addOrUpdateSample(sampleId, locationId, connection)
      setNewLocationAsCurrent(sampleId, personId, locationId, location, connection)

      connection.commit()
    } catch (Exception ex) {
      ex.printStackTrace();
      connection.rollback()
    }
    connection.setAutoCommit(true)

    HttpResponse.created(new URI("/"+sampleId));
  }

  @Put("/{sampleId}/currentLocation/")
  HttpResponse<Location> updateLocation(@Parameter('sampleId') String sampleId, Location location) {
    HttpResponse<Location> response = HttpResponse.accepted();

    Connection connection = manager.getConnection()
    connection.setAutoCommit(false);
    try {
      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
      int locationId = getLocationIdFromName(location.getName(), connection);
      addOrUpdateSample(sampleId, locationId, connection)

      if(isNewSampleLocation(sampleId, location)) {
        response = setNewLocationAsCurrent(sampleId, location)
      } else {
        updateCurrentLocationInDB(sampleId, personId, locationId, location, connection)
      }
    } catch (Exception e) {
      e.printStackTrace()
      connection.rollback();
    }
    connection.setAutoCommit(true)
    return response;
  }

  private boolean isNewSampleLocation(String sampleId, Location location) {
    println sampleId;
    println location.getName()
    String locationIDQuery = "SELECT id FROM locations WHERE name = ?;"
    Connection connection = manager.getConnection()
    boolean res = true;
    try {
      PreparedStatement statement = connection.prepareStatement(locationIDQuery);
      statement.setString(1, location.getName());
      
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        int id = rs.getInt("id");

        statement.close()
        String currentLocationQuery = "SELECT * from samples_locations WHERE location_id = ? AND sample_id = ?";
        statement = connection.prepareStatement(currentLocationQuery);
        statement.setInt(1, id);
        statement.setString(2, sampleId);

        rs = statement.executeQuery();
        if (rs.next()) {
          res = false;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    println "new location: "+res
    return res;
  }

  private void updateCurrentLocationInDB(String sampleId, int personId, int locationId, Location location, Connection connection) {
    String sql = "UPDATE samples_locations SET sample_id=?, location_id=?, arrival_time=?, forwarded_time=?, sample_status=?, responsible_person_id=?"

    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, sampleId);
    statement.setInt(2, locationId);
    statement.setDate(3, location.getArrivalDate());
    statement.setDate(4, location.getforwardDate());
    statement.setString(5, location.getStatus().toString());
    statement.setInt(6, personId);
    statement.executeQuery();
    connection.commit();
  }

  private HttpResponse setNewLocationAsCurrent(String sampleId, Location location) {
    HttpResponse response = HttpResponse.accepted();
    String sql = "INSERT INTO samples_locations (sample_id, location_id, arrival_time, forwarded_time, sample_status, responsible_person_id) VALUES (?,?,?,?,?,?)"
    Connection connection = manager.getConnection()
    connection.setAutoCommit(false);
    try {
      int personId = getPersonIdFromEmail(location.getResponsiblePerson(), connection)
      int locationId = getLocationIdFromName(location.getName(), connection);
      addOrUpdateSample(sampleId, locationId, connection)
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, sampleId);
      statement.setInt(2, locationId);
      statement.setDate(3, location.getArrivalDate());
      statement.setDate(4, location.getforwardDate());
      statement.setString(5, location.getStatus().toString());
      statement.setInt(6, personId);
      statement.executeQuery();
      connection.commit();
      //                logger.error("Project has been successfully added to all related tables.");
      //                success = true;
    } catch (Exception ex) {
      ex.printStackTrace();
      //                logger.error("Exception occured while adding project. Rolling back.");
      connection.rollback();
      response = HttpResponse.badRequest();
    }
    connection.setAutoCommit(true);
    return response;
  }

  private void addOrUpdateSample(String sampleId, int locationId, Connection connection) {
    String search = "SELECT * FROM samples where id = ?"
    try{
      PreparedStatement statement = connection.prepareStatement(search);
      statement.setString(1, sampleId);
      ResultSet rs = statement.executeQuery();
      if(!rs.next()) {
        String create = "INSERT into samples (id, current_location_id) VALUES(?,?)"
        statement = connection.prepareStatement(create);
        statement.setString(1, sampleId);
        statement.setInt(2, locationId);
        statement.executeQuery();
      } else {
        String update = "UPDATE samples SET current_location_id = ? WHERE sample_id = ?"
        statement = connection.prepareStatement(update);
        statement.setInt(1, locationId);
        statement.setString(2, sampleId);
        statement.executeQuery();
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Put("/{sampleId}/currentLocation/{status}")
  HttpResponse sampleStatus(@Parameter('sampleId') String sampleId, @Parameter('status') Status status) {
    if(!RegExValidator.isValidSampleCode(sampleId))
    {
      return HttpResponse.badRequest("Not a valid sample code!");
    }
    boolean found = searchSample(sampleId);
    if(found) {
      updateSampleStatus(sampleId, status);
      return HttpResponse.created("Sample status updated.");
    } else {
      return HttpResponse.notFound("Sample was not found in the system!");
    }
  }

  private Sample searchSample(String code) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Sample res = null;
    String sql = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
        "WHERE UPPER(samples.id) = UPPER(?)";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, code);
      ResultSet rs = statement.executeQuery();
      List<Location> pastLocs = new ArrayList<>()
      Location currLoc = null;
      while (rs.next()) {
        int currID = rs.getInt("current_location_id");
        int locID = rs.getInt("location_id");
        Date arrivalDate = rs.getDate("arrival_time");
        Date forwardedDate = rs.getDate("forwarded_time");
        Status status = rs.getString("sample_status");
        String name = rs.getString("name");
        String street = rs.getString("street");
        String country = rs.getString("country");
        int zip = rs.getInt("zip_code");

        Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
        int personID = rs.getInt("responsible_person_id");
        String responsiblePerson = getPersonNameByID(personID)

        if(currID == locID) {
          currLoc = new Location(name: name, responsiblePerson: responsiblePerson, address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
        } else {
          pastLocs.add(new Location(name: name, responsiblePerson: responsiblePerson, address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
        }
      }
      if(currLoc!=null) {
        res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private String getPersonNameByID(int id) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    String res = null;
    String sql = "SELECT * from persons WHERE id = ?";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        //        logger.info("email found!");
        String firstName = rs.getString("first_name")
        String lastName = rs.getString("family_name")
        res = firstName+" "+lastName
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private boolean updateSampleStatus(String sampleId, Status status) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    boolean res = false;
    String sql = "SELECT * from samples WHERE UPPER(id) = UPPER(?)";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, sampleId);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        int locationID = rs.getInt("current_location_ID");
        //        String oldStatus = getStatus(sampleID, locationID);
        //        int currIndex = stati.indexOf(oldStatus);
        //        if(currIndex+1 < stati.size()) {
        setStatus(sampleId, locationID, status);
        res = true;
        //        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private void setStatus(String sampleID, int locationID, Status status) {
    String sql = "UPDATE samples_locations SET sample_status = ? where sample_id = ? and location_id = ?";
    try {
      PreparedStatement statement = manager.getConnection().prepareStatement(sql);
      statement.setString(1, status.toString());
      statement.setString(2, sampleID);
      statement.setInt(3, locationID);
      statement.executeQuery();
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private int getLocationIdFromName(String locationName, Connection connection) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    String sql = "SELECT * from locations WHERE UPPER(name) = UPPER(?)";
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, locationName);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        //        logger.info("email found!");
        res = rs.getInt("id");
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private int getPersonIdFromEmail(String email, Connection connection) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    String sql = "SELECT * from persons WHERE UPPER(email) = UPPER(?)";
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, email);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        //        logger.info("email found!");
        res = rs.getInt("id");
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }
}