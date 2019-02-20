package life.qbic;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

import javax.inject.Singleton

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires

@Singleton
@Requires(property = "app.db.host")
@Requires(property = "app.db.port")
@Requires(property = "app.db.name")
@Requires(property = "app.db.pw")
@Requires(property = "app.db.user")
@Requires(property = "app.db.driver.class")
@Requires(property = 'app.db.driver.prefix')
class DBManager {

  private final String dbURL
  private final String userName
  private final String password
  private final String driverPrefix
  private final String driverClass
  private final Connection connection

  DBManager(@Property(name = 'app.db.host') String host,
            @Property(name = 'app.db.port') String port,
            @Property(name = 'app.db.name') String name,
            @Property(name = 'app.db.user') String user,
            @Property(name = 'app.db.pw') String pw,
            @Property(name = 'app.db.driver.class') String driverClass,
            @Property(name = 'app.db.driver.prefix') String driverPrefix) {
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
