package domain.sample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

public class DomainEventSerializer {

  public <T extends SampleEvent> Blob serialize(T event) {
    try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteOutputStream)) {
      objectStream.writeObject(event);
      return new SerialBlob(byteOutputStream.toByteArray());
    } catch (IOException | SQLException e) {
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
