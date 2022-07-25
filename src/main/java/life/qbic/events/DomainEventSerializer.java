package life.qbic.events;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import life.qbic.domain.events.DomainEvent;

public class DomainEventSerializer {

  public <T extends DomainEvent> String serialize(T event) {
    try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteOutputStream)) {
      objectStream.writeObject(event);
      return Base64.getEncoder().encodeToString(byteOutputStream.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public DomainEvent deserialize(String serializedEvent) {
    try (ByteArrayInputStream byteInputStream =
            new ByteArrayInputStream(Base64.getDecoder().decode(serializedEvent));
        ObjectInputStream objectStream = new ObjectInputStream(byteInputStream)) {
      return (DomainEvent) objectStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
