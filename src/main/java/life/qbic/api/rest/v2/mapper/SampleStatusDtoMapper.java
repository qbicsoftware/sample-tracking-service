package life.qbic.api.rest.v2.mapper;

import life.qbic.api.rest.v2.samples.SampleStatusDto;
import life.qbic.domain.sample.Status;
import life.qbic.exception.UnrecoverableException;

/**
 * Maps internal representation to stable API data transfer object.
 *
 * @since 2.0.0
 */
public class SampleStatusDtoMapper {

  public static SampleStatusDto sampleStatusToDto(Status sampleStatus) {
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
    return statusDto;
  }
}
