package domain.sample


import domain.sample.events.MetadataRegistered
import spock.lang.Specification

import java.time.Instant

class SampleEventStoreSpec extends Specification {


  def "expect storing the same events multiple times does not lead to duplicated read"() {
    given: "an event store and an event"
    def sampleCode = SampleCode.fromString("QABCD001A0")
    MetadataRegistered metadataRegistered = MetadataRegistered.create(sampleCode, Instant.now())
    def sampleEventDatasource = new MockSampleEventDatasource()
    SampleEventStore sampleEventStore = new SampleEventStore(sampleEventDatasource)
    when: "storing the same events multiple times"
    sampleEventStore.store(metadataRegistered)
    sampleEventStore.store(metadataRegistered)
    then: "no duplicated events are read"
    1 == sampleEventStore.findForSample(sampleCode).count { (it == metadataRegistered) }
  }

  private static class MockSampleEventDatasource implements SampleEventDatasource {
    private List<? extends SampleEvent> events = new ArrayList<>()
    @Override
    <T extends SampleEvent> void store(T sampleEvent) {
      events.add(sampleEvent)
    }

    @Override
    List<SampleEvent> findAllForSample(SampleCode sampleCode) {
      return events
    }
  }
}
