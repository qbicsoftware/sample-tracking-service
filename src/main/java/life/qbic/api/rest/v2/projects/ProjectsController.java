package life.qbic.api.rest.v2.projects;

import static org.slf4j.LoggerFactory.getLogger;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import life.qbic.application.ProjectStatusService;
import life.qbic.auth.Authentication;
import life.qbic.domain.sample.Sample;
import org.slf4j.Logger;

@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/projects")
public class ProjectsController {

  private static final Logger log = getLogger(ProjectsController.class);

  private final ProjectStatusService projectStatusService;

  public ProjectsController(ProjectStatusService projectStatusService) {
    this.projectStatusService = projectStatusService;
  }

  @Operation(summary = "Request information about the status of samples in a project.",
      description = "Delivers the current status of all samples in the project.",
      tags = {"Sample Status", "Project Status"})
  @ApiResponse(responseCode = "200", description = "The request was fulfilled. The current status is provided in the response body.",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ProjectStatusResponse.class)))
  @Get("{projectCode}/status")
  @RolesAllowed("READER")
  public HttpResponse<?> sampleStatusInformation(@PathVariable String projectCode) {
    log.debug(String.format("Requested status information for project %s", projectCode));
    List<Sample> samples = projectStatusService.sampleStatuses(projectCode);
    log.debug(String.format("Processed request for project %s", projectCode));
    return HttpResponse.ok(ProjectStatusResponse.from(samples));
  }
}
