package life.qbic.domain.sample;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An event store for sample events. The event store is responsible for de-duplication.
 *
 * @since 2.0.0
 */
public class SampleEventStore implements ISampleEventStore {

  private final SampleEventDatasource sampleEventDatasource;

  public SampleEventStore(SampleEventDatasource sampleEventDatasource) {
    this.sampleEventDatasource = sampleEventDatasource;
  }

  @Override
  public void store(SampleEvent sampleEvent) {
    if (findForSample(sampleEvent.sampleCode()).contains(sampleEvent)) {
      return;
    }
    sampleEventDatasource.store(sampleEvent);
  }

  @Override
  public List<SampleEvent> findForSample(SampleCode sampleCode) {
    List<SampleEvent> events = sampleEventDatasource.findAllForSample(sampleCode);
    return events.stream()
        .distinct()
        .sorted(Comparator.comparing(SampleEvent::occurredOn))
        .collect(Collectors.toList());
  }
}
