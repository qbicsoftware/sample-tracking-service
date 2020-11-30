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
   */
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
   */
  List<Location> getLocationsForEmail(String email)
}