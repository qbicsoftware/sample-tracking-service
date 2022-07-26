package domain.sample;

/**
 * <p>A status a sample can have. It denotes a distinct state of the sample life-cycle.</p>
 */
enum Status {
  METADATA_REGISTERED,
  SAMPLE_RECEIVED,
  SAMPLE_QC_PASSED,
  SAMPLE_QC_FAILED,
  LIBRARY_PREP_FINISHED,
  SEQUENCING_COMPLETED,
  DATA_AVAILABLE
}
