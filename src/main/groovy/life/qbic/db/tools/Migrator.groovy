package life.qbic.db.tools

import groovy.sql.Sql
import life.qbic.api.rest.v2.samples.SampleStatusDto
import life.qbic.api.rest.v2.samples.SamplesControllerV2
import life.qbic.api.rest.v2.samples.StatusChangeRequest

import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource
import java.sql.Timestamp
import java.time.Instant

import static java.util.Objects.requireNonNull

@Singleton
class Migrator {

  private final DbInteractor dbInteractor
  private final SamplesControllerV2 controllerV2

  @Inject
  Migrator(DbInteractor dbInteractor, SamplesControllerV2 controllerV2) {
    this.dbInteractor = dbInteractor
    this.controllerV2 = controllerV2
  }

  /**
   * migrates data from the old data schema to the schema of v2. Starts at the earliestModification.
   * @param earliestModification
   */
  void migrateFromVersionOneToVersionTwo(Instant earliestModification) {
    dbInteractor.statusChangeRequests(earliestModification).forEach(it -> controllerV2.moveSampleToStatus(it.sampleCode, new StatusChangeRequest(it.statusDto, it.arrivalTime.toString())))
  }

  /**
   * migrates data from the old data schema to the schema of v2.
   */
  void migrateFromVersionOneToVersionTwo() {
    dbInteractor.statusChangeRequests(new Date(0).toInstant()).forEach(it -> controllerV2.moveSampleToStatus(it.sampleCode, new StatusChangeRequest(it.statusDto, it.arrivalTime.toString())))
  }

  static class StatusChange {
    final String sampleCode
    final SampleStatusDto statusDto
    final Instant arrivalTime

    StatusChange(String sampleCode, SampleStatusDto statusDto, Instant arrivalTime) {
      this.sampleCode = sampleCode
      this.statusDto = statusDto
      this.arrivalTime = arrivalTime
    }
  }

  @Singleton
  static class DbInteractor {

    private final DataSource dataSource

    @Inject
    DbInteractor(DataSource dataSource) {
      this.dataSource = dataSource
    }

    List<StatusChange> statusChangeRequests(Instant earliestModification) {
      def connection = requireNonNull(dataSource.getConnection())
      String query = "SELECT sample_id, sample_status, arrival_time FROM samples_locations WHERE arrival_time >= :earliestModification ORDER BY arrival_time"
      try (Sql sql = new Sql(connection)) {
        def rows = sql.rows(query, [earliestModification: Timestamp.from(earliestModification)])
        return rows.stream()
                .map(it -> new StatusChange(
                        it.get("sample_id") as String,
                        SampleStatusDto.valueOf(it.get("sample_status") as String),
                        (it.get("arrival_time") as Timestamp).toInstant()))
                .sorted((it1, it2) -> it1.arrivalTime.compareTo(it2.arrivalTime))
                .collect()
      }
    }
  }
}
