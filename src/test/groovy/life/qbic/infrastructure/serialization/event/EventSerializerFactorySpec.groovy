package life.qbic.infrastructure.serialization.event

import life.qbic.domain.sample.SampleCode
import life.qbic.domain.sample.events.*
import spock.lang.Specification

import java.time.Instant

class EventSerializerFactorySpec extends Specification {

  def "serializer works for #className"() {
    when:
    def serializer = EventSerializerFactory.eventSerializer()

    then:
    serializer.serialize(event) == expectedJson

    where:
    event << [
            MetadataRegistered.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z")),
            SampleReceived.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z")),
            FailedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z")),
            PassedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z")),
            LibraryPrepared.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z")),
            DataMadeAvailable.create(SampleCode.fromString("QABCD001A0"), Instant.parse("2022-08-03T16:27:00Z"))
    ]
    className = event.class.name
    expectedJson = '{"className":"' + className + '","version":"' + event.version() + '","sampleCode":"' + SampleCode.fromString("QABCD001A0").toString() + '","occurredOn":"' + "2022-08-03T16:27:00Z" + '"}'
  }
}
