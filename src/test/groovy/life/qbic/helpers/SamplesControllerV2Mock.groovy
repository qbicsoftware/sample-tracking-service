package life.qbic.helpers

import io.micronaut.http.HttpResponse
import life.qbic.api.rest.v2.samples.SamplesControllerV2
import life.qbic.api.rest.v2.samples.StatusChangeRequest

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class SamplesControllerV2Mock extends SamplesControllerV2 {
  SamplesControllerV2Mock() {
    super(null, null)
  }

  @Override
  HttpResponse<?> moveSampleToStatus(String sampleCode, StatusChangeRequest statusChangeRequest) {
    return HttpResponse.ok()
  }

  @Override
  HttpResponse<String> getSampleStatus(String sampleCode) {
    return HttpResponse.ok()
  }
}
