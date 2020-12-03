package life.qbic.db

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*
import life.qbic.micronaututils.QBiCDataSource

import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource
import java.sql.SQLException
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime

@Log4j2
@Singleton
class MariaDBManager implements IQueryService {

  private DataSource dataSource

  private Sql sql

  private static final PERSONS_TABLE = "person"

  @Inject MariaDBManager(QBiCDataSource dataSource) {
    this.dataSource = dataSource.getSource()
  }

  HttpResponse<Location> addNewLocation(String sampleId, Location location) {
    this.sql = new Sql(dataSource)
    HttpResponse response = HttpResponse.ok(location);
    try {
      sql.withTransaction {
        int personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql)
        if(personId == -1) {
          String msg = "User with email "+location.getResponsibleEmail()+" was not found."
          log.error(msg)
          throw new NotFoundException(msg)
        }
        int locationId = getLocationIdFromName(location.getName(), sql);
        if(locationId == -1) {
          String msg = "Location "+location.getName()+" was not found."
          log.error(msg)
          throw new NotFoundException(msg)
        }

        log.info "Set new sample location ${location} for sample ${sampleId}."
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)
        addOrUpdateSample(sampleId, locationId, sql)

      }
    } catch (Exception ex) {
      String msg = ex.getMessage()
      log.info msg+" Rolling back previous changes and returning bad request."
      response = HttpResponse.badRequest(msg)
    } finally {
      sql.close()
    }
    return response
  }

  HttpResponse<Location> updateLocation(String sampleId, Location location) {
    HttpResponse response = HttpResponse.ok(location);
    this.sql = new Sql(dataSource)
    try {
      sql.withTransaction {
        int personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql);

        if(personId == -1) {
          String msg = "User with email "+location.getResponsibleEmail()+" was not found."
          log.error(msg)
          throw new NotFoundException(msg)
        }
        int locationId = getLocationIdFromName(location.getName(), sql);
        if(locationId == -1) {
          String msg = "Location "+location.getName()+" was not found."
          log.error(msg)
          throw new NotFoundException(msg)
        }
        // Always set new location as current
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)

        // update sample table current location id OR create new row
        addOrUpdateSample(sampleId, locationId, sql)

      }
    } catch (Exception ex) {
      String msg = ex.getMessage()
      log.info msg+" Rolling back previous changes and returning bad request."
      response = HttpResponse.badRequest(msg)
    } finally {
      sql.close()
    }
    return response
  }

  @Override
  Contact searchPersonByEmail(String email) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    this.sql = new Sql(dataSource)
    Contact contact = null;
    final String query = "SELECT * from $PERSONS_TABLE WHERE UPPER(email) = UPPER('${email}');"
    List<GroovyRowResult> results = sql.rows(query)
    if( results.size() > 0 ) {
      GroovyRowResult res = results.get(0)
      int id = res.get("id")
      String first = res.get("first_name")
      String last = res.get("family_name")
      Address adr = getAddressByPerson(id, sql)
      contact = new Contact(fullName: first + " " + last, email: email, address: adr)
    }
    sql.close()
    return contact
  }

  List<Location> getLocationsForEmail(String email) {
    List<Location> res = new ArrayList<>()
    for(Location l : listLocations()) {
      if(l.responsibleEmail.equals(email)) {
        res.add(l)
      }
    }
    return res
  }

  @Override
  List<Location> getLocationsForPerson(String identifier) {
    Sql sql = null
    List<Location> locations = new ArrayList<>()
    try {
      sql = new Sql(this.dataSource)
      sql.withTransaction {
        Map userInformation = getPersonById(identifier, sql)
        // find locations for user
        int userDbId = userInformation.get("id") as int
        String query = "SELECT name FROM locations INNER JOIN persons_locations ON id=location_id WHERE person_id = '$userDbId';"
        List<GroovyRowResult> rowResults = sql.rows(query)
        rowResults.each { locations.add(parseLocationFromMap(it)) }
      }
    } catch(Exception e) {
      log.error("Retrieving locations for $identifier failed unexpectedly.", e)
    } finally {
      sql?.close()
    }
    return locations
  }

  /**
   * This method parses a map and create a location from it.<br>
   * @param input the map containing information to be used in creating the location
   * The following keys are expected to be present in the map:
   *     <ul>
   *         <li><code>country</code></li>
   *         <li><code>email</code></li>
   *         <li><code>family_name</code></li>
   *         <li><code>first_name</code></li>
   *         <li><code>name</code></li>
   *         <li><code>street</code></li>
   *         <li><code>zip_code</code></li>
   *     </ul>
   * @return a location with the information provided by the map
   */
  private static Location parseLocationFromMap(Map input) {

    Collection<String> expectedKeys = ["name", "street", "zip_code", "country", "first_name", "family_name", "email"]
    if (!input.keySet().containsAll(expectedKeys)) {
      log.info(input.keySet())
      throw new IllegalArgumentException ("The provided input did not provide all expected keys.")
    }

    //Location
    String name = input.get("name")
    String street = input.get("street")
    int zip = input.get("zip_code") as int
    String country = input.get("country")
    Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
    //Person
    String first = input.get("first_name")
    String last = input.get("family_name")
    String mail = input.get("email")
    return new Location(name: name, responsiblePerson: first+" "+last, responsibleEmail: mail, address: address);
  }

  List<Location> listLocations() {
    this.sql = new Sql(dataSource)
    List<Location> locs = new ArrayList<>()
    final String query = "SELECT * FROM locations INNER JOIN persons_locations ON locations.id = persons_locations.location_id INNER JOIN $PERSONS_TABLE ON persons_locations.person_id = ${PERSONS_TABLE}.id"
    List<GroovyRowResult> results = sql.rows(query)

    for(GroovyRowResult rs: results) {
      //Location
      String name = rs.get("name")
      String street = rs.get("street")
      int zip = rs.get("zip_code")
      String country = rs.get("country")

      Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
      //Person
      String first = rs.get("first_name")
      String last = rs.get("family_name")
      String mail = rs.get("email")
      Location l = new Location(name: name, responsiblePerson: first+" "+last, responsibleEmail: mail, address: address);
      locs.add(l)
    }
    sql.close()
    return locs
  }

  private Address getAddressByPerson(int personID, Sql sql) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Address res = null;
    final String query = "SELECT * FROM locations INNER JOIN persons_locations ON locations.id = persons_locations.location_id WHERE person_id = '${personID}';"
    List<GroovyRowResult> results = sql.rows(query)
    if( results.size() > 0 ) {
      GroovyRowResult rs = results.get(0)
      String affiliation = rs.get("name")
      String street = rs.get("street")
      int zip = rs.get("zip_code")
      String country = rs.get("country")
      res = new Address(affiliation: affiliation, street: street, zipCode: zip, country: country)
    }
    return res
  }

  private boolean isCurrentSampleLocation(String sampleId, Location location, Sql sql) {
    String locName = location.name
    final String locationIDQuery = "SELECT id FROM locations WHERE name = '${locName}';"
    List<GroovyRowResult> results = sql.rows(locationIDQuery)
    boolean res = false;
    if( results.size() > 0 ) {
      GroovyRowResult rs = results.get(0)
      int id = rs.get("id")

      final String currentLocationQuery = "SELECT * FROM samples WHERE current_location_id = '${id}' AND id = '${sampleId}';"

      List<GroovyRowResult> currLocRes = sql.rows(currentLocationQuery)
      if(currLocRes.size() > 0 ) {
        res = true;
      }
    }
    return res;
  }

  /**
   * Currently unused
   */
  private boolean isNewSampleLocation(String sampleId, Location location, Sql sql) {
    String locName = location.name
    final String locationIDQuery = "SELECT id FROM locations WHERE name = '${locName}';"
    boolean res = true;
    List<GroovyRowResult> results = sql.rows(locationIDQuery)
    if( results.size() > 0 ) {
      GroovyRowResult rs = results.get(0)
      int id = rs.get("id");

      final String currentLocationQuery = "SELECT * from samples_locations WHERE location_id = '${id}' AND sample_id = '${sampleId}';"

      List<GroovyRowResult> results2 = sql.rows(currentLocationQuery)
      if( results2.size() > 0 ) {
        res = false;
      }
    }
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

  /**
   * includes handling of test database
   * @param ts
   * @return
   */
  private java.util.Date toDate(Object ts) {
    if(ts==null)
      return null
    if(ts instanceof Timestamp) {
      java.util.Date res = ts;
      return res;
    } else if(ts instanceof OffsetDateTime){
      // this is the data type returned for integration tests!
      log.info "Date object is an OffsetDateTime, this should only happen in testing!"
      return new java.util.Date(ts.toInstant().toEpochMilli());
    }
  }

  /**
   * @depredcated
   * Not needed anymore, as we will add new location entries always.
   *
   * @param sampleId
   * @param personId
   * @param locationId
   * @param location
   * @param sql
   * @return <code>true</code> if executed successfully, <code>false</code> otherwise
   */
  @Deprecated
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
    final String search = "SELECT * FROM samples WHERE id = '${sampleId}';"
    List<GroovyRowResult> results = sql.rows(search)
    if( results.size() == 0 ) {
      final String create = "INSERT INTO samples (id, current_location_id) VALUES(?,?)"
      sql.execute(create, sampleId, locationId)
    } else {
      final String update = "UPDATE samples SET current_location_id = ? WHERE id = ?"
      sql.execute(update, locationId, sampleId)
    }
  }

  Sample searchSample(String code) {
    Sample res = null;
    this.sql = new Sql(dataSource)
    final String query = "SELECT * FROM samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
        "WHERE UPPER(samples.id) = UPPER('${code}');"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      List<Location> pastLocs = new ArrayList<>()
      Location currLoc = null;
      java.util.Date currDate = null
      for(GroovyRowResult rs: results) {
        int currID = rs.get("current_location_id")
        int locID = rs.get("location_id")
        java.util.Date arrivalDate = toDate(rs.get("arrival_time"))
        java.util.Date forwardedDate = toDate(rs.get("forwarded_time"))
        Status status = rs.get("sample_status")
        String name = rs.get("name")
        String street = rs.get("street")
        String country = rs.get("country")
        int zip = rs.get("zip_code")

        Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
        int personID = rs.get("responsible_person_id");
        Person pers = getPersonNameByID(personID)

        if(currID == locID ) {
           // Compare if current location is the newest entry
            if (currDate && currDate.before(arrivalDate)) {
              // Set the current location to the newer one
              log.info("Newer location entry found!")
              pastLocs.add(new Location(name: currLoc.name, responsiblePerson: currLoc.responsiblePerson,
                      responsibleEmail: currLoc.responsibleEmail, address: currLoc.address,
                      status: currLoc.status, arrivalDate: currDate))
              currDate = arrivalDate
              currLoc = new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
            } else if(currDate && !currDate.before(arrivalDate)) {
              // The location is the current one, but arrival date is older
              pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate))
            } else {
              // Current location was not yet set, so we set it the first time
              currLoc = new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
              currDate = arrivalDate
          }
        } else {
          pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName()+" "+pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
        }
      }
      if(currLoc!=null) {
        res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)
      }

    } catch (SQLException e) {
      e.printStackTrace()
    } finally {
      sql.close()
    }
    return res
  }

  private Person getPersonNameByID(int id) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Person res = null;
    this.sql = new Sql(dataSource)

    final String query = "SELECT * from $PERSONS_TABLE WHERE id = ${id};"
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
    } finally {
      sql.close()
    }
    return res
  }

  boolean updateSampleStatus(String sampleId, Status status) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    boolean res = false;
    this.sql = new Sql(dataSource)

    final String query = "SELECT * from samples WHERE UPPER(id) = UPPER('${sampleId}');"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if( results.size() > 0 ) {
        GroovyRowResult rs = results.get(0)
        int locationID = rs.get("current_location_ID")
        //        String oldStatus = getStatus(sampleID, locationID);
        //        int currIndex = stati.indexOf(oldStatus);
        //        if(currIndex+1 < stati.size()) {
        setStatus(sampleId, locationID, status, sql)
        res = true;
        //        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      sql.close()
    }
    return res
  }

  private void setStatus(String sampleID, int locationID, Status status, Sql sql) {
    final String query = "UPDATE samples_locations SET sample_status = ? where sample_id = ? and location_id = ?";
    try {
      sql.execute(query, status.toString(), sampleID, locationID)
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private int getLocationIdFromName(String locationName, Sql sql) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    final String query = "SELECT * from locations WHERE UPPER(name) = UPPER('${locationName}')";
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

  /**
   * Retrieves the person row from the database for the given identifier
   *
   * @param identifier the primary user identifier. NOT the db entry id
   * @return a map containing all columns as keys and the respective values
   */
  private static Map<String, ?> getPersonById(String identifier, Sql sql) {
    String query = "SELECT * FROM $PERSONS_TABLE WHERE user_id = '$identifier'"
    List<GroovyRowResult> rowResults = sql.rows(query)
    if (rowResults.size() == 1) {
      return rowResults.first() as Map
    } else {
      throw new IllegalArgumentException("No user or multiple users with the same id.")
    }
  }

  private int getPersonIdFromEmail(String email, Sql sql) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    int res = -1;
    final String query = "SELECT * from $PERSONS_TABLE WHERE UPPER(email) = UPPER('${email}')";
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
