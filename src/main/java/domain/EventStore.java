package domain;

import domain.sample.SampleCode;
import domain.sample.SampleEvent;
import java.util.SortedSet;

/**
 * <p>Stores and retrieves domain events.</p>
 */
public interface EventStore {
  void store(SampleEvent sampleEvent);
  SortedSet<SampleEvent> findForSample(SampleCode sampleCode);
}
