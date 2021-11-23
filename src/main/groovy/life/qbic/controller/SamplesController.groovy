package life.qbic.controller


import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.auth.Authentication
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.INotificationService
import life.qbic.service.ISampleService

import javax.annotation.security.RolesAllowed
import javax.inject.Inject

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/samples")
class SamplesController {

  ISampleService sampleService
  INotificationService notificationService

  @Inject
  SamplesController(ISampleService sampleService, INotificationService notificationService) {
    this.sampleService = sampleService
    this.notificationService = notificationService
  }

  @Operation(summary = "Request a sample's tracking information",
          description = "Requests a sample resource with the given identifier.",
          tags = "Sample")
  @ApiResponse(
          responseCode = "200", description = "Returns a sample with tracking information", content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Sample.class)))
  @ApiResponse(responseCode = "400", description = "Bad Request, Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample tracking information for the provided identifier not found")
  @ApiResponse(responseCode = "500", description = "Sample tracking information retrieval failed for an unknown reason")
  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed([ "READER", "WRITER"])
  HttpResponse<Sample> sample(@PathVariable('sampleId') String sampleId) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "${sampleId} is not a valid sample identifier!")
    }

    try {
      Sample sample = sampleService.searchSample(sampleId)
      if(sample) {
        return HttpResponse.ok(sample)
      }
      else {
        return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample with ID ${sampleId} was not found in the system!")
      }
    }
      catch(Exception e) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
      }
    }

  @Post("/{sampleId}/currentLocation/")
  @Operation(summary = "Sets a sample's current location",
          description = "Sets a sample current location with the given identifier.",
          tags = "Sample Location")
  @ApiResponse(responseCode = "200", description = "Current location for sample set successfully")
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "500", description = "Update of sample location failed for an unknown reason")
  @RolesAllowed("WRITER")
  HttpResponse<Location> newLocation(@PathVariable('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "${sampleId} is not a valid sample identifier!")
    }
    try{
        sampleService.addNewLocation(sampleId, location)
        notificationService.sampleChanged(sampleId, location.getStatus())
        return HttpResponse.ok(location)
    } catch (IllegalArgumentException illegalArgumentException) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, illegalArgumentException.message)
    } catch(Exception e) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }
  }

  /**
   * update or create location of a specific sample
   * @param sampleId sample code from the URL
   * @param location location object, transferred via json body
   * @return
   */
  @Put("/{sampleId}/currentLocation/")
  @Operation(summary = "Updates a sample's current location",
          description = "Updates a sample current location with the given identifier.",
          tags = "Sample Location")
  @ApiResponse(responseCode = "200", description = "Current location for sample set successfully")
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample for the provided identifier not found")
  @ApiResponse(responseCode = "500", description = "Update of current sample location failed for an unknown reason")
  @RolesAllowed("WRITER")
  HttpResponse<Location> updateLocation(@PathVariable('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "${sampleId} is not a valid sample identifier!")
    }
    try {
      sampleService.updateLocation(sampleId, location)
      notificationService.sampleChanged(sampleId, location.getStatus())
      return HttpResponse.ok(location)
    } catch (IllegalArgumentException illegalArgumentException) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, illegalArgumentException.message)
    } catch(Exception e){
      return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }
  }

  @Put("/{sampleId}/currentLocation/{status}")
  @Operation(summary = "Sets a sample's current location status",
          description = "Sets a sample current location status with the given identifier.",
          tags = "Sample Status")
  @ApiResponse(responseCode = "201", description = "Current location for sample set successfully")
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample for the provided identifier not found")
  @ApiResponse(responseCode = "500", description = "Update of sample location failed for an unknown reason")
  @RolesAllowed("WRITER")
  HttpResponse sampleStatus(@PathVariable('sampleId') String sampleId, @PathVariable('status') Status status) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "${sampleId} is not a valid sample identifier!")
    }
    try {
      if (null != sampleService.searchSample(sampleId)) {
        sampleService.updateSampleStatus(sampleId, status)
        notificationService.sampleChanged(sampleId, status)
        return HttpResponse.status(HttpStatus.CREATED, "Sample status updated to ${status}.")
      } else {
        return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample with ID ${sampleId} was not found in the system!")
      }
    }
    catch(Exception e){
      return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }
  }
}