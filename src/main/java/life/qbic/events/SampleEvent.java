package life.qbic.events;

import life.qbic.domain.SampleCode;
import life.qbic.domain.events.DomainEvent;

import java.time.Instant;
/**
 * A domain event in the sample domain.
 */
public interface SampleEvent {
    SampleCode sampleCode();
    Instant occurredOn();
}