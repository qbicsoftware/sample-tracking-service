package life.qbic.domain.sample;

import java.util.List;

/**
 * <p>Stores and retrieves domain events.</p>
 */
public interface ISampleEventStore {
  void store(SampleEvent sampleEvent);
  List<SampleEvent> findForSample(SampleCode sampleCode);
}
