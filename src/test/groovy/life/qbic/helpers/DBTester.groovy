package life.qbic.helpers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.people.Person
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status

import java.sql.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Log4j2
class DBTester {

  private Connection connection

  public void loginWithCredentials(String driver, String url, String user, String pw) throws Exception{
    Class.forName(driver)
    connection = DriverManager.getConnection(url, user, pw)
  }

  void dropTables() {
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE if exists LOCATIONS")
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE if exists PERSON")
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE if exists SAMPLES")
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE if exists PERSONS_LOCATIONS")
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE if exists SAMPLES_LOCATIONS")
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void createTables() {
    dropTables()
    String locations = "CREATE TABLE LOCATIONS"+
        "(ID INTEGER NOT NULL IDENTITY,"+
        "NAME VARCHAR(64) NOT NULL,"+
        "STREET VARCHAR(64) NOT NULL,"+
        "ZIP_CODE INTEGER NOT NULL,"+
        "COUNTRY VARCHAR(64) NOT NULL,"+
        "PRIMARY KEY (ID))"

    String persons = "CREATE TABLE PERSON"+
        "(ID INTEGER NOT NULL IDENTITY,"+
        "USER_ID VARCHAR(256),"+
        "FIRST_NAME VARCHAR(45) NOT NULL,"+
        "LAST_NAME VARCHAR(45) NOT NULL,"+
        "TITLE VARCHAR(5),"+
        "EMAIL VARCHAR(256) NOT NULL,"+
        "PRIMARY KEY (ID))"

    String samples = "CREATE TABLE SAMPLES"+
        "(ID VARCHAR(14) NOT NULL,"+
        "CURRENT_LOCATION_ID INTEGER NOT NULL,"+
        "PRIMARY KEY (ID))"

    String samples_locations = "CREATE TABLE SAMPLES_LOCATIONS"+
        "(SAMPLE_ID VARCHAR(14) NOT NULL,"+
        "LOCATION_ID INTEGER NOT NULL,"+
        "ARRIVAL_TIME DATETIME WITH TIME ZONE,"+
        "FORWARDED_TIME DATETIME WITH TIME ZONE,"+
        "SAMPLE_STATUS VARCHAR(20) DEFAULT 'WAITING' NOT NULL,"+
        "responsible_person_id INTEGER NOT NULL)"

    String persons_locations = "CREATE TABLE PERSONS_LOCATIONS"+
        "(PERSON_ID INTEGER NOT NULL,"+
        "LOCATION_ID INTEGER NOT NULL)"

    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate(locations)

      statement = connection.createStatement()
      statement.executeUpdate(persons)

      statement = connection.createStatement()
      statement.executeUpdate(samples)

      statement = connection.createStatement()
      statement.executeUpdate(samples_locations)

      statement = connection.createStatement()
      statement.executeUpdate(persons_locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private String parseDate(Date date) {
    TimeZone tz = TimeZone.getTimeZone("MEZ");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    df.setTimeZone(tz);
    return df.format(date);
  }

  private java.sql.Date parseDate(String date) {
    TimeZone tz = TimeZone.getTimeZone("MEZ");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    df.setTimeZone(tz)
    return new java.sql.Date(df.parse(date).getTime())
  }

  void addSampleWithHistory(String code, Location currentLocation, Person currentPerson, List<Location> pastLocations, List<Person> pastPersons) {
    for(int i = 0; i < pastLocations.size(); i++) {
      Location loc = pastLocations.get(i)
      int personId = addPersonStandardMetadata(pastPersons.get(i))
      Address address = loc.getAddress()
      int locationId = addLocationForPerson(loc.getName(),address.getStreet(), address.getCountry(), address.getZipCode(), personId)
      addSampleLocation(code, locationId, loc, personId)
    }
    int currentPersonId = addPersonStandardMetadata(currentPerson)
    Address currentAddress = currentLocation.getAddress()
    int currentLocationId = addLocationForPerson(currentLocation.getName(),currentAddress.getStreet(), currentAddress.getCountry(), currentAddress.getZipCode(), currentPersonId)

    addSampleLocation(code, currentLocationId, currentLocation, currentPersonId)
    addSample(code, currentLocationId)
  }

  boolean findSample(String code, int locID) {
    boolean res = false
    try {
      connection.prepareStatement("SELECT * FROM samples where id = '"+code+"'").withCloseable { PreparedStatement statement ->
        statement.executeQuery().withCloseable { ResultSet resultSet ->
          while (resultSet.next()) {
            String id = resultSet.getString("id");
            int loc = resultSet.getInt("current_location_id");
            res = code.equals(id) && loc == locID
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return res
  }

  void addSample(String code, int locationId) {
    String sql = "INSERT INTO samples (id,current_location_id) VALUES (?,?)";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setString(1, code);
        statement.setInt(2, locationId);
        statement.execute()
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    //    log.info "-----"
    try {
      connection.prepareStatement("SELECT * FROM samples").withCloseable { PreparedStatement statement ->
        statement.executeQuery().withCloseable { ResultSet resultSet ->
          //          log.info "id---current_location_id"
          while (resultSet.next()) {
            String id = resultSet.getString("id");
            int loc = resultSet.getInt("current_location_id");
            //            log.info id+"---"+loc
          }
          //          log.info "-----"
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void addSampleLocation(String code, int locationId, Location loc, int personId) {
    String sql = "INSERT INTO samples_locations(sample_id,location_id,arrival_time,forwarded_time,sample_status,responsible_person_id) VALUES(?,?,?,?,?,?)";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setString(1, code);
        statement.setInt(2, locationId);
        statement.setDate(3, parseDate(loc.getArrivalDate()));
        statement.setDate(4, parseDate(loc.getForwardDate()));
        statement.setString(5, loc.getStatus().toString());
        statement.setInt(6, personId);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  int addPersonStandardMetadata(Person p) {
    return addPerson("user",p.getFirstName(), p.getLastName(),p.getEMail())
  }

  int addPerson(String user, String first, String last, String email) {
    String sql = "INSERT INTO person(user_id,first_name,last_name,email) VALUES(?,?,?,?)";
    int res = -1;
    try {
      connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement statement ->
        statement.setString(1, user);
        statement.setString(2, first);
        statement.setString(3, last);
        statement.setString(4, email);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          //        logger.info("email found!");
          res = rs.getInt("id");
        }
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return res;
  }

  int addLocation(Location loc) {
    return addLocation(loc.name, loc.address.street, loc.address.country, loc.address.zipCode)
  }

  int addLocation(String name, String street, String country, int zip) {
    String sql1 = "INSERT INTO locations(name, street, zip_code, country) VALUES(?,?,?,?)"
    int locationID = -1;
    try {
      connection.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement statement ->
        statement.setString(1, name);
        statement.setString(2, street);
        statement.setInt(3, zip);
        statement.setString(4, country);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          locationID = rs.getInt("id");
        }
        rs.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      connection.prepareStatement("SELECT * FROM locations").withCloseable { PreparedStatement statement ->
        statement.executeQuery().withCloseable { ResultSet resultSet ->
          while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String loc = resultSet.getString("name");
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locationID
  }

  Sample searchSample(String code) {
    Sample res = null;
    String sql = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
        "WHERE UPPER(samples.id) = UPPER(?)";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setString(1, code)
        statement.executeQuery().withCloseable { ResultSet rs ->
          List<Location> pastLocs = new ArrayList<>()
          Location currLoc = null;
          while (rs.next()) {
            int currID = rs.getInt("current_location_id")
            int locID = rs.getInt("location_id");
            Timestamp arvl = rs.getTimestamp("arrival_time")
            Timestamp fwd = rs.getTimestamp("forwarded_time")
            Date arrivalDate = null
            if(arvl!=null) {
              arrivalDate = new Date(arvl.getTime())
            }
            Date forwardedDate = null
            if(fwd!=null) {
              forwardedDate = new Date(fwd.getTime());
            }
            Status status = rs.getString("sample_status");
            String name = rs.getString("name");
            String street = rs.getString("street");
            String country = rs.getString("country");
            int zip = rs.getInt("zip_code");

            Address address = new Address(affiliation: name, street: street, zipCode: zip, country: country)
            int personID = rs.getInt("responsible_person_id");
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
        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  void printTable(String table) {
    println "Table "+table
    String sql = "SELECT * from "+table;
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.executeQuery().withCloseable { ResultSet rs ->
          ResultSetMetaData metadata = rs.getMetaData();
          int columnCount = metadata.getColumnCount();
          while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
              if (i > 1) System.out.print(",  ");
              String columnValue = rs.getString(i);
              System.out.print(columnValue);
            }
            System.out.println("");
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Person getPersonNameByID(int id) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Person res = null;
    String sql = "SELECT * from person WHERE id = ?";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setInt(1, id);
        statement.executeQuery().withCloseable { ResultSet rs ->
          if (rs.next()) {
            //        logger.info("email found!");
            String firstName = rs.getString("first_name")
            String lastName = rs.getString("last_name")
            String email = rs.getString("email")
            res = new Person(firstName, lastName, email)
          }
        }
      }
    } catch (SQLException e) {
      //      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    return res
  }

  int addLocationForPerson(String name, String street, String country, int zip, int personID) {
    int locationID = addLocation(name, street, country, zip)
    String sql2 = "INSERT INTO persons_locations(person_id, location_id) VALUES(?,?)";
    try {
      connection.prepareStatement(sql2).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.setInt(2, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locationID;
  }

  void removePerson(int personID) {
    String sql2 = "DELETE FROM person WHERE id = ?"
    try {
      connection.prepareStatement(sql2).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void removeLocationAndPerson(int personID, int locationID) {
    String sql1 = "DELETE FROM persons_locations WHERE person_id = ? AND location_id = ?"
    String sql2 = "DELETE FROM person WHERE id = ?"
    String sql3 = "DELETE FROM locations WHERE id = ?"
    try {
      connection.prepareStatement(sql1).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.setInt(2, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    removePerson(personID)
    try {
      connection.prepareStatement(sql3).withCloseable { PreparedStatement statement ->
        statement.setInt(1, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
