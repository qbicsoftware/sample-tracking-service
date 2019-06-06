import javax.inject.Singleton
import io.micronaut.http.HttpResponse
import life.qbic.datamodel.services.*

@Singleton
interface IDBManager {

  Contact searchPersonByEmail(String email)

  HttpResponse addNewLocation(String sampleId, Location location)

  HttpResponse updateLocation(String sampleId, Location location)

  Sample searchSample(String sampleId)

  boolean updateSampleStatus(String sampleId, Status status)
}