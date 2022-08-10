package life.qbic.infrastructure.serialization.event


import life.qbic.domain.sample.events.*
import spock.lang.Specification

class EventDeserializerFactorySpec extends Specification {

  def "deserializer obtained by factory works for #className"() {
    given:
    def deserializer = EventDeserializerFactory.sampleEventDeserializer()
    def json = '{"className":"' + className + '","version":"1.0","sampleCode":"QABCD001A0","occurredOn":"2022-08-10T06:34:40.147Z"}'

    when:
    def event = deserializer.deserialize(json)
    then:
    Objects.nonNull(event)
    eventClass.isInstance(event)

    where:
    eventClass << [
            MetadataRegistered.class,
            SampleReceived.class,
            FailedQualityControl.class,
            PassedQualityControl.class,
            LibraryPrepared.class,
            DataMadeAvailable.class
    ]
    className = eventClass.name

  }
}
