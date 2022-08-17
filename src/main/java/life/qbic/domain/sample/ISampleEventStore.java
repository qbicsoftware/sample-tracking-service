package life.qbic.domain.sample;

import java.util.List;
import javax.inject.Singleton;

/**
 * <p>Stores and retrieves domain events.</p>
 */
@Singleton
public interface ISampleEventStore {
  void store(SampleEvent sampleEvent);
  List<SampleEvent> findForSample(SampleCode sampleCode);
}
