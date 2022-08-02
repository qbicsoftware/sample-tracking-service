package life.qbic.api.rest.v2.samples;


import static org.slf4j.LoggerFactory.getLogger;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import life.qbic.application.ApplicationException;
import life.qbic.application.SampleService;
import life.qbic.auth.Authentication;
import life.qbic.domain.InvalidDomainException;
import life.qbic.domain.notification.INotificationRepository;
import life.qbic.domain.sample.Sample.CurrentState;
import life.qbic.domain.sample.SampleEventDatasource;
import life.qbic.domain.sample.SampleEventStore;
import life.qbic.domain.sample.SampleRepository;
import life.qbic.domain.sample.Status;
import org.slf4j.Logger;

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/samples")
public class SamplesControllerV2 {

  private static final Logger log = getLogger(SamplesControllerV2.class);

  SampleService sampleService;

  @Inject
  public SamplesControllerV2(SampleEventDatasource sampleEventDatasource, INotificationRepository notificationRepository) {
    this.sampleService = new SampleService(
        new SampleRepository(new SampleEventStore(sampleEventDatasource)), notificationRepository);
  }

  @Operation(summary = "Assign a status to a dedicated sample.",
      description = "Registers the sample with the provided code to be in the provide status. The status is valid from the instant specified.")
  @ApiResponse(responseCode = "200", description = "The request was fulfilled. The sample was registered to have the provided status.")
  @Put(uri = "/{sampleCode}/status")
  @RolesAllowed("WRITER")
  public HttpResponse<?> moveSampleToStatus(@PathVariable String sampleCode,
      @Body StatusChangeRequest statusChangeRequest) {
    log.info(String.format("Request to put sample %s in status %s valid since %s", sampleCode,
        statusChangeRequest.status(), statusChangeRequest.validSince()));
    String validSince = statusChangeRequest.validSince();
    String requestedStatus = statusChangeRequest.status();
    if (SampleStatusDto.METADATA_REGISTERED.name().equals(requestedStatus)) {
      sampleService.registerMetadata(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_RECEIVED.name().equals(requestedStatus)) {
      sampleService.receiveSample(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_QC_FAIL.name().equals(requestedStatus)) {
      sampleService.failQualityControl(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_QC_PASS.name().equals(requestedStatus)) {
      sampleService.passQualityControl(sampleCode, validSince);

    } else if (SampleStatusDto.LIBRARY_PREP_FINISHED.name().equals(requestedStatus)) {
      sampleService.prepareLibrary(sampleCode, validSince);

    } else if (SampleStatusDto.DATA_AVAILABLE.name().equals(requestedStatus)) {
      sampleService.provideData(sampleCode, validSince);

    } else {
      /* this is unnecessary in Java 17 using enhanced switch methods should never be reached if all enum values are handled here.*/
      throw new IllegalArgumentException(
          "Provided sample status not recognized: "
              + requestedStatus);
    }
    log.info(String.format("Sample %s is in status %s valid since %s", sampleCode, statusChangeRequest.status(), statusChangeRequest.validSince()));
    return HttpResponse.ok();
  }

  @Operation(summary = "Request information about the current status of a sample.",
      description = "Delivers the current status of a sample in the system.")
  @ApiResponse(responseCode = "200", description = "The request was fulfilled. The current status is provided in the response body.")
  @Get(uri = "/{sampleCode}/status")
  @RolesAllowed("READER")
  @Produces(MediaType.APPLICATION_JSON)
  public HttpResponse<SampleStatusResponse> getSampleStatus(@PathVariable String sampleCode) {
    log.info("Retrieving status for " + sampleCode);
    CurrentState sampleState = sampleService.getSampleState(sampleCode);
    Status sampleStatus = sampleState.status();
    SampleStatusDto statusDto;
    switch (sampleStatus) {
      case METADATA_REGISTERED:
        statusDto = SampleStatusDto.METADATA_REGISTERED;
        break;
      case SAMPLE_RECEIVED:
        statusDto = SampleStatusDto.SAMPLE_RECEIVED;
        break;
      case SAMPLE_QC_PASSED:
        statusDto = SampleStatusDto.SAMPLE_QC_PASS;
        break;
      case SAMPLE_QC_FAILED:
        statusDto = SampleStatusDto.SAMPLE_QC_FAIL;
        break;
      case LIBRARY_PREP_FINISHED:
        statusDto = SampleStatusDto.LIBRARY_PREP_FINISHED;
        break;
      case DATA_AVAILABLE:
        statusDto = SampleStatusDto.DATA_AVAILABLE;
        break;
      default:
        /* this is unnecessary in Java 17 using enhanced switch methods should never be reached if all enum values are handled here.*/
        throw new IllegalArgumentException(
            "Provided sample status not recognized: "
                + sampleStatus.name());
    }
    log.info(String.format("Found sample %s with status %s", sampleCode, statusDto.name()));
    SampleStatusResponse responseBody = new SampleStatusResponse(sampleCode,
        statusDto.name(),
        sampleState.validSince());
    return HttpResponse.ok(responseBody);
  }

  @Error(InvalidDomainException.class)
  HttpResponse<String> onDomainError(InvalidDomainException invalidDomainException) {
    log.error(invalidDomainException.getMessage(), invalidDomainException);
    return HttpResponse.serverError("Apologies! Your request could not be processed.");
  }

  @Error(ApplicationException.class)
  HttpResponse<String> onApplicationError(ApplicationException applicationException) {
    log.error(applicationException.getMessage(), applicationException);
    return HttpResponse.serverError("Apologies! Your request could not be processed.");
  }

  @Error(IllegalArgumentException.class)
  HttpResponse<String> onIllegalArguments(IllegalArgumentException illegalArgumentException) {
    log.error(illegalArgumentException.getMessage(), illegalArgumentException);
    return HttpResponse.status(HttpStatus.BAD_REQUEST);
  }

  @Error(Exception.class)
  HttpResponse<String> onOtherError(Exception e) {
    log.error(e.getMessage(), e);
    return HttpResponse.serverError("Apologies! Your request could not be processed.");
  }


}
