package life.qbic.service

import life.qbic.datamodel.samples.Location
import life.qbic.domain.sample.Status

/**
 * Provides dummy location objects containing provided information.
 *
 * @since 2.0.0
 */
interface IDummyLocationFactory {

  /**
   * Creates a dummy location with status and arrival date
   * @param status the status for this location
   * @param arrivalDate the arrival date
   * @return a dummy location with the information provided
   */
  Location dummyLocation(Status status, Date arrivalDate)
}
