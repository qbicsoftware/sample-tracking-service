package life.qbic.domain.sample

import life.qbic.datamodel.identifiers.SampleCodeFunctions
import life.qbic.domain.sample.events.*
import life.qbic.exception.UnrecoverableException
import spock.lang.Specification

import java.time.Instant

class SampleSpec extends Specification {

  def "expect a sample that was created from events is in the correct state"() {
    given:
    SampleCode sampleCode = SampleCode.fromString("QABCD001A0")
    MetadataRegistered metadataRegistered = MetadataRegistered.create(sampleCode, metadataRegisteredOccurredOn)
    SampleReceived sampleReceived = SampleReceived.create(sampleCode, sampleReceivedOccurredOn)
    DataMadeAvailable dataMadeAvailable = DataMadeAvailable.create(sampleCode, dataAvailableOccurredOn)
    when: "a sample that was created from events"
    def sample = Sample.fromEvents([metadataRegistered, sampleReceived, dataMadeAvailable])
    then: "the sample is in the correct state"
    sample.currentState().status() == expectedStatus
    and: "the sample code is the code specified by the events"
    sample.sampleCode() == sampleCode
    where:
    expectedStatus             | metadataRegisteredOccurredOn              | sampleReceivedOccurredOn                  | dataAvailableOccurredOn
    Status.METADATA_REGISTERED | Instant.MAX | Instant.parse("2022-07-27T00:00:01.000Z") | Instant.MIN
    Status.SAMPLE_RECEIVED     | Instant.parse("2022-07-27T00:00:01.000Z") | Instant.MAX                               | Instant.MIN
    Status.DATA_AVAILABLE      | Instant.MIN                               | Instant.parse("2022-07-27T00:00:01.000Z") | Instant.MAX
  }

  def "given A sample and an event processed by it: when the event is added to the sample, then the sample will ignore the request"() {
    given: "A sample and an event processed by it"
    def sampleCode = SampleCode.fromString("QABCD001A0")
    Sample sample = Sample.create(sampleCode)
    MetadataRegistered metadataRegistered = MetadataRegistered.create(sampleCode, Instant.now())
    sample.addEvent(metadataRegistered)
    when: "the event is added to the sample"
    def stateBefore = sample.currentState()
    sample.addEvent(metadataRegistered)
    def stateAfter = sample.currentState()

    then: "the sample will ignore the request"
    stateAfter == stateBefore
  }

  def "given a sample and an event predating the sample state, when the event is added to the sample, then an Exception is thrown"() {
    given: "a Sample with at least one event"
    SampleCode sampleCode = SampleCode.fromString("QABCD001A0")
    MetadataRegistered metadataRegistered = MetadataRegistered.create(sampleCode, Instant.MAX)
    Sample sample = Sample.fromEvents([metadataRegistered])
    and: "an event predating the current state of the sample"
    SampleReceived sampleReceived = SampleReceived.create(sampleCode, Instant.MIN)
    when: "the event is added to the sample"
    sample.addEvent(sampleReceived)
    then: "the event was not added to the sample"
    !sample.events().contains(sampleReceived)
    and: "an exception is thrown"
    thrown(UnrecoverableException)
  }

  def "expect sample creation from events not possible if no events are provided"() {
    when: "no events are provided"
    Sample.fromEvents([])
    then: "sample creation is not possible"
    thrown(IllegalArgumentException)
  }

  def "given a new sample, when metadata was registered for that sample, then the sample's state has status METADATA_REGISTERED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "metadata was registered for that sample"
    MetadataRegistered metadataRegistered = MetadataRegistered.create(code, Instant.now())
    sample.addEvent(metadataRegistered)

    then: "the sample's state has status METADATA_REGISTERED"
    sample.currentState().status() == Status.METADATA_REGISTERED
    and: "the sample is aware of the processed event"
    sample.events().contains(metadataRegistered)
  }

  def "given a new sample, when the sample was received, then the sample's state has status SAMPLE_RECEIVED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "the sample was received"
    SampleReceived sampleReceived = SampleReceived.create(code, Instant.now())
    sample.addEvent(sampleReceived)

    then: "the sample's state has status SAMPLE_RECEIVED"
    sample.currentState().status() == Status.SAMPLE_RECEIVED
    and: "the sample is aware of the processed event"
    sample.events().contains(sampleReceived)
  }

  def "given a new sample, when the quality control passed, then the sample's state has status SAMPLE_QC_PASSED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "when the quality control passed"
    PassedQualityControl passedQualityControl = PassedQualityControl.create(code, Instant.now())
    sample.addEvent(passedQualityControl)

    then: "the sample's state has status SAMPLE_QC_PASSED"
    sample.currentState().status() == Status.SAMPLE_QC_PASSED
    and: "the sample is aware of the processed event"
    sample.events().contains(passedQualityControl)
  }

  def "given a new sample, when the quality control failed, then the sample's state has status SAMPLE_QC_FAILED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "when the quality control failed"
    FailedQualityControl failedQualityControl = FailedQualityControl.create(code, Instant.now())
    sample.addEvent(failedQualityControl)

    then: "the sample's state has status SAMPLE_QC_FAILED"
    sample.currentState().status() == Status.SAMPLE_QC_FAILED
    and: "the sample is aware of the processed event"
    sample.events().contains(failedQualityControl)
  }

  def "given a new sample, when data has been made available for that sample, then the sample's state has status DATA_AVAILABLE"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "when data has been made available"
    DataMadeAvailable dataMadeAvailable = DataMadeAvailable.create(code, Instant.now())
    sample.addEvent(dataMadeAvailable)

    then: "the sample's state has status DATA_AVAILABLE"
    sample.currentState().status() == Status.DATA_AVAILABLE
    and: "the sample is aware of the processed event"
    sample.events().contains(dataMadeAvailable)
  }

  def "given a new sample, when the library was prepared for that sample, then the sample's state has status LIBRARY_PREP_FINISHED"() {
    given: "a new sample"
    def code = SampleCode.fromString("QABCD001A" + SampleCodeFunctions.checksum("QABCD001A"))
    Sample sample = Sample.create(code)
    when: "when the library was prepared for that sample"
    LibraryPrepared libraryPrepared = LibraryPrepared.create(code, Instant.now())
    sample.addEvent(libraryPrepared)

    then: "the sample's state has status LIBRARY_PREP_FINISHED"
    sample.currentState().status() == Status.LIBRARY_PREP_FINISHED
    and: "the sample is aware of the processed event"
    sample.events().contains(libraryPrepared)
  }
}
