package domain.sample;

import java.time.Instant;

/**
 * A domain event in the sample domain.
 */
public interface SampleEvent {
  SampleCode sampleCode();
  Instant occurredOn();
}
