package life.qbic.infrastructure.serialization.event;

import life.qbic.domain.sample.SampleEvent;

public interface  EventSerializer<T extends SampleEvent>  {

  String serialize(T in);

}
