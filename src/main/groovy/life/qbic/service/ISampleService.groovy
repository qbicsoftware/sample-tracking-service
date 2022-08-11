package life.qbic.service


import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.NotFoundException

import javax.inject.Singleton

/**
 * Service interface to search and update sample information
 */
@Singleton
interface ISampleService {

  /**
   * Adds a new location for a provided sample code, signifying the forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample must not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @since 1.0.0
   * @throws IllegalArgumentException when the location or sampleId is not allowed
   */
  void addNewLocation(String sampleId, Location location) throws IllegalArgumentException

  /**
   * Updates or adds a new location for a provided sample code, signifying an update to sample status or the
   * forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample may or may not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @since 1.0.0
   * @throws IllegalArgumentException when the location or sampleId is not allowed
   */
  void updateLocation(String sampleId, Location location) throws IllegalArgumentException

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
   * @since 1.0.0
   * @throws NotFoundException in case the sampleId could not be found
   */
   void updateSampleStatus(String sampleId, Status status) throws NotFoundException
}
