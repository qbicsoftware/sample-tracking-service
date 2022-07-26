package domain.sample

import domain.sample.events.MetadataRegistered
import life.qbic.datamodel.identifiers.SampleCodeFunctions
import spock.lang.Specification

import java.time.Instant

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class SampleSpec extends Specification {
  def "when metadata was registered for that sample, then the sample's state has status METADATA_REGISTERED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "metadata was registered for that sample"
    MetadataRegistered metadataRegistered = MetadataRegistered.create(code, Instant.now())
    sample.addEvent(metadataRegistered)

    then: "the sample's state has status METADATA_REGISTERED"
    sample.currentState().status() == Status.METADATA_REGISTERED
    sample.events().contains(metadataRegistered)
  }
}
