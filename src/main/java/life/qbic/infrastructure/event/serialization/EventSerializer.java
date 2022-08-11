package life.qbic.infrastructure.event.serialization;

import life.qbic.domain.sample.SampleEvent;

public interface  EventSerializer<T extends SampleEvent>  {

  String serialize(T in);

}
