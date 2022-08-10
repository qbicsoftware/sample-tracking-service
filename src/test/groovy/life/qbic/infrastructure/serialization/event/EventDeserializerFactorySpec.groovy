package life.qbic.infrastructure.serialization.event


import spock.lang.Specification

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class EventDeserializerFactorySpec extends Specification {

  def "test"() {
    given:
    def deserializer = EventDeserializerFactory.sampleEventDeserializer()
    def json = '{"className":"life.qbic.domain.sample.events.FailedQualityControl","version":"1.0","sampleCode":"QABCD001A0","occurredOn":"2022-08-10T06:34:40.147Z"}'

    expect:
    println deserializer.deserialize(json)

  }
}
