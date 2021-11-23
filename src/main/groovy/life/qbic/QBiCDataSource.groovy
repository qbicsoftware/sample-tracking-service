package life.qbic

import javax.inject.Inject
import javax.inject.Singleton
import java.sql.Connection

/**
 * <b>QBiCDataSource Class</b>
 *
 * <p>Provides access to QBiC's persistent data.</p>
 *
 * @since 1.2.1
 */
@Singleton
class QBiCDataSource implements DataSource {

    javax.sql.DataSource source

    @Inject QBiCDataSource (javax.sql.DataSource source) {
        this.source = source
    }

    /**
     * {@InheritDocs}
     */
    @Override
    Connection getConnection() {
        return this.source.connection
    }
}
