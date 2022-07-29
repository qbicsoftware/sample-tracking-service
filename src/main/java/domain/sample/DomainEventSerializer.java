package domain.sample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DomainEventSerializer {

  public <T extends SampleEvent> byte[] serialize(T event) {
    try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteOutputStream)) {
      objectStream.writeObject(event);
      return byteOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public SampleEvent deserialize(byte[] blob) {
    try (ByteArrayInputStream byteInputStream =
        new ByteArrayInputStream(blob);
        ObjectInputStream objectStream = new ObjectInputStream(byteInputStream)) {
      return (SampleEvent) objectStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
