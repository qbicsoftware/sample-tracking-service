package life.qbic.db

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.Put
import life.qbic.datamodel.services.*
import life.qbic.micronaututils.QBiCDataSource
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import javax.inject.Inject
import javax.validation.metadata.ReturnValueDescriptor

class MariaDBQueriesOLD{// implements IQueryService {
//
//  private QBiCDataSource dataSource
//
////  @Inject MariaDBQueries(DBManager manager) {
////    this.manager = manager
////  }
//  
//  @Inject MariaDBQueriesOLD(QBiCDataSource dataSource) {
//    this.dataSource = dataSource
//}
//
//  HttpResponse<Location> addNewLocation(String sampleId, Location location) {
//    HttpResponse response = HttpResponse.ok(location);
//    Connection connection = dataSource.connection
//    connection.setAutoCommit(false)
//    try {
//      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
//      if(personId == -1) {
//        throw new NotFoundException("User with email "+location.getResponsibleEmail()+" was not found.")
//      }
//      int locationId = getLocationIdFromName(location.getName(), connection);
//      if(locationId == -1) {
//        throw new NotFoundException("Location "+location.getName()+" was not found.")
//      }
//      if(isNewSampleLocation(sampleId, location)) {
//        setNewLocationAsCurrent(sampleId, personId, locationId, location, connection)
//        addOrUpdateSample(sampleId, locationId, connection)
//        connection.commit()
//      }
//    } catch (Exception ex) {
//      connection.rollback()
//      response = HttpResponse.badRequest(ex.getMessage())
//    }
//    connection.setAutoCommit(true)
//    return response
//  }
//
//  HttpResponse<Location> updateLocation(String sampleId, Location location) {
//    HttpResponse response = HttpResponse.ok(location);
//    Connection connection = dataSource.connection
//    connection.setAutoCommit(false);
//    try {
//      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), connection);
//      if(personId == -1) {
//        throw new NotFoundException("User with email "+location.getResponsibleEmail()+" was not found.")
//      }
//      int locationId = getLocationIdFromName(location.getName(), connection);
//      if(locationId == -1) {
//        throw new NotFoundException("Location "+location.getName()+" was not found.")
//      }
//      // if the location changed, change the location of the sample
//      if(isNewSampleLocation(sampleId, location)) {
//        setNewLocationAsCurrent(sampleId, personId, locationId, location, connection)
//      } else {
//        // else: update information about the sample at the current location (times, status, etc.)
//        updateCurrentLocationObjectInDB(sampleId, personId, locationId, location, connection)
//      }
//
//      // update sample table current location id OR create new row
//      addOrUpdateSample(sampleId, locationId, connection)
//
//      connection.commit();
//    } catch (Exception e) {
//      connection.rollback();
//      response = HttpResponse.badRequest(e.getMessage())
//    }
//    connection.setAutoCommit(true)
//    return response
//  }
//
//  Contact searchPersonByEmail(String email) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    Contact contact = null;
//    String sql = "SELECT * from persons WHERE UPPER(email) = UPPER(?)"
//    try {
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
//  private Address getAddressByPerson(int personID) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    Address res = null;
//    String sql = "SELECT * from locations inner join persons_locations on locations.id = persons_locations.location_id WHERE person_id = ?";
//    try {
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
//
//  private boolean isNewSampleLocation(String sampleId, Location location) {
//    String locationIDQuery = "SELECT id FROM locations WHERE name = ?;"
//    Connection connection = dataSource.connection
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
//  private Timestamp toTimestamp(String date) {
//    if(date==null || date.isEmpty())
//      return null
//    TimeZone tz = TimeZone.getTimeZone("MEZ");
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//    df.setTimeZone(tz)
//    java.sql.Date res = new java.sql.Date(df.parse(date).getTime())
//    return new Timestamp(res.getTime())
//  }
//
//  private boolean updateCurrentLocationObjectInDB(String sampleId, int personId, int locationId, Location location, Connection connection) {
//    String sql = "UPDATE samples_locations SET arrival_time=?, forwarded_time=?, sample_status=?, responsible_person_id=? WHERE sample_id=? AND location_id=?"
//    connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
//      statement.setTimestamp(1, toTimestamp(location.getArrivalDate()))
//      statement.setTimestamp(2, toTimestamp(location.getForwardDate()))
//      statement.setString(3, location.getStatus().toString())
//      statement.setInt(4, personId)
//      statement.setString(5, sampleId)
//      statement.setInt(6, locationId)
//      statement.execute()
//    }
//    return true
//  }
//
//  private void setNewLocationAsCurrent(String sampleId, int personId, int locationId, Location location, Connection connection) {
//    boolean res = false
//    String sql = "INSERT INTO samples_locations (sample_id, location_id, arrival_time, forwarded_time, sample_status, responsible_person_id) VALUES (?,?,?,?,?,?)"
//    connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
//      statement.setString(1, sampleId)
//      statement.setInt(2, locationId)
//      statement.setTimestamp(3, toTimestamp(location.getArrivalDate()))
//      statement.setTimestamp(4, toTimestamp(location.getForwardDate()))
//      statement.setString(5, location.getStatus().toString())
//      statement.setInt(6, personId)
//      statement.execute()
//    }
//  }
//
//  private void addOrUpdateSample(String sampleId, int locationId, Connection connection) {
//    String search = "SELECT * FROM samples where id = ?"
//    connection.prepareStatement(search).withCloseable { PreparedStatement statement ->
//      statement.setString(1, sampleId);
//      statement.executeQuery().withCloseable { ResultSet rs ->
//        if(!rs.next()) {
//          String create = "INSERT into samples (id, current_location_id) VALUES(?,?)"
//          connection.prepareStatement(create).withCloseable { PreparedStatement statement2 ->
//            statement2.setString(1, sampleId);
//            statement2.setInt(2, locationId);
//            statement2.execute();
//          }
//        } else {
//          String update = "UPDATE samples SET current_location_id = ? WHERE id = ?"
//          connection.prepareStatement(update).withCloseable { PreparedStatement statement3 ->
//            statement3.setInt(1, locationId);
//            statement3.setString(2, sampleId);
//            statement3.execute();
//          }
//        }
//      }
//    }
//  }
//
//  Sample searchSample(String code) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    Sample res = null;
//    String sql = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
//        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
//        "WHERE UPPER(samples.id) = UPPER(?)";
//    try {
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
//            Person pers = getPersonNameByID(personID)
//
//            if(currID == locID) {
//              currLoc = new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
//            } else {
//              pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
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
//  private Person getPersonNameByID(int id) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    Person res = null;
//    String sql = "SELECT * from persons WHERE id = ?";
//    try {
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
//        statement.setInt(1, id);
//        statement.executeQuery().withCloseable { ResultSet rs ->
//          if (rs.next()) {
//            //        logger.info("email found!");
//            String firstName = rs.getString("first_name")
//            String lastName = rs.getString("family_name")
//            String email = rs.getString("email")
//            res = new Person(firstName, lastName, email)
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
//  boolean updateSampleStatus(String sampleId, Status status) {
//    //    logger.info("Looking for user with email " + email + " in the DB");
//    boolean res = false;
//    String sql = "SELECT * from samples WHERE UPPER(id) = UPPER(?)";
//    try {
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
//      dataSource.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
//    return res
//  }
}
