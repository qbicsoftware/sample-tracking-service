package domain.sample.events;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;

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
  public String toString() {
    return "LibraryPrepared{} " + super.toString();
  }
}
