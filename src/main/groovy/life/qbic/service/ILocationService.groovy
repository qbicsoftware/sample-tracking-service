package life.qbic.service;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

@Singleton
interface ILocationService {

  Contact searchPersonByEmail(String email)
  
  List<Location> listLocations()
  
  List<Location> getLocationsForEmail(String email)
}