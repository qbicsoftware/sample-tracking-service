package api.rest.v2.samples;

/**
 * <b>Requests the sample status to be changed.</b>
 *
 * <p>A request body containing information about a sample change. Used for external calls to the
 * REST endpoints.</p>
 *
 * @since 2.0.0
 */
public class StatusChangeRequest {

  /**
   * The sample status value.
   */
  public String status;

  /**
   * The instant from which on this status is valid (e.g. 2022-07-27T00:00:01.352Z);
   */
  public String validFrom;

}
