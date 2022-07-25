package life.qbic.events;

import java.util.List;
import java.util.SortedSet;

import life.qbic.domain.SampleCode;
import life.qbic.domain.events.DomainEvent;

/**
 * A repository for domain events.
 *
 * @since 1.0.0
 */
public interface SampleEventRepository {
  void store(SampleEvent sampleEvent);
  SortedSet<SampleEvent> findAllForSample(SampleCode sampleCode);
}
