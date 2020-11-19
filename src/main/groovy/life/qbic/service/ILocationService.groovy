package life.qbic.service;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

@Singleton
interface ILocationService {

  Contact searchPersonByEmail(String email)
  
  Contact searchPersonByUsername(String username)
  
  List<Location> listLocations()
  
  List<Location> getLocationsForEmail(String email)
  
  List<Location> getLocationsForUsername(String username)
}