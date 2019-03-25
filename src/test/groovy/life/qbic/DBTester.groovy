package life.qbic

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Inject
import life.qbic.model.Address
import life.qbic.model.Location
import life.qbic.model.Status

class DBTester {

  private final DBManager manager

  DBTester(String host, String port, String db, String user, String pw, String driver, String driverPrefix) {
    this.manager = new DBManager(host, port, db, user, pw, driver, driverPrefix)
  }

  void createTables() {
    String locations = "CREATE TABLE IF NOT EXISTS LOCATIONS"+
        "(ID INTEGER NOT NULL IDENTITY,"+
        "NAME VARCHAR(64) NOT NULL,"+
        "STREET VARCHAR(64) NOT NULL,"+
        "ZIP_CODE INTEGER NOT NULL,"+
        "COUNTRY VARCHAR(64) NOT NULL,"+
        "PRIMARY KEY (ID))"

    String persons = "CREATE TABLE IF NOT EXISTS PERSONS"+
        "(ID INTEGER NOT NULL IDENTITY,"+
        "USERNAME VARCHAR(8) NOT NULL,"+
        "TITLE VARCHAR(5),"+
        "FIRST_NAME VARCHAR(35) NOT NULL,"+
        "FAMILY_NAME VARCHAR(35) NOT NULL,"+
        "EMAIL VARCHAR(64) NOT NULL,"+
        "PHONE VARCHAR(50),"+
        "ACTIVE TINYINT DEFAULT 0,"+
        "PRIMARY KEY (ID))"

    String samples = "CREATE TABLE IF NOT EXISTS SAMPLES"+
        "(ID VARCHAR(14) NOT NULL,"+
        "CURRENT_LOCATION_ID INTEGER NOT NULL,"+
        "PRIMARY KEY (ID))"

    String samples_locations = "CREATE TABLE IF NOT EXISTS SAMPLES_LOCATIONS"+
        "(SAMPLE_ID VARCHAR(14) NOT NULL,"+
        "LOCATION_ID INTEGER NOT NULL,"+
        "ARRIVAL_TIME DATE,"+
        "FORWARDED_TIME DATE,"+
        "SAMPLE_STATUS VARCHAR(11) DEFAULT 'WAITING' NOT NULL,"+
        "responsible_person_id INTEGER NOT NULL)"

    String persons_locations = "CREATE TABLE IF NOT EXISTS PERSONS_LOCATIONS"+
        "(PERSON_ID INTEGER NOT NULL,"+
        "LOCATION_ID INTEGER NOT NULL)"

    try {
      Statement statement = manager.connection.createStatement()
      statement.executeUpdate(locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = manager.connection.createStatement()
      statement.executeUpdate(persons)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = manager.connection.createStatement()
      statement.executeUpdate(samples)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = manager.connection.createStatement()
      statement.executeUpdate(samples_locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = manager.connection.createStatement()
      statement.executeUpdate(persons_locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

  void addSample(String code, int locationId) {
    String sql = "INSERT INTO samples(id,current_location_id) VALUES(?,?)";
    try {
      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setString(1, code);
        statement.setInt(2, locationId);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  void addSampleLocation(String code, int locationId, Location loc, int personId) {
    String sql = "INSERT INTO samples_locations(sample_id,location_id,arrival_time,forwarded_time,sample_status,responsible_person_id) VALUES(?,?,?,?,?,?)";
    try {
      manager.connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
    return addPerson("user",p.getFirstName(), p.getLastName(),"email","phone")
  }

  int addPerson(String user, String first, String last, String email, String phone) {
    String sql = "INSERT INTO persons(username,first_name,family_name,email,phone) VALUES(?,?,?,?,?)";
    int res = -1;
    try {
      manager.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement statement ->
        statement.setString(1, user);
        statement.setString(2, first);
        statement.setString(3, last);
        statement.setString(4, email);
        statement.setString(5, phone);
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

  int addLocationForPerson(String name, String street, String country, int zip, int personID) {
    String sql1 = "INSERT INTO locations(name, street, zip_code, country) VALUES(?,?,?,?)"
    String sql2 = "INSERT INTO persons_locations(person_id, location_id) VALUES(?,?)";
    int locationID = -1;
    try {
      manager.connection.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement statement ->
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
      manager.connection.prepareStatement(sql2).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.setInt(2, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locationID;
  }

  void removeLocationAndPerson(int personID, int locationID) {
    String sql1 = "DELETE FROM persons_locations WHERE person_id = ? AND location_id = ?"
    String sql2 = "DELETE FROM persons WHERE id = ?"
    String sql3 = "DELETE FROM locations WHERE id = ?"
    try {
      manager.connection.prepareStatement(sql1).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.setInt(2, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      manager.connection.prepareStatement(sql2).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      manager.connection.prepareStatement(sql3).withCloseable { PreparedStatement statement ->
        statement.setInt(1, locationID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
