package life.qbic.domain.sample

import life.qbic.domain.sample.events.*
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

  def "expect existing serialization is preserved"() {

    def serializer = new DomainEventSerializer()
    expect:
    serializer.deserialize(bytes) == expectedEvent
    where:
    bytes << [
            [-84, -19, 0, 5, 115, 114, 0, 49, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 77, 101, 116, 97, 100, 97, 116, 97, 82, 101, 103, 105, 115, 116, 101, 114, 101, 100, 7, -23, 104, -9, 56, 12, 83, -115, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[],
            [-84, -19, 0, 5, 115, 114, 0, 45, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 83, 97, 109, 112, 108, 101, 82, 101, 99, 101, 105, 118, 101, 100, 2, 64, 111, -67, -127, 5, -79, 112, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[],
            [-84, -19, 0, 5, 115, 114, 0, 51, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 80, 97, 115, 115, 101, 100, 81, 117, 97, 108, 105, 116, 121, 67, 111, 110, 116, 114, 111, 108, 69, 91, -3, -118, 9, -39, -50, -105, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[],
            [-84, -19, 0, 5, 115, 114, 0, 51, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 70, 97, 105, 108, 101, 100, 81, 117, 97, 108, 105, 116, 121, 67, 111, 110, 116, 114, 111, 108, 21, 47, -86, -126, 100, 87, -120, 50, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[],
            [-84, -19, 0, 5, 115, 114, 0, 46, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 76, 105, 98, 114, 97, 114, 121, 80, 114, 101, 112, 97, 114, 101, 100, -9, -99, 16, 14, -4, -82, -12, -92, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[],
            [-84, -19, 0, 5, 115, 114, 0, 48, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 101, 118, 101, 110, 116, 115, 46, 68, 97, 116, 97, 77, 97, 100, 101, 65, 118, 97, 105, 108, 97, 98, 108, 101, -70, 124, -114, -25, 102, 27, -92, -19, 2, 0, 0, 120, 114, 0, 35, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 69, 118, 101, 110, 116, 101, -100, -92, 17, 2, 119, 72, 49, 2, 0, 2, 76, 0, 10, 111, 99, 99, 117, 114, 114, 101, 100, 79, 110, 116, 0, 19, 76, 106, 97, 118, 97, 47, 116, 105, 109, 101, 47, 73, 110, 115, 116, 97, 110, 116, 59, 76, 0, 10, 115, 97, 109, 112, 108, 101, 67, 111, 100, 101, 116, 0, 36, 76, 108, 105, 102, 101, 47, 113, 98, 105, 99, 47, 100, 111, 109, 97, 105, 110, 47, 115, 97, 109, 112, 108, 101, 47, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, 59, 120, 112, 115, 114, 0, 13, 106, 97, 118, 97, 46, 116, 105, 109, 101, 46, 83, 101, 114, -107, 93, -124, -70, 27, 34, 72, -78, 12, 0, 0, 120, 112, 119, 13, 2, -1, -113, -29, 16, 20, 100, 20, 0, 0, 0, 0, 0, 120, 115, 114, 0, 34, 108, 105, 102, 101, 46, 113, 98, 105, 99, 46, 100, 111, 109, 97, 105, 110, 46, 115, 97, 109, 112, 108, 101, 46, 83, 97, 109, 112, 108, 101, 67, 111, 100, 101, -64, -86, 84, 127, -93, -98, -110, 89, 2, 0, 1, 76, 0, 4, 116, 101, 120, 116, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 10, 81, 65, 66, 67, 68, 48, 48, 49, 65, 48] as byte[]
    ]
    expectedEvent << [
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