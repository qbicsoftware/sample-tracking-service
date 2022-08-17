package life.qbic.domain.sample.events;

import java.time.Instant;
import life.qbic.domain.sample.SampleCode;
import life.qbic.domain.sample.SampleEvent;

/**
 * A library was prepared for a sample.
 *
 * @since 2.0.0
 */
public class LibraryPrepared extends SampleEvent {

  public static LibraryPrepared create(SampleCode sampleCode, Instant occurredOn) {
    return new LibraryPrepared(sampleCode, occurredOn);
  }

  private LibraryPrepared(SampleCode sampleCode, Instant occurredOn) {
    super(sampleCode, occurredOn);
  }
  @Override
  public Version version() {
    return Version.create(1, 0);
  }

  @Override
  public String toString() {
    return "LibraryPrepared{} " + super.toString();
  }
}
