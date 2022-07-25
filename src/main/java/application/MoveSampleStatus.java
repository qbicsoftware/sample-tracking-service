package application;

import static org.slf4j.LoggerFactory.getLogger;

import domain.EventStore;
import domain.Sample;
import domain.SampleCode;
import domain.SampleEvent;
import domain.Status;
import java.time.Instant;
import java.util.SortedSet;
import org.slf4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class MoveSampleStatus {

  private EventStore eventStore;
  private static final Logger log = getLogger(MoveSampleStatus.class);

  public void moveSample(String sampleCode, String sampleStatus, String instant) {
    SampleCode code = SampleCode.fromString(sampleCode);
    Status status = Status.fromLabel(sampleStatus)
        .orElseThrow(
            () -> new ApplicationException(String.format("Unknown status: %s", sampleStatus)));
    Instant occurredOn = Instant.parse(instant);
    SortedSet<SampleEvent> sampleEvents = eventStore.findAllForSample(code);
    Sample sample = new Sample(code, null); //FIXME add sample event publisher
    sample.handleEvents(sampleEvents);

    sample.moveToStatus(status, occurredOn);
  }

}
