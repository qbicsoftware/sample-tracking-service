package life.qbic.api.rest.v2.samples;

import java.time.Instant;

/**
 * Response providing information about a sample, its status and since when the sample has this status.
 * @since 2.0.0
 */
public class SampleStatusResponse {

  protected SampleStatusResponse() {
  }

  public SampleStatusResponse(String sampleCode, String status, Instant validSince) {
    this.sampleCode = sampleCode;
    this.status = status;
    this.validSince = validSince;
  }

  private String sampleCode;
  private String status;
  private Instant validSince;

  public String status() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant validSince() {
    return validSince;
  }

  public void setValidSince(Instant validSince) {
    this.validSince = validSince;
  }

  public String sampleCode() {
    return sampleCode;
  }

  public void setSampleCode(String sampleCode) {
    this.sampleCode = sampleCode;
  }
}
