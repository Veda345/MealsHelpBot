package mealsbot.http;

import com.sun.istack.internal.NotNull;
import com.xebialabs.restito.semantics.Condition;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;

public class RecipesRequesterStubServerTest {

    private final JSONParser jsonParser = new JSONParser();

    @NotNull
    private final RecipesRequester mRequester = new RecipesRequester(jsonParser);

    private static final int PORT = 3438;

    @Test
    public void test_requestUrl() throws Exception {
        runStubServer(PORT, s -> {
            whenHttp(s)
                    .match(Condition.startsWithUri("/ping"))
                    .then(stringContent("pong"));
            String result = null;
            try {
                result = mRequester.requestUrl("http://localhost:" + PORT + "/ping");
            } catch (Exception e) {
                Assert.fail();
            }
            Assert.assertEquals("pong\n", result);
        });
    }

    @Test(expected = UncheckedIOException.class)
    public void test_requestUrlNotFoundError() {
        runStubServer(PORT + 1, s -> {
            whenHttp(s)
                    .match(Condition.startsWithUri("/ping"))
                    .then(status(HttpStatus.NOT_FOUND_404));
            try {
                mRequester.requestUrl("http://localhost:" + PORT + "/ping");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void runStubServer(int port, Consumer<StubServer> callback) {
        StubServer stubServer = null;
        try {
            stubServer = new StubServer(port).run();
            callback.accept(stubServer);
        } finally {
            if (stubServer != null) {
                stubServer.stop();
            }
        }
    }
}
