package life.qbic

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.inject.Inject

class DBTester {

  private final DBManagerTester manager
  
  DBTester(String host, String port, String db, String user, String pw, String driver, String driverPrefix) {
    this.manager = new DBManagerTester(host, port, db, user, pw, driver, driverPrefix)
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
    String sql1 = "DELETE * FROM persons_locations WHERE person_id = ? AND location_id = ?"
    String sql2 = "DELETE * FROM persons WHERE id = ?"
    String sql3 = "DELETE * FROM locations WHERE id = ?"
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
