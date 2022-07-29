package life.qbic.domain.notification;

import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.Status;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A value object representing a notification about samples.
 *
 * @since 2.0.0
 */
public class SampleStatusNotification {

  private final SampleCode sampleCode;
  private final Instant recodedAt;
  private final Status sampleStatus;

  private SampleStatusNotification(SampleCode sampleCode, Instant recodedAt, Status sampleStatus) {
    this.sampleCode = Objects.requireNonNull(sampleCode);
    this.recodedAt = Objects.requireNonNull(recodedAt);
    this.sampleStatus = Objects.requireNonNull(sampleStatus);
  }

  public static SampleStatusNotification create(SampleCode sampleCode, Instant recodedAt, Status sampleStatus) {
    return new SampleStatusNotification(sampleCode, recodedAt, sampleStatus);
  }

  public SampleCode sampleCode() {
    return sampleCode;
  }

  public Instant recodedAt() {
    return recodedAt;
  }

  public Status sampleStatus() {
    return sampleStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SampleStatusNotification that = (SampleStatusNotification) o;

    if (!sampleCode.equals(that.sampleCode)) {
      return false;
    }
    if (!recodedAt.equals(that.recodedAt)) {
      return false;
    }
    return sampleStatus == that.sampleStatus;
  }

  @Override
  public int hashCode() {
    int result = sampleCode.hashCode();
    result = 31 * result + recodedAt.hashCode();
    result = 31 * result + sampleStatus.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SampleStatusNotification.class.getSimpleName() + "[", "]")
        .add("sampleCode=" + sampleCode)
        .add("recodedAt=" + recodedAt)
        .add("sampleStatus=" + sampleStatus)
        .toString();
  }
}
