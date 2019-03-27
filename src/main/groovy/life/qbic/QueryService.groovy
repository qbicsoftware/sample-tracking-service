package life.qbic;

import java.sql.Connection

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import life.qbic.model.Contact
import life.qbic.model.Location
import life.qbic.model.Sample
import life.qbic.model.Status

interface QueryService {

  Contact searchPersonByEmail(String email)
  
  HttpResponse addNewLocation(String sampleId, Location location)
  
  HttpResponse updateLocation(String sampleId, Location location)
    
  Sample searchSample(String sampleId)
  
  boolean updateSampleStatus(String sampleId, Status status)
}
