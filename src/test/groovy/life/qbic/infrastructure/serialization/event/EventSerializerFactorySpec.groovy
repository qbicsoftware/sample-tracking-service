package life.qbic.infrastructure.serialization.event

import life.qbic.domain.sample.SampleCode
import life.qbic.domain.sample.events.DataMadeAvailable
import life.qbic.domain.sample.events.FailedQualityControl
import life.qbic.domain.sample.events.MetadataRegistered
import spock.lang.Specification

import java.time.Instant

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class EventSerializerFactorySpec extends Specification {

  def "test"() {
    given:
    def serializer = EventSerializerFactory.eventSerializer(DataMadeAvailable.class)
    expect:
    println serializer.serialize(DataMadeAvailable.create(SampleCode.fromString("QABCD001A0"), Instant.now()))
    println serializer.serialize(FailedQualityControl.create(SampleCode.fromString("QABCD001A0"), Instant.now()))
    println serializer.serialize(MetadataRegistered.create(SampleCode.fromString("QABCD001A0"), Instant.now()))


  }
}
