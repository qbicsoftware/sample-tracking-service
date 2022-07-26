package restApi.v2.samples;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public enum StatusMapping {
  METADATA_REGISTERED("METADATA_REGISTERED"),
  SAMPLE_RECEIVED("SAMPLE_RECEIVED"),
  SAMPLE_QC_PASSED("SAMPLE_QC_PASS"),
  SAMPLE_QC_FAILED("SAMPLE_QC_FAIL"),
  LIBRARY_PREP_FINISHED("LIBRARY_PREP_FINISHED"),
  DATA_AVAILABLE("DATA_AVAILABLE");

  private final String stringRepresentation;

  StatusMapping(String stringRepresentation) {
    this.stringRepresentation = stringRepresentation;
  }

  public String stringRepresentation() {
    return stringRepresentation;
  }
}
