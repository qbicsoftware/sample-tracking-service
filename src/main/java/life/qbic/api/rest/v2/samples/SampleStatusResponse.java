package life.qbic.api.rest.v2.samples;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response providing information about a sample, its status and since when the sample has this status.
 * @since 2.0.0
 */
public class SampleStatusResponse {

  protected SampleStatusResponse() {
  }

  public SampleStatusResponse(String sampleCode, String status, String validSince) {
    this.sampleCode = sampleCode;
    this.status = status;
    this.validSince = validSince;
  }

  @JsonProperty("sampleCode")
  private String sampleCode;
  @JsonProperty("status")
  private String status;
  @JsonProperty("validSince")
  private String validSince;

  public String status() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String validSince() {
    return validSince;
  }

  public void setValidSince(String validSince) {
    this.validSince = validSince;
  }

  public String sampleCode() {
    return sampleCode;
  }

  public void setSampleCode(String sampleCode) {
    this.sampleCode = sampleCode;
  }
}
