package life.qbic

import javax.inject.Inject
import javax.inject.Singleton
import java.sql.Connection

@Singleton
class QBiCDataSource implements DataSource {

    javax.sql.DataSource source

    @Inject QBiCDataSource (javax.sql.DataSource source) {
        println "init datasource"
        this.source = source
    }

    @Override
    Connection getConnection() {
        return this.source.connection
    }
}