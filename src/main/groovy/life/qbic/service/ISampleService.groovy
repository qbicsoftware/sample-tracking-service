package life.qbic.service;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

/**
 * //TODO describe this interface
 */
@Singleton
interface ISampleService {

  /**
   * //TODO describe intended method behaviour
   * @param sampleId
   * @param location
   * @return TODO
   * @since TODO
   */
  HttpResponse addNewLocation(String sampleId, Location location)

  /**
   * //TODO describe intended method behaviour
   * @param sampleId
   * @param location
   * @return TODO
   * @since TODO
   */
  HttpResponse updateLocation(String sampleId, Location location)

  /**
   * //TODO describe intended method behaviour
   * @param sampleId
   * @return TODO
   * @since TODO
   */
  Sample searchSample(String sampleId)

  /**
   * //TODO describe intended method behaviour
   * @param sampleId
   * @param status
   * @return TODO
   * @since TODO
   */
  boolean updateSampleStatus(String sampleId, Status status)
}