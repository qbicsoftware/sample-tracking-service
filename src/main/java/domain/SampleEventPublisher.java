package domain;

/**
 * <p>Publishes sample events in the application.</p>
 *
 * @since <version tag>
 */
public interface SampleEventPublisher {

  void publish(SampleEvent sampleEvent);

}
