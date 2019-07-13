package life.qbic.controller

import io.micronaut.context.annotation.Parameter
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put

import javax.inject.Inject
import life.qbic.datamodel.services.*
import life.qbic.service.ISampleService

@Controller("/samples")
class SamplesController {

  ISampleService sampleService

  @Inject
  SamplesController(ISampleService sampleService) {
    this.sampleService = sampleService
  }

  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON) HttpResponse<Sample> sample(@Parameter('sampleId') String code) {
    if(!RegExValidator.isValidSampleCode(code)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      Sample s = sampleService.searchSample(code);
      if(s!=null) {
        return HttpResponse.ok(s);
      } else {
        return HttpResponse.notFound("Sample was not found in the system!");
      }
    }
  }

  @Post("/{sampleId}/currentLocation/")
  HttpResponse<Location> newLocation(@Parameter('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      return sampleService.addNewLocation(sampleId, location)
    }
  }

  /**
   * update or create location of a specific sample
   * @param sampleId sample code from the URL
   * @param location location object, transferred via json body
   * @return
   */
  @Put("/{sampleId}/currentLocation/")
  HttpResponse<Location> updateLocation(@Parameter('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      return sampleService.updateLocation(sampleId, location)
    }
  }

  @Put("/{sampleId}/currentLocation/{status}")
  HttpResponse sampleStatus(@Parameter('sampleId') String sampleId, @Parameter('status') Status status) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    }
    boolean found = sampleService.searchSample(sampleId)!=null;
    if(found) {
      sampleService.updateSampleStatus(sampleId, status);
      return HttpResponse.created("Sample status updated.");
    } else {
      return HttpResponse.notFound("Sample was not found in the system!");
    }
  }
}