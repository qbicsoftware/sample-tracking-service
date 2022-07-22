package domain;

import java.util.Set;

/**
 * <p>Stores and retrieves domain events.</p>
 */
public interface EventStore {
  void store(SampleEvent sampleEvent);
  Set<SampleEvent> findAllForSample(SampleCode sampleCode);
}
