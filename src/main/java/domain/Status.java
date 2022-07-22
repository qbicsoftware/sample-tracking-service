package domain;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A status a sample can have. It denotes a distinct state of the sample life-cycle.</p>
 */
public enum Status {
  SAMPLE_RECEIVED,
  SAMPLE_QC_PASSED,
  SAMPLE_QC_FAILED,
  LIBRARY_PREP_FINISHED,
  SEQUENCING_COMPLETED,
  METADATA_REGISTERED,
  DATA_AVAILABLE;

  /**
   * Holding a status as key with all its direct precursors.
   */
  private static final Map<Status, Status> STATUS_AND_PRECURSOR = initPrecursorMap();

  /**
   * @return a map containing a Status as key and direct precursors as value.
   */
  private static Map<Status, Status> initPrecursorMap() {
    Map<Status, Status> precursor = new HashMap<>();
    precursor.put(SAMPLE_QC_PASSED, SAMPLE_RECEIVED);
    precursor.put(SAMPLE_QC_FAILED, SAMPLE_RECEIVED);
    precursor.put(LIBRARY_PREP_FINISHED, SAMPLE_QC_PASSED);
    precursor.put(SEQUENCING_COMPLETED, LIBRARY_PREP_FINISHED);
    precursor.put(METADATA_REGISTERED, SEQUENCING_COMPLETED);
    precursor.put(DATA_AVAILABLE, METADATA_REGISTERED);

    return precursor;
  }

  /**
   * This method determines whether this sample status can follow on a provided sample status.
   * @param previous the sample status the sample was in previously.
   * @return whether this sample status is a successor of the provided status.
   */
  public boolean canFollow(Status previous) {
    return STATUS_AND_PRECURSOR.get(this).equals(previous);
  }
}
