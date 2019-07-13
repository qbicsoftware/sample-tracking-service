package life.qbic.service;

import javax.inject.Inject
import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*
import life.qbic.db.IQueryService

@Singleton
class SampleServiceCenter implements ISampleService {

  private final IQueryService database
  
  @Inject SampleServiceCenter(IQueryService database) {
    this.database = database
  }

  @Override
  HttpResponse addNewLocation(String sampleId, Location location) {
    return database.addNewLocation(sampleId, location)
  }

  @Override
  HttpResponse updateLocation(String sampleId, Location location) {
    return database.updateLocation(sampleId, location)
  }

  @Override
  Sample searchSample(String sampleId) {
    return database.searchSample(sampleId)
  }

  @Override
  boolean updateSampleStatus(String sampleId, Status status) {
    return database.updateSampleStatus(sampleId, status)
  }
}