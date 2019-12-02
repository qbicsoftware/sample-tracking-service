package life.qbic.controller

import io.micronaut.context.annotation.Parameter
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import life.qbic.micronaututils.auth.Authentication

import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import life.qbic.datamodel.services.*
import life.qbic.service.ISampleService

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/samples")
class SamplesController {

  ISampleService sampleService

  @Inject
  SamplesController(ISampleService sampleService) {
    this.sampleService = sampleService
  }

  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed([ "READER", "WRITER"])
  HttpResponse<Sample> sample(@Parameter('sampleId') String code) {
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
  @RolesAllowed("WRITER")
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
  @RolesAllowed("WRITER")
  HttpResponse<Location> updateLocation(@Parameter('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.badRequest("Not a valid sample code!");
    } else {
      return sampleService.updateLocation(sampleId, location)
    }
  }

  @Put("/{sampleId}/currentLocation/{status}")
  @RolesAllowed("WRITER")
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