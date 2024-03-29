package life.qbic.controller

import life.qbic.datamodel.samples.Location
import life.qbic.domain.sample.Status
import life.qbic.service.DummyLocationService
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocationArrivalDateParserSpec extends Specification {


  def "The instant obtained from a location equals the date input into the location (ignoring seconds)."() {
    given:
    def instant = Instant.now()
    def instantWithoutSeconds = LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), ZoneOffset.UTC)
            .withSecond(0)
            .withNano(0)
            .toInstant(ZoneOffset.UTC)
    def date = instantWithoutSeconds.toDate()
    Location location = new DummyLocationService().dummyLocation(Status.DATA_AVAILABLE, date)

    expect:
    instantWithoutSeconds == LocationArrivalDateParser.arrivalTimeInstant(location)
  }
}
