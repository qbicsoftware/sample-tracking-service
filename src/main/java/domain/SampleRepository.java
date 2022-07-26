package domain;

import domain.sample.Sample;
import domain.sample.SampleCode;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleRepository {

  private final EventStore eventStore;

  public SampleRepository(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  public Sample get(SampleCode sampleCode) {
    return Sample.fromEvents(eventStore.findForSample(sampleCode));
  }

  public void store(Sample sample) {
    sample.events().forEach(eventStore::store);
  }

}
