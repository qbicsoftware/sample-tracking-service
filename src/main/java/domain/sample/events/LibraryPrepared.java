package domain.sample.events;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.time.Instant;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
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
