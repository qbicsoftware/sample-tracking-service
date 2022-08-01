package life.qbic.domain.sample;

import java.util.List;
import java.util.Optional;

/**
 * A repository for samples. Used to get samples and store samples in persistence.
 *
 * @since 2.0.0
 */
public class SampleRepository {

  private final ISampleEventStore sampleEventStore;

  public SampleRepository(ISampleEventStore sampleEventStore) {
    this.sampleEventStore = sampleEventStore;
  }

  public Optional<Sample> get(SampleCode sampleCode) {

    List<SampleEvent> sampleEvents = sampleEventStore.findForSample(sampleCode);
    if (sampleEvents.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Sample.fromEvents(sampleEvents));
  }

  public void store(Sample sample) {
    sample.events().forEach(sampleEventStore::store);
  }

}
