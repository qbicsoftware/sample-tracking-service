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
   * Adds a new location for a provided sample code, signifying the forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample must not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @return HttpResponse signifying the success status of the performed action
   * @since 1.0.0
   */
  HttpResponse addNewLocation(String sampleId, Location location)

  /**
   * Updates or adds a new location for a provided sample code, signifying an update to sample status or the
   * forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample may or may not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @return HttpResponse signifying the success status of the performed action
   * @since 1.0.0
   */
  HttpResponse updateLocation(String sampleId, Location location)

  /**
   * Returns sample location, status and history information given a sample identifier
   * @param sampleId the sample code of the sample in question
   * @return Sample object containing history and current location and status of the sample
   * @since 1.0.0
   */
  Sample searchSample(String sampleId)

  /**
   * Updates the status of a sample without changing its location
   * @param sampleId the sample code of the sample in question
   * @param status a Status object denoting the status to be set for the provided sample
   * @return boolean denoting if the update was successful
   * @since 1.0.0
   */
  boolean updateSampleStatus(String sampleId, Status status)
}