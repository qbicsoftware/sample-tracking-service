package life.qbic.service;

import javax.inject.Inject
import javax.inject.Singleton

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*
import life.qbic.db.IQueryService

@Log4j2
@Singleton
class LocationServiceCenter implements ILocationService {

  private final IQueryService database

  @Inject LocationServiceCenter(IQueryService database) {
    this.database = database
  }

  @Override
  Contact searchPersonByEmail(String email) {
    log.info "Searching for person with e-mail "+email
    return database.searchPersonByEmail(email)
  }
}