package life.qbic.api.rest.v2.samples

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import life.qbic.application.ApplicationException
import life.qbic.datamodel.samples.Sample
import life.qbic.domain.notification.INotificationRepository
import life.qbic.domain.sample.SampleEventDatasource
import org.junit.Before
import org.junit.Test

import java.time.Instant

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class SamplesControllerV2Test {

  private SamplesControllerV2 samples
  private String existingCode = "QABCD001A0";
  private String missingSampleCode = "QABCD002ME";

  @Before
  void setupMock() {
    SampleEventDatasource sampleEventDatasource = new SampleEventSourceMock()
    INotificationRepository notificationRepository = new NotificationRepoMock()
    
    samples = new SamplesControllerV2(sampleEventDatasource, notificationRepository);
  }

  @Test
  void testMalformedSample() throws Exception {
    String message
    String code = "wrong"
    try {
      HttpResponse response = samples.getSampleStatus(code)
    } catch (IllegalArgumentException responseException) {
      message = responseException.getMessage()
    }
    assertEquals(message, "Sample code '"+code+"' is invalid.")
  }

  @Test
  void testSample() throws Exception {
    HttpResponse response = samples.getSampleStatus(existingCode);
    String statusName = response.body.orElse(null);
    assertEquals('METADATA_REGISTERED', statusName)
  }

  @Test
  void testMissingSample() throws Exception {
    String message
    try {
      HttpResponse response = samples.getSampleStatus(missingSampleCode);
    } catch (ApplicationException responseException) {
      message = responseException.getMessage()
    }
    assertEquals(message, "Sample "+missingSampleCode+ " was not found.")
  }

  @Test
  void testStatusMalformedSample() throws Exception {
    String message
    String code = ""
    try {
      HttpResponse response = samples.moveSampleToStatus(code, new StatusChangeRequest("SAMPLE_RECEIVED", Instant.now().toString()))
    } catch (IllegalArgumentException responseException) {
      message = responseException.getMessage()
    }
    assertEquals(message, "Sample code '"+code+"' is invalid.")
  }

  @Test
  void testStatus() throws Exception {
    HttpResponse response = samples.moveSampleToStatus(existingCode, new StatusChangeRequest("SAMPLE_RECEIVED", Instant.now().toString()))
    assertEquals(200, response.getStatus().getCode())
  }

  @Test
  void testWrongStatus() throws Exception {
    String message
    try {
      HttpResponse response = samples.moveSampleToStatus(existingCode, new StatusChangeRequest("WRONG", Instant.now().toString()))
    } catch (IllegalArgumentException responseException) {
      message = responseException.getMessage()
    }
    assertEquals(message, "Provided sample status not recognized: WRONG")
  }

  @Test
  void testSetStatusUnknownSample() throws Exception {
    String message
    try {
    HttpResponse response = samples.moveSampleToStatus(missingSampleCode, new StatusChangeRequest("SAMPLE_RECEIVED", Instant.now().toString()))
    } catch (IllegalArgumentException responseException) {
      message = responseException.getMessage()
    }
    assertEquals(message, "Provided sample code not recognized: "+missingSampleCode)
  }

}
