package domain.sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleRepository {

  private final ISampleEventStore sampleEventStore;

  public SampleRepository(ISampleEventStore sampleEventStore) {
    this.sampleEventStore = sampleEventStore;
  }

  public Sample get(SampleCode sampleCode) {
    return Sample.fromEvents(sampleEventStore.findForSample(sampleCode));
  }

  public void store(Sample sample) {
    sample.events().forEach(sampleEventStore::store);
  }

}
