package life.qbic.api.rest.v2.samples;

import static org.slf4j.LoggerFactory.getLogger;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import life.qbic.application.SampleService;
import life.qbic.auth.Authentication;
import life.qbic.domain.sample.Sample.CurrentState;
import life.qbic.domain.sample.Status;
import life.qbic.exception.ErrorCode;
import life.qbic.exception.ErrorParameters;
import life.qbic.exception.UnrecoverableException;
import org.slf4j.Logger;

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/samples")
public class SamplesControllerV2 {

  private static final Logger log = getLogger(SamplesControllerV2.class);

  SampleService sampleService;

  @Inject
  public SamplesControllerV2(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  @Operation(summary = "Assign a status to a dedicated sample.",
      description = "Registers the sample with the provided code to be in the provide status. The status is valid from the instant specified.",
      tags = "Sample Status")
  @ApiResponse(responseCode = "200", description = "The request was fulfilled. The sample was registered to have the provided status.")
  @Put(uri = "/{sampleCode}/status")
  @RolesAllowed("WRITER")
  public HttpResponse<?> moveSampleToStatus(@PathVariable String sampleCode,
      @Body StatusChangeRequest statusChangeRequest) {
    log.info(String.format("Request to put sample %s in status %s valid since %s", sampleCode,
        statusChangeRequest.status(), statusChangeRequest.validSince()));
    String validSince = statusChangeRequest.validSince();
    SampleStatusDto requestedStatus = statusChangeRequest.status();
    if (SampleStatusDto.METADATA_REGISTERED.equals(requestedStatus)) {
      sampleService.registerMetadata(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_RECEIVED.equals(requestedStatus)) {
      sampleService.receiveSample(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_QC_FAIL.equals(requestedStatus)) {
      sampleService.failQualityControl(sampleCode, validSince);

    } else if (SampleStatusDto.SAMPLE_QC_PASS.equals(requestedStatus)) {
      sampleService.passQualityControl(sampleCode, validSince);

    } else if (SampleStatusDto.LIBRARY_PREP_FINISHED.equals(requestedStatus)) {
      sampleService.prepareLibrary(sampleCode, validSince);

    } else if (SampleStatusDto.DATA_AVAILABLE.equals(requestedStatus)) {
      sampleService.provideData(sampleCode, validSince);

    } else {
      /* this is unnecessary in Java 17 using enhanced switch methods should never be reached if all enum values are handled here.*/
      throw new UnrecoverableException(
          "Provided sample status not recognized: "
              + requestedStatus, ErrorCode.BAD_SAMPLE_STATUS, ErrorParameters.create().with("sampleStatus", requestedStatus));
    }
    log.info(String.format("Sample %s is in status %s valid since %s", sampleCode, statusChangeRequest.status(), statusChangeRequest.validSince()));
    return HttpResponse.ok();
  }

  @Operation(summary = "Request information about the current status of a sample.",
      description = "Delivers the current status of a sample in the system.",
      tags = "Sample Status")
  @ApiResponse(responseCode = "200", description = "The request was fulfilled. The current status is provided in the response body.",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = SampleStatusResponse.class)))
  @Get(uri = "/{sampleCode}/status")
  @RolesAllowed("READER")
  @SingleResult
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
        throw new UnrecoverableException(
            String.format("Could not map internal sample status %s to api status.",
                sampleStatus.name()));
    }
    log.info(String.format("Found sample %s with status %s. Valid since %s", sampleCode, statusDto.name(), sampleState.statusValidSince()));
    SampleStatusResponse responseBody = new SampleStatusResponse(sampleCode,
        statusDto,
        sampleState.statusValidSince().toString());
    return HttpResponse.ok(responseBody);
  }


}
