package life.qbic.infrastructure.serialization.event;

public interface EventDeserializer<T> {

  T deserialize(String json);
}
