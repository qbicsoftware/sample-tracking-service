package life.qbic.exception;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Used to set parameters for error responses.
 */
public class ErrorParameters {

  private final HashMap<String, Object> mappings = new HashMap<>();

  public Optional<Object> get(String key) {
    return Optional.ofNullable(mappings.get(key));
  }

  public void put(String key, Object value) {
    mappings.put(key, value);
  }

  public static ErrorParameters create() {
    return new ErrorParameters();
  }

  public ErrorParameters with(String key, Object value) {
    this.put(key, value);
    return this;
  }

  public Map<String, Object> asMap() {
    return unmodifiableMap(mappings);
  }
}
