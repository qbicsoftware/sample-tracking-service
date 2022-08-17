package life.qbic.db


import life.qbic.datamodel.people.Contact
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status

import javax.inject.Singleton

/**
 * Interface for database queries
 */
@Singleton
interface IQueryService {

  /**
   * Returns the contact using the provided email address
   * @param email email address
   * @return Contact object of the contact with the provided email address
   * @since 1.0.0
   * @deprecated As of version 1.1.0 this method should no longer be used and might be removed in the future.
   */
  //@Deprecated(since="1.1.0", forRemoval=true) //TODO this can be enabled in Java 11
  @Deprecated
  Contact searchPersonByEmail(String email)

  /**
   * List all locations found in the database
   * @return list of Location objects
   * @since 1.0.0
   */
  List<Location> listLocations()

  /**
   * List all locations attached to a person based on the provided email
   * @param email email address
   * @return list of Location objects
   * @since 1.0.0
   * @deprecated As of version 1.1.0 this method should no longer be used and will be removed in the future. {@link #getLocationsForPerson} should be used instead.
   */
  //@Deprecated(since="1.1.0", forRemoval=true) //TODO this can be enabled in Java 11
  @Deprecated
  List<Location> getLocationsForEmail(String email)

  /**
   * List all locations attached to a person with the provided identifier
   * Currently one email address is used as identifier for a person in the system
   * @param identifier the identifier for a person
   * @return a list of locations for the given person, if the query was successful
   * @since 1.1.0
   */
  List<Location> getLocationsForPerson(String identifier)

  /**
   * Adds a new location for a provided sample code to the database, signifying the forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample must not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @return HttpResponse signifying the success status of the performed action
   * @since 1.0.0
   * @throws IllegalArgumentException when the sampleId or the location is not allowed
   */
  void addNewLocation(String sampleId, Location location) throws IllegalArgumentException

  /**
   * Updates or adds a new location for a provided sample code to the database, signifying an update to sample status or the
   * forwarding of that sample to that location.
   * The location object must exist in the database. The provided sample may or may not be at that location currently.
   * @param sampleId sample code
   * @param location Location object signifying the location of the sample
   * @since 1.0.0
   * @throws IllegalArgumentException when the sampleId or the location is not allowed
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
   * Updates the status of a sample in the database without changing its location
   * @param sampleId the sample code of the sample in question
   * @param status a Status object denoting the status to be set for the provided sample
   * @return boolean denoting if the update was successful
   * @since 1.0.0
   */
  void updateSampleStatus(String sampleId, Status status) throws NotFoundException
}
