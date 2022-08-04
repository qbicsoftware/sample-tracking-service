package life.qbic

import life.qbic.domain.sample.Status

/**
 * Maps status enums
 *
 * @since 2.0.0
 */
class StatusMapper {

  static life.qbic.datamodel.samples.Status toStatusV1(Status status) {

    switch (status) {
      case Status.METADATA_REGISTERED:
        return life.qbic.datamodel.samples.Status.METADATA_REGISTERED
      case Status.SAMPLE_RECEIVED:
        return life.qbic.datamodel.samples.Status.SAMPLE_RECEIVED
      case Status.SAMPLE_QC_FAILED:
        return life.qbic.datamodel.samples.Status.SAMPLE_QC_FAIL
      case Status.SAMPLE_QC_PASSED:
        return life.qbic.datamodel.samples.Status.SAMPLE_QC_PASS
      case Status.LIBRARY_PREP_FINISHED:
        return life.qbic.datamodel.samples.Status.LIBRARY_PREP_FINISHED
      case Status.DATA_AVAILABLE:
        return life.qbic.datamodel.samples.Status.DATA_AVAILABLE
      default:
        throw new RuntimeException("status $status not handled. Missing implementation!")
    }
  }

}
