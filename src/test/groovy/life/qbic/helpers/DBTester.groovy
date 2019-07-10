package life.qbic.helpers

import java.sql.DriverManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement
import java.text.DateFormat
import java.text.SimpleDateFormat

import javax.inject.Inject
import javax.inject.Singleton

import life.qbic.datamodel.services.*
import life.qbic.micronaututils.QBiCDataSource

class DBTester {

  private Connection connection

//  @Inject MariaDBManager(QBiCDataSource dataSource) {
//    this.manager = dataSource
//  }

  public void loginWithCredentials(String driver, String url, String user, String pw) throws Exception{
    println driver
    Class.forName(driver)
    connection = DriverManager.getConnection(url, user, pw)
  }
  
//  DBTester(String host, String port, String db, String user, String pw, String driver, String driverPrefix) {
//    this.manager = new (host, port, db, user, pw, driver, driverPrefix)
//  }

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
        "ARRIVAL_TIME DATETIME WITH TIME ZONE,"+
        "FORWARDED_TIME DATETIME WITH TIME ZONE,"+
        "SAMPLE_STATUS VARCHAR(11) DEFAULT 'WAITING' NOT NULL,"+
        "responsible_person_id INTEGER NOT NULL)"

    String persons_locations = "CREATE TABLE IF NOT EXISTS PERSONS_LOCATIONS"+
        "(PERSON_ID INTEGER NOT NULL,"+
        "LOCATION_ID INTEGER NOT NULL)"

    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate(locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate(persons)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate(samples)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
      statement.executeUpdate(samples_locations)
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      Statement statement = connection.createStatement()
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

  void addSample(String code, int locationId) {
    String sql = "INSERT INTO samples(id,current_location_id) VALUES(?,?)";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
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
    return addPerson("user",p.getFirstName(), p.getLastName(),p.getEMail(),"phone")
  }

  int addPerson(String user, String first, String last, String email, String phone) {
    String sql = "INSERT INTO persons(username,first_name,family_name,email,phone) VALUES(?,?,?,?,?)";
    int res = -1;
    try {
      connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS).withCloseable { PreparedStatement statement ->
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
    return locationID
  }

  Sample searchSample(String code) {
    //    logger.info("Looking for user with email " + email + " in the DB");
    Sample res = null;
    String sql = "SELECT * from samples INNER JOIN samples_locations ON samples.id = samples_locations.sample_id "+
        "INNER JOIN locations ON samples_locations.location_id = locations.id "+
        "WHERE UPPER(samples.id) = UPPER(?)";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setString(1, code);
        statement.executeQuery().withCloseable { ResultSet rs ->
          List<Location> pastLocs = new ArrayList<>()
          Location currLoc = null;
          while (rs.next()) {
            int currID = rs.getInt("current_location_id");
            int locID = rs.getInt("location_id");
            Date arrivalDate = new Date(rs.getTimestamp("arrival_time").getTime())
            Date forwardedDate = new Date(rs.getTimestamp("forwarded_time").getTime());
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
    String sql = "SELECT * from persons WHERE id = ?";
    try {
      connection.prepareStatement(sql).withCloseable { PreparedStatement statement ->
        statement.setInt(1, id);
        statement.executeQuery().withCloseable { ResultSet rs ->
          if (rs.next()) {
            //        logger.info("email found!");
            String firstName = rs.getString("first_name")
            String lastName = rs.getString("family_name")
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

  void removeLocationAndPerson(int personID, int locationID) {
    String sql1 = "DELETE FROM persons_locations WHERE person_id = ? AND location_id = ?"
    String sql2 = "DELETE FROM persons WHERE id = ?"
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
    try {
      connection.prepareStatement(sql2).withCloseable { PreparedStatement statement ->
        statement.setInt(1, personID);
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
