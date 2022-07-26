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
import java.time.Instant;
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
  public HttpResponse<?> moveSampleToStatus(@PathVariable String sampleCode, @Body String status) {
    sampleService.moveSample(sampleCode, status, Instant.now().toString());
    return HttpResponse.ok();
  }

  @Get(uri = "/{sampleCode}/status")
  public HttpResponse<String> getSampleStatus(@PathVariable String sampleCode) {
    Status sampleStatus = sampleService.getSampleStatus(sampleCode);
    String body = "";
    switch (sampleStatus) {
      case METADATA_REGISTERED:
        body = StatusMapping.METADATA_REGISTERED.stringRepresentation();
        break;
      case SAMPLE_RECEIVED:
        body = StatusMapping.SAMPLE_RECEIVED.stringRepresentation();
        break;
      case SAMPLE_QC_PASSED:
        body = StatusMapping.SAMPLE_QC_PASSED.stringRepresentation();
        break;
      case SAMPLE_QC_FAILED:
        body = StatusMapping.SAMPLE_QC_FAILED.stringRepresentation();
        break;
      case LIBRARY_PREP_FINISHED:
        body = StatusMapping.LIBRARY_PREP_FINISHED.stringRepresentation();
        break;
      case DATA_AVAILABLE:
        body = StatusMapping.DATA_AVAILABLE.stringRepresentation();
        break;
    }
    return HttpResponse.ok(body);
  }


}
