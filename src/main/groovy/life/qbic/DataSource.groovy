package life.qbic

import java.sql.Connection

/**
 * <b>Interface Datasource</b>
 *
 * <p>A source that provides connections to a persistent datasource.</p>
 * @since 1.2.1
 */
interface DataSource {

    /**
     * Provides a new {@link Connection} object instance to be able to to submit
     * queries to the underlying datasource.
     *
     * The caller MUST handle the closing of the connection, after the connection is not used anymore.
     *
     * Since the {@link Connection} class implements the {@link AutoCloseable} interface, you wrap the
     * execution of the connection object with a try-with-resource statement or call the
     * {@link AutoCloseable#close()} method explicitly.
     * @return a connection to the datasource
     */
    Connection getConnection()
}
