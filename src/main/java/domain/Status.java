package domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>A status a sample can have. It denotes a distinct state of the sample life-cycle.</p>
 */
public enum Status {
  SAMPLE_RECEIVED("Sample received"),
  SAMPLE_QC_PASSED("QC passed"),
  SAMPLE_QC_FAILED("QC failed"),
  LIBRARY_PREP_FINISHED("Library prepared"),
  SEQUENCING_COMPLETED("Sequencing completed"),
  METADATA_REGISTERED("Metadata registered"),
  DATA_AVAILABLE("Data available");

  private final String label;

  Status(String label) {
    this.label = label;
  }

  public static Optional<Status> fromLabel(String label) {
    return Arrays.stream(values()).filter(it -> Objects.equals(it.label, label)).findAny();
  }
}
