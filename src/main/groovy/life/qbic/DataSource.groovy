package life.qbic

import java.sql.Connection

interface DataSource {

    Connection getConnection()

}