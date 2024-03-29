package life.qbic.api.rest.v2.samples

import life.qbic.domain.project.ProjectCode
import life.qbic.domain.sample.SampleCode
import life.qbic.domain.sample.SampleEvent
import life.qbic.domain.sample.SampleEventDatasource
import life.qbic.domain.sample.events.MetadataRegistered

import java.time.Instant

class SampleEventSourceMock implements SampleEventDatasource {
    @Override
    <T extends SampleEvent> void store(T sampleEvent) {

    }

    @Override
    List<SampleEvent> findAllForSample(SampleCode sampleCode) {
        List<SampleEvent> events = new ArrayList<>()
        if(sampleCode.toString() == "QABCD001A0") {
            events.add(MetadataRegistered.create(sampleCode, Instant.parse("2018-11-30T18:35:24.00Z")))
        }
        return events
    }

    @Override
    List<SampleEvent> findAllMatchingProject(ProjectCode projectCode) {
        List<SampleEvent> events = new ArrayList<>()
        if(projectCode.toString() == "QABCD") {
            events.add(MetadataRegistered.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2018-11-30T18:35:24.00Z")))
        }
        return events
    }
}
