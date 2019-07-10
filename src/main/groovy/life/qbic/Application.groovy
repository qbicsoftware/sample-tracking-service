package life.qbic

import javax.sql.DataSource

import org.mariadb.jdbc.MariaDbDataSource
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.runtime.Micronaut
import java.sql.Connection

@CompileStatic
@Log4j2
class Application {
  static void main(String[] args) {
    ApplicationContext ctx = Micronaut.run(Application.class)

    //    ApplicationContext applicationContext = ApplicationContext.build().build().registerSingleton(IQueryService, mariaDB)
    Environment environment = ctx.getEnvironment();

    String url = environment.getProperty("datasources.default.url", String.class).get()
    String user = environment.getProperty("datasources.default.username", String.class).get()
    String pw = environment.getProperty("datasources.default.password", String.class).get()
    String driver = environment.getProperty("datasources.default.driver-class-name", String.class).get()
//    
//    MariaDbDataSource ds = new MariaDbDataSource(url)
//    ds.setPassword(pw);
//    ds.setUserName(user)
//    Connection con = ds.getConnection()
//    println con
//    Sql sql = new Sql(con);
//    final String query = "SELECT * from persons;"
//    List<GroovyRowResult> results = sql.rows(query)
//    println results.size()
//    for(GroovyRowResult r : results) {
//      println r.result
//    }
    println url
    println user
    println driver
    println pw
  }
}