package life.qbic.api.rest.v2.samples;

/**
 * <b>Requests the sample status to be changed.</b>
 *
 * <p>A request body containing information about a sample change. Used for external calls to the
 * REST endpoints.</p>
 *
 * @since 2.0.0
 */
public class StatusChangeRequest {

  protected StatusChangeRequest() {
  }

  public StatusChangeRequest(SampleStatusDto status, String validSince) {
    this.status = status;
    this.validSince = validSince;
  }

  /**
   * The sample status value.
   */
  private SampleStatusDto status;

  /**
   * The instant from which on this status is valid (e.g. 2022-07-27T00:00:01.352Z);
   */
  private String validSince;

  public SampleStatusDto status() {
    return status;
  }

  public SampleStatusDto getStatus() {
    return status();
  }

  public void setStatus(SampleStatusDto status) {
    this.status = status;
  }

  public String validSince() {
    return validSince;
  }

  public String getValidSince() {
    return validSince();
  }

  public void setValidSince(String validSince) {
    this.validSince = validSince;
  }
}
