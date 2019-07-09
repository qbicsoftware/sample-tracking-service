package life.qbic.service;

import javax.inject.Inject
import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*
import life.qbic.db.IQueryService

@Singleton
class LocationServiceCenter implements ILocationService {

  private final IQueryService database

  @Inject LocationServiceCenter(IQueryService database) {
    this.database = database
  }

  @Override
  Contact searchPersonByEmail(String email) {
    return database.searchPersonByEmail(email)
  }
}