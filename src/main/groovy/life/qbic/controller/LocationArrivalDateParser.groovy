package life.qbic.controller

import life.qbic.datamodel.samples.Location

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant

class LocationArrivalDateParser {

  static Instant arrivalTimeInstant(Location location) {
    // assumed internal non-standard format (as of data-model-lib:2.17.0) does not contain second information and is in MEZ
    TimeZone tz = TimeZone.getTimeZone("MEZ")
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.setTimeZone(tz)
    return df.parse(location.getArrivalDate()).toInstant()
  }

}
