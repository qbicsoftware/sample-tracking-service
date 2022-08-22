package life.qbic.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.time.Instant;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import life.qbic.auth.Authentication;
import life.qbic.db.tools.Migrator;

/**
 * Utility tool to migrate data.
 *
 * @since 2.0.0
 */
@Requires(beans = Authentication.class)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/v2/migrate")
public class DatabaseMigrationController {

  private final Migrator migrator;

  @Inject
  public DatabaseMigrationController(Migrator migrator) {
    this.migrator = migrator;
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @RolesAllowed("WRITER")
  void migrateAllData() {
    migrator.migrateFromVersionOneToVersionTwo();
  }

  @Get(uri = "/{utcDateTime}", produces = MediaType.APPLICATION_JSON)
  @RolesAllowed("WRITER")
  void migrateAllData(@PathVariable String utcDateTime) {
    Instant earliestTime = Instant.parse(utcDateTime);
    migrator.migrateFromVersionOneToVersionTwo(earliestTime);
  }
}
