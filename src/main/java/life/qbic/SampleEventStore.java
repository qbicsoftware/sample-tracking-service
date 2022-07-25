package life.qbic;

import life.qbic.domain.SampleCode;
import life.qbic.events.SampleEvent;

import java.util.SortedSet;
/**
 * <p>Stores and retrieves domain events.</p>
 */
public interface SampleEventStore {
  void store(SampleEvent sampleEvent);
  SortedSet<SampleEvent> findAllForSample(SampleCode sampleCode);
}