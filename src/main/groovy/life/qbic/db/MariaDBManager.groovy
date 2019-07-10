package life.qbic.db

import io.micronaut.context.annotation.Requires
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import io.micronaut.context.annotation.Property
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
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
import javax.inject.Singleton
import javax.validation.metadata.ReturnValueDescriptor

@Singleton
class MariaDBManager implements IQueryService {

    private QBiCDataSource dataSource
  //  private DBManager dataSource

  private Sql sql

  @Inject MariaDBManager(QBiCDataSource dataSource) {
    this.dataSource = dataSource
  }

  HttpResponse<Location> addNewLocation(String sampleId, Location location) {
    HttpResponse response = HttpResponse.ok(location);
    sql = new Sql(dataSource.connection)
    sql.connection.autoCommit = false

    try {
      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql)
      if(personId == -1) {
        throw new NotFoundException("User with email "+location.getResponsibleEmail()+" was not found.")
      }
      int locationId = getLocationIdFromName(location.getName(), sql)
      if(locationId == -1) {
        throw new NotFoundException("Location "+location.getName()+" was not found.")
      }
      if(isNewSampleLocation(sampleId, location)) {
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)
        addOrUpdateSample(sampleId, locationId, sql)
        sql.commit()
      }
    } catch (Exception ex) {
      sql.rollback()
      response = HttpResponse.badRequest(ex.getMessage())
    }
    sql.close()
    return response
  }

  HttpResponse<Location> updateLocation(String sampleId, Location location) {
    HttpResponse response = HttpResponse.ok(location);
    sql = new Sql(dataSource.connection)
    sql.connection.autoCommit = false
    try {
      int personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql);
      if(personId == -1) {
        throw new NotFoundException("User with email "+location.getResponsibleEmail()+" was not found.")
      }
      int locationId = getLocationIdFromName(location.getName(), sql);
      if(locationId == -1) {
        throw new NotFoundException("Location "+location.getName()+" was not found.")
      }
      // if the location changed, change the location of the sample
      if(isNewSampleLocation(sampleId, location)) {
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)
      } else {
        // else: update information about the sample at the current location (times, status, etc.)
        updateCurrentLocationObjectInDB(sampleId, personId, locationId, location, sql)
      }

      // update sample table current location id OR create new row
      addOrUpdateSample(sampleId, locationId, sql)

      sql.commit();
    } catch (Exception e) {
      sql.rollback();
      response = HttpResponse.badRequest(e.getMessage())
    }
    sql.close()
    return response
  }

  Contact searchPersonByEmail(String email) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    sql = new Sql(dataSource.connection)
    Contact contact = null;
    final String query = "SELECT * from persons WHERE UPPER(email) = UPPER('${email}');"
    List<GroovyRowResult> results = sql.rows(query)
    if( results.size() > 0 ) {
      GroovyRowResult res = results.get(0)
      int id = res.get("id")
      String first = res.get("first_name")
      String last = res.get("first_name")
      Address adr = getAddressByPerson(id)
      contact = new Contact(fullName: first + " " + last, email: email, address: adr)
    }
    sql.close()
    return contact
  }

  private Address getAddressByPerson(int personID) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    sql = new Sql(dataSource.connection)
    Address res = null;
    final String query = "SELECT * from locations inner join persons_locations on locations.id = persons_locations.location_id WHERE person_id = '${personID}';"
    List<GroovyRowResult> results = sql.rows(query)
    if( results.size() > 0 ) {
      GroovyRowResult rs = results.get(0)
      String affiliation = rs.get("name")
      String street = rs.get("street")
      int zip = rs.get("zip_code")
      String country = rs.get("country")
      res = new Address(affiliation: affiliation, street: street, zipCode: zip, country: country)
    }
    sql.close()
    return res
  }

  private boolean isNewSampleLocation(String sampleId, Location location) {
    final String locationIDQuery = "SELECT id FROM locations WHERE name = ${location.name};"
    sql = new Sql(dataSource.connection)
    boolean res = true;
    List<GroovyRowResult> results = sql.rows(locationIDQuery)
    if( results.size() > 0 ) {
      GroovyRowResult rs = results.get(0)
      int id = rs.get("id");

      final String currentLocationQuery = "SELECT * from samples_locations WHERE location_id = ${id} AND sample_id = ${sampleId};"

      List<GroovyRowResult> results2 = sql.rows(locationIDQuery)
      if( results2.size() > 0 ) {
        res = false;
      }
    }
    sql.close()
    return res;
  }

  private Timestamp toTimestamp(String date) {
    if(date==null || date.isEmpty())
      return null
    TimeZone tz = TimeZone.getTimeZone("MEZ");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    df.setTimeZone(tz)
    java.sql.Date res = new java.sql.Date(df.parse(date).getTime())
    return new Timestamp(res.getTime())
  }

  private Date toDate(Timestamp ts) {
    if(ts==null)
      return null
    return Date.valueOf(ts.toLocalDateTime().toLocalDate());
  }
  
  private boolean updateCurrentLocationObjectInDB(String sampleId, int personId, int locationId, Location location, Sql sql) {
    final String query = "UPDATE samples_locations SET arrival_time=?, forwarded_time=?, sample_status=?, responsible_person_id=? WHERE sample_id=? AND location_id=?"
    int count = sql.executeUpdate(query, toTimestamp(location.getArrivalDate()), toTimestamp(location.getForwardDate()), location.getStatus().toString(), personId, sampleId, locationId)
    return count > 0
  }

  private void setNewLocationAsCurrent(String sampleId, int personId, int locationId, Location location, Sql sql) {
    final String query = "INSERT INTO samples_locations (sample_id, location_id, arrival_time, forwarded_time, sample_status, responsible_person_id) VALUES (?,?,?,?,?,?)"
    sql.execute(query, sampleId, locationId, toTimestamp(location.getArrivalDate()), toTimestamp(location.getForwardDate()), location.getStatus().toString(), personId)
  }

  private void addOrUpdateSample(String sampleId, int locationId, Sql sql) {
    final String search = "SELECT * FROM samples where id = ${sampleId};"
    List<GroovyRowResult> results = sql.rows(search)
    if( results.size() > 0 ) {
      final String create = "INSERT into samples (id, current_location_id) VALUES(?,?)"
      sql.execute(create, sampleId, locationId)
    } else {
      final String update = "UPDATE samples SET current_location_id = ? WHERE id = ?"
      sql.execute(update, locationId, sampleId)
    }
  }

  Sample searchSample(String code) {
    Sample res = null;
    sql = new Sql(dataSource.connection)
    final String query = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
        "WHERE UPPER(samples.id) = UPPER('${code}');"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      List<Location> pastLocs = new ArrayList<>()
      Location currLoc = null;
      for(GroovyRowResult rs: results) {
        int currID = rs.get("current_location_id")
        int locID = rs.get("location_id")
        Timestamp arrivalT = rs.get("arrival_time")
        Timestamp forwardedT = rs.get("forwarded_time")
        Date arrivalDate = toDate(arrivalT)
        Date forwardedDate = toDate(forwardedT)
        Status status = rs.get("sample_status")
        String name = rs.get("name")
        String street = rs.get("street")
        String country = rs.get("country")
        int zip = rs.get("zip_code")

        Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
        int personID = rs.get("responsible_person_id");
        Person pers = getPersonNameByID(personID)

        if(currID == locID) {
          currLoc = new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
        } else {
          pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
        }
      }
      if(currLoc!=null) {
        res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)
      }

    } catch (SQLException e) {
      e.printStackTrace()
    }
    sql.close()
    return res
  }

  private Person getPersonNameByID(int id) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Person res = null;
    sql = new Sql(dataSource.connection)

    final String query = "SELECT * from persons WHERE id = ${id};"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if( results.size() > 0 ) {
        GroovyRowResult rs = results.get(0)
        //        logger.info("email found!");
        String firstName = rs.get("first_name")
        String lastName = rs.get("family_name")
        String email = rs.get("email")
        res = new Person(firstName, lastName, email)
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    sql.close()
    return res
  }

  boolean updateSampleStatus(String sampleId, Status status) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    boolean res = false;
    sql = new Sql(dataSource.connection)

    final String query = "SELECT * from samples WHERE UPPER(id) = UPPER(${sampleId});"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if( results.size() > 0 ) {
        GroovyRowResult rs = results.get(0)
        int locationID = rs.get("current_location_ID")
        //        String oldStatus = getStatus(sampleID, locationID);
        //        int currIndex = stati.indexOf(oldStatus);
        //        if(currIndex+1 < stati.size()) {
        setStatus(sampleId, locationID, status)
        res = true;
        //        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    sql.close()
    return res
  }

  private void setStatus(String sampleID, int locationID, Status status) {
    sql = new Sql(dataSource.connection)

    final String query = "UPDATE samples_locations SET sample_status = ? where sample_id = ? and location_id = ?";
    try {
      sql.execute(query, status.toString(), sampleID, locationID)
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    sql.close()
  }

  private int getLocationIdFromName(String locationName, Sql sql) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    final String query = "SELECT * from locations WHERE UPPER(name) = UPPER(${locationName})";
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if( results.size() > 0 ) {
        res = results.get(0).get("id")
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  private int getPersonIdFromEmail(String email, Sql sql) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    final String query = "SELECT * from persons WHERE UPPER(email) = UPPER(${email})";
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if( results.size() > 0 ) {
        res = results.get(0).get("id")
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

}
//@Requires(env="test")
//@Requires(property="database.schema-uri")
//@Singleton
//class DatabaseInit implements BeanCreatedEventListener<IQueryService> {
//
//  String schemaUri
//
//  DatabaseInit(@Property(name='database.schema-uri') schemaUri) {
//    println "init"
//    println schemaUri
//    this.schemaUri = schemaUri
//  }
//
//  IQueryService onCreated(BeanCreatedEvent<IQueryService> event) {
//    println "created bean"
//    String sqlStatement = new File(schemaUri).text
//    MariaDBManager mngr = event.bean as MariaDBManager
//    setupDatabase(mngr.dataSource.connection, sqlStatement)
//    return event.bean
//  }
//
//  private setupDatabase(Connection connection, String sqlStatement) {
//    Sql sql = new Sql(connection)
//    println "execute statement"
//    sql.execute(sqlStatement)
//  }
//}
