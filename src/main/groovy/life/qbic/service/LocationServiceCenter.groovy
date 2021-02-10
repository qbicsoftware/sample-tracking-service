package life.qbic.service;

import java.util.List

import javax.inject.Inject
import javax.inject.Singleton

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.people.*
import life.qbic.datamodel.services.*
import life.qbic.datamodel.samples.*
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

  @Override
  List<Location> listLocations() {
    log.info "Listing all known locations"
    return database.listLocations();
  }

  @Override
  List<Location> getLocationsForEmail(String email) {
    log.info "Listing all known locations for person with e-mail "+email
    return database.getLocationsForEmail(email);
  }

  @Override
  List<Location> getLocationsForPerson(String identifier) {
    log.info "Listing all known locations for the person identified by $identifier"
    return database.getLocationsForPerson(identifier)
  }
}