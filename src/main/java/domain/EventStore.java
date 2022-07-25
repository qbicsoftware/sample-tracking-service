package domain;

import java.util.SortedSet;

/**
 * <p>Stores and retrieves domain events.</p>
 */
public interface EventStore {
  void store(SampleEvent sampleEvent);
  SortedSet<SampleEvent> findAllForSample(SampleCode sampleCode);
}
