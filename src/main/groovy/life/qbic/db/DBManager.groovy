package life.qbic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import org.mariadb.*;

import javax.inject.Singleton;

import groovy.lang.Grab
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;

@Singleton
class DBManager {

  private final String url
  private final String user
  private final String password
  private final String driverClass
  private final Connection connection

  DBManager(@Property(name ="datasources.default.url") String url,
  @Property(name ="datasources.default.driver") String driver,
  @Property(name ="datasources.default.username") String username,
  @Property(name ="datasources.default.password") String password) {
    this.url = url;
    this.user = username
    this.password = password
    this.driverClass = driver
    this.connection = login()
  }

  private void logout(Connection conn) {
    if (conn != null)
      conn.close()
  }

  Connection getConnection() {
    return connection;
  }

  private Connection login() {
    Connection conn = null
    try {
      Class.forName(this.driverClass)
      conn = DriverManager.getConnection(this.url, this.user, this.password)
    } catch (Exception e) {
      e.printStackTrace()
    }
    return conn
  }
}
