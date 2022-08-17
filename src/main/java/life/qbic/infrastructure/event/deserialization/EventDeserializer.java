package life.qbic.infrastructure.event.deserialization;

public interface EventDeserializer<T> {

  T deserialize(String json);
}
