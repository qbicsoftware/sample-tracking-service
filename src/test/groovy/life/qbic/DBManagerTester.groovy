package life.qbic;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

import javax.inject.Singleton

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires

class DBManagerTester {

  private final String dbURL
  private final String userName
  private final String password
  private final String driverPrefix
  private final String driverClass
  private final Connection connection

  DBManagerTester(String host, String port, String name, String user, String pw, String driverClass, String driverPrefix) {
    this.dbURL = driverPrefix + "://" + host + ":" + port + "/" + name
    this.userName = user
    this.password = pw
    this.driverClass = driverClass
    this.connection = login()
  }

  private void logout(Connection conn) {
    if (conn != null)
      conn.close()
  }

  Connection getConnection() {
    return connection
  }

  private Connection login() {
    Connection conn = null
    try {
      Class.forName(this.driverClass)
      conn = DriverManager.getConnection(this.dbURL, this.userName, this.password)
    } catch (Exception e) {
      e.printStackTrace()
    }
    return conn
  }
}
