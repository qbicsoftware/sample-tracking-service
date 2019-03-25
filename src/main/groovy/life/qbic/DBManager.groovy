package life.qbic

import java.sql.Connection
import java.sql.DriverManager

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

  private final String databaseURL
  private final String driverClass
  private Connection connection

  DBManager(@Property(name = 'app.db.host') String databaseHost,
  @Property(name = 'app.db.port') String databasePort,
  @Property(name = 'app.db.name') String databaseName,
  @Property(name = 'app.db.user') String databaseUser,
  @Property(name = 'app.db.pw') String userPassword,
  @Property(name = 'app.db.driver.class') String driverClass,
  @Property(name = 'app.db.driver.prefix') String driverPrefix) {
    databaseURL = driverPrefix + "://" + databaseHost + ":" + databasePort + "/" + databaseName;
    if (databasePort == null || databasePort.isEmpty()) {
      databaseURL = driverPrefix + ":" + databaseHost + "/" + databaseName;
    }
    this.driverClass = driverClass
    loginWithCredentials(new DatabaseCredentials(databaseUser, userPassword))
  }

  private void loginWithCredentials(DatabaseCredentials credentials) throws Exception{
    Class.forName(this.driverClass)
    connection = DriverManager.getConnection(this.databaseURL, credentials.user, credentials.password)
  }

  class DatabaseCredentials {
    String user, password

    DatabaseCredentials(String user, String password){
      this.user = user
      this.password = password
    }
  }
}
