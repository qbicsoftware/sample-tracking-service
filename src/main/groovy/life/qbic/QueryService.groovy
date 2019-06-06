package life.qbic;

import java.sql.Connection

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

interface QueryService {

  Contact searchPersonByEmail(String email)
  
  HttpResponse addNewLocation(String sampleId, Location location)
  
  HttpResponse updateLocation(String sampleId, Location location)
    
  Sample searchSample(String sampleId)
  
  boolean updateSampleStatus(String sampleId, Status status)
}
