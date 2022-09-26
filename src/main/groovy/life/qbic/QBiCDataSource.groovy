package life.qbic

import groovy.util.logging.Log4j2

import javax.inject.Inject
import javax.inject.Singleton
import java.sql.Connection
import java.sql.SQLTimeoutException

/**
 * <b>QBiCDataSource Class</b>
 *
 * <p>Provides access to QBiC's persistent data.</p>
 *
 * @since 1.2.1
 */
@Singleton
@Log4j2
class QBiCDataSource implements DataSource {

    private final static int MAX_RETRY_COUNT = 3

    private final javax.sql.DataSource source

    @Inject
    QBiCDataSource(javax.sql.DataSource source) {
        this.source = source
    }

    /**
     * {@InheritDocs}
     */
    @Override
    Connection getConnection() throws TimeOutException {
        Connection connection = null
        int turn = 1
        while (!connection) {
            if (turn == MAX_RETRY_COUNT) {
                throw new TimeOutException("Maximum number of tries reached ($MAX_RETRY_COUNT), connection to database server timed out repeatedly.")
            }
            try {
                connection = this.source.getConnection()
            } catch (SQLTimeoutException e) {
                log.error("Turn $turn to get connection failed.")
                log.error("Connection to database server timed out.", e)
            }
            turn++;
        }
        return this.source.connection
    }
}
