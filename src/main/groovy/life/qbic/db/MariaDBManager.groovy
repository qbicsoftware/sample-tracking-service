package life.qbic.db

import domain.notification.SampleStatusNotification
import domain.notification.SampleStatusNotificationDatasource
import domain.sample.DomainEventSerializer
import domain.sample.SampleCode
import domain.sample.SampleEvent
import domain.sample.SampleEventDatasource
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Log4j2
import life.qbic.QBiCDataSource
import life.qbic.datamodel.identifiers.SampleCodeFunctions
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.people.Contact
import life.qbic.datamodel.people.Person
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource
import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob
import java.sql.Connection
import java.sql.SQLException
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime

@Log4j2
@Singleton
class MariaDBManager implements IQueryService, INotificationService, SampleEventDatasource, SampleStatusNotificationDatasource {

  private DataSource dataSource

  private Sql sql

  private static final PERSONS_TABLE = "person"

  //TODO as a parameter?
  private DomainEventSerializer eventSerializer = new DomainEventSerializer();

  @Inject
  MariaDBManager(QBiCDataSource dataSource) {
    this.dataSource = dataSource.getSource()
  }

  void addNewLocation(String sampleId, Location location) throws IllegalArgumentException {

    if (!SampleCodeFunctions.isQbicBarcode(sampleId) && !SampleCodeFunctions.isQbicEntityCode(sampleId)) {
      throw new IllegalArgumentException("$sampleId is not valid.")
    }

    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    Sql sql = new Sql(connection)
    int locationId
    int personId

    try {
      //validate location
      locationId = getLocationIdFromName(location.getName(), sql);
      personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql)
    } catch (Exception e) {
      // only close in case of an exception appearing we need it later on
      sql.close()
      throw e
    }

    if (personId < 0) {
      String msg = "User with email " + location.getResponsibleEmail() + " was not found."
      log.error(msg)
      sql.close()
      throw new IllegalArgumentException(msg)
    }

    if (locationId < 0) {
      String msg = "Location " + location.getName() + " was not found."
      log.error(msg)
      sql.close()
      throw new IllegalArgumentException(msg)
    }

    try {
      sql.withTransaction {
        log.info "Set new sample location ${location} for sample ${sampleId}."
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)
        addOrUpdateSample(sampleId, locationId, sql)
      }
    } catch (SQLException sqlException) {
      // will always be thrown instead of an exception when withClosure is used
      String message = sqlException.getMessage()
      log.error(message)
      log.debug(sqlException)
      log.info(sqlException.message + " Rolling back previous changes.")
      throw new RuntimeException("Could not add $sampleId to $location")
    } finally {
      sql.close()
    }
  }

  void updateLocation(String sampleId, Location location) throws IllegalArgumentException {
    if (!SampleCodeFunctions.isQbicBarcode(sampleId) && !SampleCodeFunctions.isQbicEntityCode(sampleId)) {
      throw new IllegalArgumentException("$sampleId is not valid.")
    }

    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")

    Sql sql = new Sql(connection)
    int personId
    int locationId

    try {
      // validate location
      locationId = getLocationIdFromName(location.getName(), sql)
      personId = getPersonIdFromEmail(location.getResponsibleEmail(), sql)
    } catch (Exception unexpected) {
      String message = unexpected.getMessage()
      log.error(message)
      log.debug(unexpected)
      // only close the connection in case of an exception. We need it later on.
      sql.close()
      throw new RuntimeException("Could not update $sampleId to $location")
    }

    if (personId < 0) {
      String msg = "User with email " + location.getResponsibleEmail() + " was not found."
      log.error(msg)
      sql.close()
      throw new IllegalArgumentException(msg)
    }
    if (locationId < 0) {
      String msg = "Location " + location.getName() + " was not found."
      log.error(msg)
      sql.close()
      throw new IllegalArgumentException(msg)
    }

    try {
      sql.withTransaction {
        setNewLocationAsCurrent(sampleId, personId, locationId, location, sql)
        addOrUpdateSample(sampleId, locationId, sql)
      }
    } catch (SQLException sqlException) {
      // will always be thrown instead of an exception when withClosure is used
      String message = sqlException.getMessage()
      log.error(message)
      log.debug(sqlException)
      log.info(sqlException.message + " Rolling back previous changes.")
      throw new RuntimeException("Could not update $sampleId to $location")
    } finally {
      sql.close()
    }
  }

  @Override
  Contact searchPersonByEmail(String email) {
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    Sql sql = new Sql(connection)
    Contact contact = null;
    final String query = "SELECT * from $PERSONS_TABLE WHERE UPPER(email) = UPPER('${email}');"
    List<GroovyRowResult> results = sql.rows(query)
    if (results.size() > 0) {
      GroovyRowResult res = results.get(0)
      int id = res.get("id")
      String first = res.get("first_name")
      String last = res.get("last_name")
      Address adr = getAddressByPerson(id, sql)
      contact = new Contact(fullName: first + " " + last, email: email, address: adr)
    }
    sql.close()
    return contact
  }

  List<Location> getLocationsForEmail(String email) {
    List<Location> res = new ArrayList<>()
    for (Location l : listLocations()) {
      if (l.responsibleEmail.equals(email)) {
        res.add(l)
      }
    }
    return res
  }

  @Override
  List<Location> getLocationsForPerson(String identifier) {
    List<Location> locations = new ArrayList<>()
    this.sql = new Sql(dataSource)
    Map userInformation

    try {
      userInformation = getPersonById(identifier, sql)
    } catch (NotFoundException notFoundException) {
      String msg = "Invalid user id"
      throw new IllegalArgumentException(msg, notFoundException)
    }
    try {
      // find locations for user
      int userDbId = userInformation.get("id") as int
      String query = "SELECT * FROM locations INNER JOIN persons_locations ON id = location_id INNER JOIN person ON person_id = person.id WHERE person_id = $userDbId;"

      List<GroovyRowResult> rowResults = sql.rows(query)
      rowResults.each { locations.add(parseLocationFromMap(it)) }

    } catch (SQLException sqlException) {
      String msg = "Retrieving locations for $identifier caused an SQLException"
      log.error(msg, sqlException)
    } catch (Exception e) {
      String msg = "Retrieving locations for $identifier failed unexpectedly."
      log.error(msg, e)
    } finally {
      sql?.close()
    }
    return locations
  }

  /**
   * Retrieves the person row from the database for the given identifier
   *
   * @param identifier the primary user identifier. NOT the db entry id
   * @return a map containing all columns as keys and the respective values
   * @throws NotFoundException in case no user or multiple users could be found in the db
   */
  private static Map<String, ?> getPersonById(String identifier, Sql sql) throws NotFoundException {
    String query = "SELECT * FROM $PERSONS_TABLE WHERE user_id = '$identifier'"
    List<GroovyRowResult> rowResults = sql.rows(query)
    if (rowResults.size() == 1) {
      return rowResults.first() as Map
    } else {
      throw new NotFoundException("No user or multiple users with the id: '$identifier'.")
    }
  }

  /**
   * This method parses a map and create a location from it.<br>
   * @param input the map containing information to be used in creating the location
   * The following keys are expected to be present in the map:
   *     <ul>
   *         <li><code>country</code></li>
   *         <li><code>email</code></li>
   *         <li><code>last_name</code></li>
   *         <li><code>first_name</code></li>
   *         <li><code>name</code></li>
   *         <li><code>street</code></li>
   *         <li><code>zip_code</code></li>
   *     </ul>
   * @return a location with the information provided by the map
   */
  private static Location parseLocationFromMap(Map input) {
    Collection<String> expectedKeys = ["name", "street", "zip_code", "country", "first_name", "last_name", "email"]
    for (String key : expectedKeys) {
      // the contains key uses the same groovy magic that the get uses later on to extract the fields
      // even though the keys in the keySet of the map are all upper case
      if (!input.containsKey(key)) {
        throw new IllegalArgumentException("The provided input did not provide the expected key $key.")
      }
    }

    //Location
    String name = input.get("name")
    String street = input.get("street")
    int zip = input.get("zip_code") as int
    String country = input.get("country")
    Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
    //Person
    String first = input.get("first_name")
    String last = input.get("last_name")
    String mail = input.get("email")
    return new Location(name: name, responsiblePerson: first + " " + last, responsibleEmail: mail, address: address);
  }

  List<Location> listLocations() {
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    Sql sql = new Sql(connection)
    List<Location> locs = new ArrayList<>()
    final String query = "SELECT * FROM locations INNER JOIN persons_locations ON locations.id = persons_locations.location_id INNER JOIN $PERSONS_TABLE ON persons_locations.person_id = ${PERSONS_TABLE}.id"
    List<GroovyRowResult> results = sql.rows(query)

    for (GroovyRowResult rs : results) {
      //Location
      String name = rs.get("name")
      String street = rs.get("street")
      int zip = rs.get("zip_code")
      String country = rs.get("country")

      Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
      //Person
      String first = rs.get("first_name")
      String last = rs.get("last_name")
      String mail = rs.get("email")
      Location l = new Location(name: name, responsiblePerson: first + " " + last, responsibleEmail: mail, address: address);
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
    if (results.size() > 0) {
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
    if (results.size() > 0) {
      GroovyRowResult rs = results.get(0)
      int id = rs.get("id")

      final String currentLocationQuery = "SELECT * FROM samples WHERE current_location_id = '${id}' AND id = '${sampleId}';"

      List<GroovyRowResult> currLocRes = sql.rows(currentLocationQuery)
      if (currLocRes.size() > 0) {
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
    if (results.size() > 0) {
      GroovyRowResult rs = results.get(0)
      int id = rs.get("id");

      final String currentLocationQuery = "SELECT * from samples_locations WHERE location_id = '${id}' AND sample_id = '${sampleId}';"

      List<GroovyRowResult> results2 = sql.rows(currentLocationQuery)
      if (results2.size() > 0) {
        res = false;
      }
    }
    return res;
  }

  private Timestamp toTimestamp(String date) {
    if (date == null || date.isEmpty())
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
    if (ts == null)
      return null
    if (ts instanceof Timestamp) {
      java.util.Date res = ts;
      return res;
    } else if (ts instanceof OffsetDateTime) {
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
    if (results.size() == 0) {
      final String create = "INSERT INTO samples (id, current_location_id) VALUES(?,?)"
      sql.execute(create, sampleId, locationId)
    } else {
      final String update = "UPDATE samples SET current_location_id = ? WHERE id = ?"
      sql.execute(update, locationId, sampleId)
    }
  }

  Sample searchSample(String code) {
    Sample res = null;
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    final String query = "SELECT * FROM samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id " +
            "INNER JOIN locations ON samples_locations.location_id = locations.id " +
            "WHERE UPPER(samples.id) = UPPER('${code}');"
    try (Sql sql = new Sql(connection)) {
      List<GroovyRowResult> results = sql.rows(query)
      List<Location> pastLocs = new ArrayList<>()
      Location currLoc = null;
      java.util.Date currDate = null
      for (GroovyRowResult rs : results) {
        int currID = rs.get("current_location_id") as int
        int locID = rs.get("location_id") as int
        java.util.Date arrivalDate = toDate(rs.get("arrival_time"))
        java.util.Date forwardedDate = toDate(rs.get("forwarded_time"))
        Status status = rs.get("sample_status") as Status
        String name = rs.get("name")
        String street = rs.get("street")
        String country = rs.get("country")
        int zip = rs.get("zip_code") as int

        Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
        int personID = rs.get("responsible_person_id") as int
        Person pers
        try {
          pers = getPersonByDbId(personID)
        } catch (NotFoundException personNotFoundException) {
          log.error("Could not retrieve responsible person for change {sample: ${code}; time of change: ${arrivalDate.toString()}}.", personNotFoundException)
          throw new RuntimeException("The request for sample ${code} failed.")
        }

        if (currID == locID) {
          // Compare if current location is the newest entry
          if (currDate && currDate.before(arrivalDate)) {
            // Set the current location to the newer one
            log.info("Newer location entry found!")
            pastLocs.add(new Location(name: currLoc.name, responsiblePerson: currLoc.responsiblePerson,
                    responsibleEmail: currLoc.responsibleEmail, address: currLoc.address,
                    status: currLoc.status, arrivalDate: currDate))
            currDate = arrivalDate
            currLoc = new Location(name: name, responsiblePerson: pers.getFirstName() + " " + pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
          } else if (currDate && !currDate.before(arrivalDate)) {
            // The location is the current one, but arrival date is older
            pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName() + " " + pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate))
          } else {
            // Current location was not yet set, so we set it the first time
            currLoc = new Location(name: name, responsiblePerson: pers.getFirstName() + " " + pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate);
            currDate = arrivalDate
          }
        } else {
          pastLocs.add(new Location(name: name, responsiblePerson: pers.getFirstName() + " " + pers.getLastName(), responsibleEmail: pers.getEMail(), address: address, status: status, arrivalDate: arrivalDate, forwardDate: forwardedDate));
        }
      }
      if (currLoc != null) {
        res = new Sample(code: code, currentLocation: currLoc, pastLocations: pastLocs)
      }

    } catch (SQLException e) {
      e.printStackTrace()
    }
    return res
  }

  private static Person rowResultToPerson(GroovyRowResult rowResult) {
    String firstName = rowResult.get("first_name")
    String lastName = rowResult.get("last_name")
    String email = rowResult.get("email")
    if (firstName == null || lastName == null || email == null) {
      log.error("Incomplete person dataset. ${rowResult.get("id")}")
      throw new RuntimeException("Incomplete person dataset.")
    }
    return new Person(firstName, lastName, email)
  }

  private Person getPersonByDbId(int id) throws NotFoundException {
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    String query = "SELECT * from ${PERSONS_TABLE} WHERE id = ${id}"
    try (Sql sql = new Sql(connection)) {
      def results = sql.rows(query)
      Person foundPerson = Optional.ofNullable(results)
              .filter({ return !(it == null || it.isEmpty()) })
              .map(DefaultGroovyMethods::first)
              .map(this::rowResultToPerson)
              .orElseThrow({ new NotFoundException("No person with id ${id} exists.") })
      return foundPerson
    }
  }

  @Override
  void sampleChanged(String sampleCode, Status sampleStatus) {
    Connection connection = Objects.requireNonNull(dataSource.getConnection(),
            "Connection must not be null.")
    Sql sql = new Sql(connection)
    try {
      logSampleChange(sampleCode, sampleStatus, sql)
    } catch (Exception unexpected) {
      log.error("An unexpected error occured: $unexpected.message")
      log.debug("An unexpected error occured: $unexpected.message", unexpected)
    } finally {
      sql.close()
    }
  }

  /**
   * Writes a sample status change to the notification table.
   * @param sampleCode the the sample code of the changed sample
   * @param sampleStatus the new value of the sample status
   * @param sql The sql connection facade to be used
   * @see #sampleChanged
   */
  private static void logSampleChange(String sampleCode, Status sampleStatus, Sql sql) {
    String query = "INSERT INTO notification (`sample_code`, `arrival_time`, `sample_status`) " +
            "VALUES(?, CURRENT_TIMESTAMP, ?);"
    try {
      sql.execute(query, sampleCode, sampleStatus.toString())
    } catch (SQLException sqlException) {
      log.error("sample change logging unsuccessful: $sqlException.message")
      log.debug("sample change logging unsuccessful: $sqlException.message", sqlException)
    }
  }

  /**
   * Updates a sample status. In case the sample is multiple times in the database, the first query row is modified.
   * <p><b>Please Note:</b> This method can potentially overwrite existing data and is unsafe to use.
   * @param sampleId the sample code for which the status changed
   * @param status the new sample status
   * @throws NotFoundException in case the sample was not found in the database or has no location assigned
   * @since 1.0.0
   */
  void updateSampleStatus(String sampleId, Status status) throws NotFoundException {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    Sql sql = new Sql(connection)

    final String query = "SELECT * from samples WHERE UPPER(id) = UPPER('${sampleId}');"
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if (results.size() > 0) {
        GroovyRowResult rs = results.get(0)
        int locationID = rs.get("current_location_ID")
        setStatus(sampleId, locationID, status, sql)
      } else {
        throw new NotFoundException("Sample $sampleId could not be found in the database.")
      }
    } catch (NotFoundException notFoundException) {
      sql.close()
      throw notFoundException
    } catch (SQLException e) {
      log.error("Could not update sample status for $sampleId: $e.message")
      log.debug("Could not update sample status for $sampleId: $e.message", e)
      sql.close()
      throw e
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
    } finally {
      sql.close()
    }
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
      if (results.size() > 0) {
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
    final String query = "SELECT * from $PERSONS_TABLE WHERE UPPER(email) = UPPER('${email}') OR UPPER(user_id) = UPPER('${email}')";
    try {
      List<GroovyRowResult> results = sql.rows(query)
      if (results.size() > 0) {
        res = results.get(0).get("id")
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  @Override
  <T extends SampleEvent> void store(T sampleEvent) {
    Blob eventSerialized = new SerialBlob(eventSerializer.serialize(sampleEvent))

    String query = "INSERT INTO sample_events (`sample_code`, `event_time`, `event_type`, `event_serialized`) " +
            "VALUES(?, ?, ?, ?);"
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    try (Sql sql = new Sql(connection)) {
      sql.execute(query, sampleEvent.sampleCode().toString(), sampleEvent.occurredOn(), sampleEvent.getClass().getSimpleName(), eventSerialized)
    } catch (SQLException sqlException) {
      log.error("sample event storage logging unsuccessful: $sqlException.message", sqlException)
    }
  }

  @Override
  List<SampleEvent> findAllForSample(SampleCode sampleCode) {
    Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.")
    try (Sql sql = new Sql(connection)) {
      List<SampleEvent> events = new ArrayList<>()
      final String query = "SELECT * FROM sample_events WHERE sample_code = '${sampleCode.toString()}' ORDER BY event_time;"

      List<GroovyRowResult> results = sql.rows(query)

      for (GroovyRowResult rs : results) {
        byte[] resultValue = rs.get("event_serialized") as byte[]
        SampleEvent event = eventSerializer.deserialize(resultValue)
        events.add(event)
      }
      sql.close()
      return events
    }
  }

  @Override
  void store(SampleStatusNotification notification) {
    String query = "INSERT INTO notification ('sample_code', 'sample_status', 'arrival_time') " +
            "VALUES(?, ?, ?);"
    try(Connection connection = Objects.requireNonNull(dataSource.getConnection(), "Connection must " +
            "not be null.");
        Sql sql = new Sql(connection)) {
      sql.execute(query,
              notification.sampleCode(),
              notification.sampleStatus(),
              notification.recodedAt())
    } catch (SQLException sqlException) {
      log.error("could not log $notification: $sqlException.message", sqlException)
    }
  }
}
