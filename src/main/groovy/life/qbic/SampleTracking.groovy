package life.qbic

import javax.sql.DataSource
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator
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
class SampleTracking {
  static void main(String[] args) {
    log.info "Application started."
    registerShutdownHook()
    ApplicationContext ctx = Micronaut.run(SampleTracking.class)
  }

  static void registerShutdownHook() {
    Runtime.runtime.addShutdownHook(new Thread(new Runnable() {
          @Override
          void run() {
            log.info "Application shutting down ..."
            Configurator.shutdown(LogManager.getContext() as LoggerContext)
          }
        }))
  }
}