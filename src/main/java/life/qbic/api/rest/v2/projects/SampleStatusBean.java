package life.qbic.api.rest.v2.projects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import life.qbic.api.rest.v2.samples.SampleStatusDto;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class SampleStatusBean {

  @JsonProperty("sampleCode")
  String sampleCode;
  @JsonProperty("status")
  SampleStatusDto status;
  Instant statusValidSince;

  @JsonProperty("statusValidSince")
  public String getStatusValidSince() {
    return statusValidSince.toString();
  }

  private SampleStatusBean() {
    super();
  }

  public SampleStatusBean(String sampleCode, SampleStatusDto status, Instant statusValidSince) {
    this.sampleCode = sampleCode;
    this.status = status;
    this.statusValidSince = statusValidSince;
  }

  public static SampleStatusBean create(String sampleCode, SampleStatusDto status,
      Instant statusValidSince) {
    return new SampleStatusBean(sampleCode, status, statusValidSince);
  }

  public String getSampleCode() {
    return sampleCode;
  }

  public void setSampleCode(String sampleCode) {
    this.sampleCode = sampleCode;
  }

  public SampleStatusDto getStatus() {
    return status;
  }

  public void setStatus(SampleStatusDto status) {
    this.status = status;
  }

  public void setStatusValidSince(Instant statusValidSince) {
    this.statusValidSince = statusValidSince;
  }
}
