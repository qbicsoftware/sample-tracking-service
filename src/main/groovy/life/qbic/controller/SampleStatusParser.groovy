package life.qbic.controller

import life.qbic.api.rest.v2.samples.SampleStatusDto
import life.qbic.exception.ErrorCode
import life.qbic.exception.ErrorParameters
import life.qbic.exception.UnrecoverableException

/**
 * Parses status names and finds matching Dto statuses
 *
 * @since 2.0.0
 */
class SampleStatusParser {

  static SampleStatusDto parseStatus(String requestedStatus) {
    def foundStatusDto = Optional.ofNullable(SampleStatusDto.values()
            .find { it.name().equals(requestedStatus) })
    return foundStatusDto.orElseThrow( () -> new UnrecoverableException(
            "Provided sample status not recognized: "
                    + requestedStatus, ErrorCode.BAD_SAMPLE_STATUS, ErrorParameters.create().with("sampleStatus", requestedStatus)))
  }

}
