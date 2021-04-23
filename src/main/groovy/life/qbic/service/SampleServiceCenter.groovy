package life.qbic.service

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.IQueryService

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Log4j2
class SampleServiceCenter implements ISampleService {

  private final IQueryService database
  
  @Inject SampleServiceCenter(IQueryService database) {
    this.database = database
  }

  @Override
  void addNewLocation(String sampleId, Location location) {
    log.info "Adding new location "+location.name+" for sample "+sampleId
    database.addNewLocation(sampleId, location)
  }

  @Override
  void updateLocation(String sampleId, Location location) {
    log.info "Updating location to "+location.name+" for sample "+sampleId
    database.updateLocation(sampleId, location)
  }

  @Override
  Sample searchSample(String sampleId) {
    log.info "Fetching sample info for sample "+sampleId
    return database.searchSample(sampleId)
  }

  @Override
  void updateSampleStatus(String sampleId, Status status) {
    log.info "Updating sample status of "+sampleId+". The status will be changed to: "+status
    database.updateSampleStatus(sampleId, status)
  }
}