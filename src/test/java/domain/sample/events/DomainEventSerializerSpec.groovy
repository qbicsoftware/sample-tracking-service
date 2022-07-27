package domain.sample.events

import domain.sample.DomainEventSerializer
import domain.sample.SampleCode
import spock.lang.Specification

import java.time.Instant

class DomainEventSerializerSpec extends Specification {

  def "expect serialization does not throw an exception"() {
    given:
    def domainEventSerializer = new DomainEventSerializer()
    when: "serialization and successive deserialization returns the original object"
    domainEventSerializer.serialize(event)
    then:
    noExceptionThrown()
    where: "the event is as follows"
    event << [
            MetadataRegistered.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            SampleReceived.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            PassedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            FailedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            LibraryPrepared.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            DataMadeAvailable.create(SampleCode.fromString("QABCD001A0"), Instant.MIN)
    ]
  }

  def "expect serialization and successive deserialization returns the original object"() {
    given:
    def domainEventSerializer = new DomainEventSerializer()
    when: "serialization and successive deserialization returns the original object"
    def deserialized = domainEventSerializer.deserialize(domainEventSerializer.serialize(event))
    then:
    deserialized == event
    where: "the event is as follows"
    event << [
            MetadataRegistered.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            SampleReceived.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            PassedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            FailedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            LibraryPrepared.create(SampleCode.fromString("QABCD001A0"), Instant.MIN),
            DataMadeAvailable.create(SampleCode.fromString("QABCD001A0"), Instant.MIN)
    ]
  }

}
