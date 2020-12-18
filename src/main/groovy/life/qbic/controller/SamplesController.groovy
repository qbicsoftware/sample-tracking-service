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
import life.qbic.datamodel.services.Location
import life.qbic.datamodel.services.Sample
import life.qbic.datamodel.services.Status
import life.qbic.db.NotFoundException
import life.qbic.micronaututils.auth.Authentication
import life.qbic.service.ISampleService

import javax.annotation.security.RolesAllowed
import javax.inject.Inject

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/samples")
class SamplesController {

  ISampleService sampleService

  @Inject
  SamplesController(ISampleService sampleService) {
    this.sampleService = sampleService
  }

  @Operation(summary = "Request a sample's tracking information",
          description = "Requests a sample resource with the given identifier.",
          tags = "Sample")
  @ApiResponse(
          responseCode = "200", description = "Returns a sample with tracking information", content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Sample.class)))
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample not found")
  @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed([ "READER", "WRITER"])
  HttpResponse<Sample> sample(@PathVariable('sampleId') String code) {
    if(!RegExValidator.isValidSampleCode(code)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "Not a valid sample code!")
    } else {
      Sample s = sampleService.searchSample(code)
      if(s!=null) {
        return HttpResponse.ok(s)
      } else {
        return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample was not found in the system!")
      }
    }
  }

  @Post("/{sampleId}/currentLocation/")
  @Operation(summary = "Sets a sample's current location",
          description = "Sets a sample current location with the given identifier.",
          tags = "Sample Location")
  @ApiResponse(responseCode = "200", description = "Current location for sample set successfully")
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample not found")
  @RolesAllowed("WRITER")
  HttpResponse<Location> newLocation(@PathVariable('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "Not a valid sample code!")
    }
    try{
      sampleService.addNewLocation(sampleId, location)
      return HttpResponse.ok(location)
    } catch(Exception e) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST, "Could not add Sample to Location in the system!")
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
  @ApiResponse(responseCode = "404", description = "Sample not found")
  @RolesAllowed("WRITER")
  HttpResponse<Location> updateLocation(@PathVariable('sampleId') String sampleId, Location location) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "Not a valid sample code!")
    }
    try {
      sampleService.updateLocation(sampleId, location)
      return HttpResponse.ok(location)
    }
    catch(Exception e){
      return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample and Location were not found in the system!")
    }
  }

  @Put("/{sampleId}/currentLocation/{status}")
  @Operation(summary = "Sets a sample's current location status",
          description = "Sets a sample current location status with the given identifier.",
          tags = "Sample Status")
  @ApiResponse(responseCode = "200", description = "Current location for sample set successfully")
  @ApiResponse(responseCode = "400", description = "Sample identifier format does not match")
  @ApiResponse(responseCode = "401", description = "Unauthorized access")
  @ApiResponse(responseCode = "404", description = "Sample not found")
  @RolesAllowed("WRITER")
  HttpResponse sampleStatus(@PathVariable('sampleId') String sampleId, @PathVariable('status') Status status) {
    if(!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "Not a valid sample code!")
    }
    try{
      sampleService.searchSample(sampleId)
      sampleService.updateSampleStatus(sampleId, status)
      return HttpResponse.status(HttpStatus.CREATED, "Sample status updated.")
    }
    catch(Exception e) {
      return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample was not found in the system!")
    }
  }
}