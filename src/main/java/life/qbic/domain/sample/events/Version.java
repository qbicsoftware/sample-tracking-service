package life.qbic.domain.sample.events;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import life.qbic.exception.UnRecoverableException;

/**
 * A semantic version with major and minor part.
 *
 * @since 2.0.0
 */
public final class Version implements Comparable<Version>, Serializable {

  private final int major;
  private final int minor;

  private Version(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }

  public static Version create(int major, int minor) {
    return new Version(major, minor);
  }



  @Override
  public int compareTo(@NotNull Version other) {
    if (this.equals(other)) {
      return 0;
    }
    if (other.major == this.major) {
      return Integer.compare(this.minor, other.minor);
    } else {
      return Integer.compare(this.major, other.major);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Version version = (Version) o;

    if (major != version.major) {
      return false;
    }
    return minor == version.minor;
  }

  @Override
  public int hashCode() {
    int result = major;
    result = 31 * result + minor;
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s.%s", major, minor);
  }

  public static Version parse(String version) {
    if (!version.matches("\\d+\\.\\d+")) {
      throw new UnRecoverableException(
          String.format("Version %s does not match version pattern major.minor", version));
    }
    String[] split = version.split("\\.");
    return Version.create(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
  }


  public int major() {
    return major;
  }

  public int minor() {
    return minor;
  }
}
