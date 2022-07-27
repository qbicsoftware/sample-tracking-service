package restApi.v2.samples;


import application.SampleService;
import domain.sample.SampleEventDatasource;
import domain.sample.SampleEventStore;
import domain.sample.SampleRepository;
import domain.sample.Status;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import javax.inject.Inject;
import life.qbic.auth.Authentication;

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/samples")
public class SamplesControllerV2 {

  SampleService sampleService;

  @Inject
  public SamplesControllerV2(SampleEventDatasource sampleEventDatasource) {
    this.sampleService = new SampleService(
        new SampleRepository(new SampleEventStore(sampleEventDatasource)));
  }

  @Put(uri = "/{sampleCode}/status")
  public HttpResponse<?> moveSampleToStatus(@PathVariable String sampleCode, @Body StatusChangeRequest statusChangeRequest) {
    sampleService.moveSample(sampleCode, statusChangeRequest.status, statusChangeRequest.validFrom);
    return HttpResponse.ok();
  }

  @Get(uri = "/{sampleCode}/status")
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
        throw new IllegalArgumentException(
            "Provided sample status not recognized: "
                + sampleStatus.name()); // this is unnecessary in Java 17 using enhanced switch methods
                                        // should never be reached if all enum values are handled here.
    }
    return HttpResponse.ok(statusDto.name());
  }


}
