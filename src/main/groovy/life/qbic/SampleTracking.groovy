package life.qbic

import groovy.util.logging.Log4j2
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator

@OpenAPIDefinition(
        info = @Info(
                title = "Sample Tracking Service",
                version = "1.0.3",
                description = "Information about sample status and location information for QBiC's data management platform.",
                license = @License(name = "", url = ""),
                contact = @Contact(url = "https://github.com/sven1103", name = "Sven Fillinger", email = "sven.filliner@qbic.uni-tuebingen.de")
        )
)
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
