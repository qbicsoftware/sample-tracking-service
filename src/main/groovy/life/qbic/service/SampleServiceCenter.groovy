package life.qbic.service;

import javax.inject.Inject
import javax.inject.Singleton

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*
import life.qbic.db.IQueryService

@Singleton
@Log4j2
class SampleServiceCenter implements ISampleService {

  private final IQueryService database
  
  @Inject SampleServiceCenter(IQueryService database) {
    this.database = database
  }

  @Override
  HttpResponse addNewLocation(String sampleId, Location location) {
    log.info "Adding new location "+location.name+" for sample "+sampleId
    return database.addNewLocation(sampleId, location)
  }

  @Override
  HttpResponse updateLocation(String sampleId, Location location) {
    log.info "Updating location to "+location.name+" for sample "+sampleId
    return database.updateLocation(sampleId, location)
  }

  @Override
  Sample searchSample(String sampleId) {
    log.info "Fetching sample info for sample "+sampleId
    return database.searchSample(sampleId)
  }

  @Override
  boolean updateSampleStatus(String sampleId, Status status) {
    log.info "Updating sample status of "+sampleId+". The status will be changed to: "+status
    return database.updateSampleStatus(sampleId, status)
  }
}