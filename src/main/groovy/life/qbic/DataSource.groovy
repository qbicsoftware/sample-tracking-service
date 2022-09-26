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
     * <p>Provides a new {@link Connection} object instance to be able to to submit
     * queries to the underlying datasource.</p>
     *
     * <p>The caller <b>MUST</b> handle the closing of the connection, after the connection is not used anymore.</p>
     *
     * <p>Since the {@link Connection} class implements the {@link AutoCloseable} interface, you wrap the
     * execution of the connection object with a try-with-resource statement or call the
     * {@link AutoCloseable#close()} method explicitly.</p>
     * @return a connection to the datasource
     * @throws {@link TimeOutException} when a connection cannot be acquired from the data source
     */
    Connection getConnection() throws TimeOutException
}
