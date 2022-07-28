package domain.sample;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Sample Event Publisher</b>
 *
 * <p>Thread-local sample domain event publisher class. Can be used to observe certain domain event types
 * and publish domain events within the domain.
 *
 * @since 1.0.0
 */
public class SampleEventPublisher {

  private static final ThreadLocal<List<SampleEventSubscriber<? extends SampleEvent>>> subscribers = new ThreadLocal<>();

  private static final ThreadLocal<Boolean> publishing =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  public static SampleEventPublisher instance() {
    return new SampleEventPublisher();
  }

  public SampleEventPublisher() {
    super();
  }

  public <T extends SampleEvent> void subscribe(SampleEventSubscriber<T> subscriber) {
    if (publishing.get()) {
      return;
    }
    List<SampleEventSubscriber<? extends SampleEvent>> registeredSubscribers = subscribers.get();

    if (registeredSubscribers == null) {
      registeredSubscribers = new ArrayList<>();
      subscribers.set(registeredSubscribers);
    }

    registeredSubscribers.add(subscriber);
  }

  public <T extends SampleEvent> void publish(final T domainEvent) {
    if (publishing.get()) {
      return;
    }
    try {
      publishing.set(Boolean.TRUE);
      List<SampleEventSubscriber<? extends SampleEvent>> registeredSubscribers = subscribers.get();
      Class<? extends SampleEvent> domainEventType = domainEvent.getClass();
      registeredSubscribers.stream()
          .filter(subscriber -> subscriber.subscribedToEventType() == domainEventType)
          .map(it -> (SampleEventSubscriber<T>) it)
          .forEach(subscriber -> subscriber.handleEvent(domainEvent));
    } finally {
      publishing.set(Boolean.FALSE);
    }
  }

  /**
   * Removes all subscribers from the current thread's domain event subscription list.
   * <p>
   * This method is not interrupting current publishing domain event processes. In this case, the
   * method will return <code>false</code>.
   *
   * @return false, when there are currently domain events published to signal that the removal was
   * not performed; true, when the publisher was not in the process of publishing
   * and all subscribers have been removed.
   * @since 1.0.0
   */
  public boolean clear() {
    if (publishing.get()) {
      return false;
    }
    List<SampleEventSubscriber<? extends SampleEvent>> listSubscribers = subscribers.get();
    if (listSubscribers != null) {
      listSubscribers.clear();
    }
    return true;
  }
}
