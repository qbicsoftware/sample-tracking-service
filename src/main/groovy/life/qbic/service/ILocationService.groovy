package life.qbic.service;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

/**
 * //TODO describe this interface
 */
@Singleton
interface ILocationService {

  /**
   * //TODO describe intended method behaviour
   * @param email
   * @return TODO
   * @since TODO
   */
  Contact searchPersonByEmail(String email)

  /**
   * //TODO describe intended method behaviour
   * @return TODO
   * @since TODO
   */
  List<Location> listLocations()

  /**
   * //TODO describe intended method behaviour
   * @param email
   * @return TODO
   * @since TODO
   */
  List<Location> getLocationsForEmail(String email)
}