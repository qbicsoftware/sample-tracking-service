package life.qbic.db;

import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

@Singleton
interface IQueryService {

  Contact searchPersonByEmail(String email)
  
  Contact searchPersonByUsername(String username)
  
  List<Location> listLocations()
  
  List<Location> getLocationsForUsername(String username)
  
  List<Location> getLocationsForEmail(String email)

  HttpResponse addNewLocation(String sampleId, Location location)

  HttpResponse updateLocation(String sampleId, Location location)

  Sample searchSample(String sampleId)

  boolean updateSampleStatus(String sampleId, Status status)
}