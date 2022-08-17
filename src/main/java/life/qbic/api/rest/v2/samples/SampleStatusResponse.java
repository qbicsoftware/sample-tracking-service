package life.qbic.api.rest.v2.samples;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Response providing information about a sample, its status and since when the sample has this status.
 * @since 2.0.0
 */
public class SampleStatusResponse {

  protected SampleStatusResponse() {
  }

  public static SampleStatusResponse create(String sampleCode, SampleStatusDto status,
      Instant validSince) {
    return new SampleStatusResponse(sampleCode, status, validSince.toString());
  }

  public SampleStatusResponse(String sampleCode, SampleStatusDto status, String validSince) {
    this.sampleCode = sampleCode;
    this.status = status;
    this.statusValidSince = validSince;
  }

  @JsonProperty("sampleCode")
  private String sampleCode;
  @JsonProperty("status")
  private SampleStatusDto status;
  @JsonProperty("statusValidSince")
  private String statusValidSince;

  public SampleStatusDto status() {
    return status;
  }

  public SampleStatusDto getStatus() {
    return status();
  }

  public void setStatus(SampleStatusDto status) {
    this.status = status;
  }

  public String statusValidSince() {
    return statusValidSince;
  }

  public void setStatusValidSince(String statusValidSince) {
    this.statusValidSince = statusValidSince;
  }

  public String sampleCode() {
    return sampleCode;
  }

  public String getStatusValidSince() {
    return statusValidSince();
  }

  public String getSampleCode() {
    return sampleCode();
  }

  public void setSampleCode(String sampleCode) {
    this.sampleCode = sampleCode;
  }
}
