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
import life.qbic.api.rest.v2.samples.SamplesControllerV2
import life.qbic.api.rest.v2.samples.StatusChangeRequest
import life.qbic.auth.Authentication
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.datamodel.samples.Status
import life.qbic.db.INotificationService
import life.qbic.domain.sample.SampleCode
import life.qbic.domain.sample.SampleRepository
import life.qbic.service.IDummyLocationFactory
import life.qbic.service.ISampleService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import java.time.Instant

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/samples")
class SamplesController {

  ISampleService sampleService
  INotificationService notificationService
  SamplesControllerV2 controllerV2

  private static final Logger log = LogManager.getLogger(SamplesController.class)
  private SampleRepository sampleRepository
  private IDummyLocationFactory dummyLocationService

  @Inject
  SamplesController(ISampleService sampleService, INotificationService notificationService, SamplesControllerV2 controllerV2, SampleRepository sampleRepository, IDummyLocationFactory dummyLocationService) {
    this.sampleService = sampleService
    this.notificationService = notificationService
    this.controllerV2 = controllerV2
    this.sampleRepository = sampleRepository
    this.dummyLocationService = dummyLocationService
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
    def optionalSample = this.sampleRepository.get(SampleCode.fromString(sampleId))
    if (!optionalSample.isPresent()) {
      return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample with ID ${sampleId} was not found in the system!")
    }
    life.qbic.domain.sample.Sample sampleV2 = optionalSample.get()
    Location dummyLocation = this.dummyLocationService.dummyLocation(sampleV2.currentState().status(), sampleV2.currentState().statusValidSince().toDate())

    Sample sampleV1 = new Sample(code: sampleId, currentLocation: dummyLocation, pastLocations: [])
    return HttpResponse.ok(sampleV1)
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
      controllerV2.moveSampleToStatus(sampleId, new StatusChangeRequest(
              location.getStatus().toString(),
              Instant.now().toString()))
      sampleService.addNewLocation(sampleId, location)
        return HttpResponse.ok(location)
    } catch (IllegalArgumentException illegalArgumentException) {
      log.error(illegalArgumentException)
      return HttpResponse.status(HttpStatus.BAD_REQUEST, illegalArgumentException.message)
    } catch(Exception e) {
      log.error(e)
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
      controllerV2.moveSampleToStatus(sampleId, new StatusChangeRequest(
              location.getStatus().toString(),
              Instant.now().toString()))
      sampleService.updateLocation(sampleId, location)
      return HttpResponse.ok(location)
    } catch (IllegalArgumentException illegalArgumentException) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, illegalArgumentException.message)
    } catch(Exception e){
      log.error(e)
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
    if (!RegExValidator.isValidSampleCode(sampleId)) {
      return HttpResponse.status(HttpStatus.BAD_REQUEST, "${sampleId} is not a valid sample identifier!")
    }
    if (!sampleRepository.get(SampleCode.fromString(sampleId)).isPresent()) {
      return HttpResponse.status(HttpStatus.NOT_FOUND, "Sample with ID ${sampleId} was not found in the system!")
    }
    try {
      controllerV2.moveSampleToStatus(sampleId, new StatusChangeRequest(
              status.toString(),
              Instant.now().toString()))
      sampleService.updateSampleStatus(sampleId, status)
      return HttpResponse.status(HttpStatus.CREATED, "Sample status updated to ${status}.")
    } catch (Exception e) {
      log.error(e)
      return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }
  }
}
