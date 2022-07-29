package life.qbic.controller


import life.qbic.datamodel.samples.Status

/**
 * Maps Statuses from version 1 to statuses from version 2 and vice versa
 *
 * @since 2.0.0
 */
class StatusMapper {

  static domain.sample.Status toStatusV2(Status status) {
    switch (status) {
      case Status.METADATA_REGISTERED:
        return domain.sample.Status.METADATA_REGISTERED
        break
      case Status.SAMPLE_RECEIVED:
        return domain.sample.Status.SAMPLE_RECEIVED
        break
      case Status.SAMPLE_QC_PASS:
        return domain.sample.Status.SAMPLE_QC_PASSED
        break
      case Status.SAMPLE_QC_FAIL:
        return domain.sample.Status.SAMPLE_QC_FAILED
        break
      case Status.LIBRARY_PREP_FINISHED:
        return domain.sample.Status.LIBRARY_PREP_FINISHED
        break
      case Status.DATA_AVAILABLE:
        return domain.sample.Status.DATA_AVAILABLE
        break
      default:
        return domain.sample.Status.METADATA_REGISTERED
    }
  }

  static Status toStatusV1(domain.sample.Status status) {
    switch (status) {
      case domain.sample.Status.METADATA_REGISTERED:
        return Status.METADATA_REGISTERED
        break
      case domain.sample.Status.SAMPLE_RECEIVED:
        return Status.SAMPLE_RECEIVED
        break
      case domain.sample.Status.SAMPLE_QC_PASSED:
        return Status.SAMPLE_QC_PASS
        break
      case domain.sample.Status.SAMPLE_QC_FAILED:
        return Status.SAMPLE_QC_FAIL
        break
      case domain.sample.Status.LIBRARY_PREP_FINISHED:
        return Status.LIBRARY_PREP_FINISHED
        break
      case domain.sample.Status.DATA_AVAILABLE:
        return Status.DATA_AVAILABLE
        break
      default:
        return Status.METADATA_REGISTERED
    }
  }


}
