package life.qbic.service;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

/**
 * Service interface to search location and contact information
 */
@Singleton
interface ILocationService {

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
   * List all known locations irrespective of user or samples at that location
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
   * List all locations attached to a person with the provided identifier.<br/>
   * Currently one email address is used as identifier for a person in the system
   * @param identifier the identifier for a person
   * @return a list of locations found
   * @since 1.1.0
   */
  List<Location> getLocationsForPerson(String identifier)
}
