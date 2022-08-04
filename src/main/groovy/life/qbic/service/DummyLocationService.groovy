package life.qbic.service

import life.qbic.StatusMapper
import life.qbic.datamodel.people.Address
import life.qbic.datamodel.samples.Location
import life.qbic.domain.sample.Status

import javax.inject.Singleton


@Singleton
class DummyLocationService implements IDummyLocationFactory {
  @Override
  Location dummyLocation(Status status, Date arrivalDate) {
    String name = "dummy location"
    String responsiblePerson = "N/A"
    String responsibleEmail = "N/A"
    life.qbic.datamodel.samples.Status statusV1 = StatusMapper.toStatusV1(status)
    return new Location(
            address: new Address(),
            arrivalDate: arrivalDate,
            forwardDate: null,
            name: name,
            responsibleEmail: responsibleEmail,
            responsiblePerson: responsiblePerson,
            status: statusV1)
  }
}
