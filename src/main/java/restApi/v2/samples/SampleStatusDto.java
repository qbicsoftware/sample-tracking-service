package restApi.v2.samples;

/**
 * Statuses exposed to the outside. The enum names can be used in REST calls to represent a sample
 * status.
 *
 * @since 2.0.0
 */
public enum SampleStatusDto {
  METADATA_REGISTERED,
  SAMPLE_RECEIVED,
  SAMPLE_QC_PASS,
  SAMPLE_QC_FAIL,
  LIBRARY_PREP_FINISHED,
  DATA_AVAILABLE
}
