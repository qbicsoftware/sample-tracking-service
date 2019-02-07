package life.qbic

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class MyControllerTest {

    private static EmbeddedServer server
    private static HttpClient client

    @BeforeClass
    static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class)
        client = server
                .getApplicationContext()
                .createBean(HttpClient.class, server.getURL())
    }

    @AfterClass
    static void stopServer() {
        if (server != null) {
            server.stop()
        }
        if (client != null) {
            client.stop()
        }
    }

    @Test
    void testHello() throws Exception {
        HttpRequest request = HttpRequest.GET("/")
        String body = client.toBlocking().retrieve(request)
        assertNotNull(body)
        assertEquals(
                body,
                "Hello World"
        );
    }
}
