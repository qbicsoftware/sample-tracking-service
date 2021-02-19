package life.qbic.service

import io.micronaut.http.HttpResponse
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status

import javax.inject.Singleton

@Singleton
interface ISampleService {

  HttpResponse addNewLocation(String sampleId, Location location)

  HttpResponse updateLocation(String sampleId, Location location)

  Sample searchSample(String sampleId)

  boolean updateSampleStatus(String sampleId, Status status)
}