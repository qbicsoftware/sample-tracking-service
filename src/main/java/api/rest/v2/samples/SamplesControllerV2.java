package api.rest.v2.samples;


import application.ApplicationException;
import application.SampleService;
import domain.InvalidDomainException;
import domain.notification.INotificationRepository;
import domain.sample.SampleEventDatasource;
import domain.sample.SampleEventStore;
import domain.sample.SampleRepository;
import domain.sample.Status;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import life.qbic.auth.Authentication;

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/samples")
public class SamplesControllerV2 {

  SampleService sampleService;

  @Inject
  public SamplesControllerV2(SampleEventDatasource sampleEventDatasource, INotificationRepository notificationRepository) {
    this.sampleService = new SampleService(
        new SampleRepository(new SampleEventStore(sampleEventDatasource)), notificationRepository);
  }

  @Put(uri = "/{sampleCode}/status")
  @RolesAllowed("WRITER")
  public HttpResponse<?> moveSampleToStatus(@PathVariable String sampleCode,
      @Body StatusChangeRequest statusChangeRequest) {
    String validSince = statusChangeRequest.validSince;
    String requestedStatus = statusChangeRequest.status;
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
    return HttpResponse.ok();
  }

  @Get(uri = "/{sampleCode}/status")
  @RolesAllowed("READER")
  public HttpResponse<String> getSampleStatus(@PathVariable String sampleCode) {
    Status sampleStatus = sampleService.getSampleStatus(sampleCode);
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
    return HttpResponse.ok(statusDto.name());
  }

  @Error(InvalidDomainException.class)
  HttpResponse<String> onDomainError() {
    return HttpResponse.serverError("Apologies! Your request could not be processed.");
  }

  @Error(ApplicationException.class)
  HttpResponse<String> onApplicationError() {
    return HttpResponse.serverError("Apologies! Your request could not be processed.");  }

  @Error(IllegalArgumentException.class)
  HttpResponse<String> onIllegalArguments() {
    return HttpResponse.status(HttpStatus.BAD_REQUEST);
  }

  @Error(Exception.class)
  HttpResponse<String> onOtherError() {
    return HttpResponse.serverError("Apologies! Your request could not be processed.");
  }


}
