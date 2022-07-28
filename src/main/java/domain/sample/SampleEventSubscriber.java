package domain.sample;

/**
 * <b>Sample Event Subscriber</b>
 *
 * <p>Clients can implement this interface to subscribe to {@link SampleEvent} and get
 * informed whenever a {@link SampleEvent} of the specified type <code>T</code> happens.
 *
 * @since 1.0.0
 */
public interface SampleEventSubscriber<T extends SampleEvent> {

  /**
   * Query the subscribed domain event type.
   *
   * @return the domain event type that is subscribed to
   * @since 1.0.0
   */
  Class<? extends SampleEvent> subscribedToEventType();

  /**
   * Callback that will be executed by the publisher.
   *
   * <p>Passes the domain event of the type that was subscribed to.
   *
   * @param event the domain event
   * @since 1.0.0
   */
  void handleEvent(T event);
}
