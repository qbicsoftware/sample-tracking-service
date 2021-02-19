package life.qbic.service

import life.qbic.datamodel.people.Contact
import life.qbic.datamodel.samples.Location

import javax.inject.Singleton

@Singleton
interface ILocationService {

  Contact searchPersonByEmail(String email)
  
  List<Location> listLocations()
  
  List<Location> getLocationsForEmail(String email)
}