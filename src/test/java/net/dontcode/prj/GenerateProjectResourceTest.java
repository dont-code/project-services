package net.dontcode.prj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.websocket.*;
import net.dontcode.core.Message;
import net.dontcode.core.project.*;
import net.dontcode.prj.generate.GenerateProjectModel;
import net.dontcode.prj.generate.GenerateProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
public class GenerateProjectResourceTest {

    @TestHTTPResource("/generate")
    URI generateUri;

    @InjectMock
    GenerateProjectService serviceMock;

    @Test
    public void testGeneration() throws DeploymentException, IOException, InterruptedException {
        DontCodeProjectEntities[] entities = new DontCodeProjectEntities[]{};
        Mockito.when(serviceMock.generateProjectJson(anyString())).thenReturn(
                new GenerateProjectModel("Here is an application that would fit", null,
                    new DontCodeProjectModel("Test", "Test application.",
                            new DontCodeProjectContent(
                                    new DontCodeProjectCreation("Test App", DontCodeProjectCreationType.application, entities))))
        );
        ClientTestSession.opened=false;
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(ClientTestSession.class, generateUri)) {
            // Wait the data to be saved in the database
/**            for (int i = 0; i < 10; i++) {
                Thread.sleep(50);
                if( ClientTestSession.opened) {
                    break;
                }
            }
            Assertions.assertTrue(ClientTestSession.opened, "Session was not opened");**/
            ClientTestSession.response=null;

            session.getAsyncRemote().sendText("Generate a new Test Application").get();

            // Wait for the answer
            for (int i = 0; i < 10; i++) {
                Thread.sleep(50);
                if( ClientTestSession.response!=null) {
                    break;
                }
            }

            Assertions.assertNotNull(ClientTestSession.response);

            GenerateProjectModel model=parseResponse (ClientTestSession.response);
            Assertions.assertNotNull(model.model().name());

            ClientTestSession.response=null;
            session.getAsyncRemote().sendText("The application should be named Super Test").get();

            // Wait for the answer
            for (int i = 0; i < 10; i++) {
                Thread.sleep(50);
                if( ClientTestSession.response!=null) {
                    break;
                }
            }

            Assertions.assertNotNull(ClientTestSession.response);
            model=parseResponse (ClientTestSession.response);
            Assertions.assertNotNull(model.model().name());
            Mockito.verify(serviceMock, Mockito.times(2)).generateProjectJson(anyString());

        } catch (ExecutionException e) {
            System.err.println(e.getCause().getMessage());
            throw new RuntimeException(e);
        }
    }

    private GenerateProjectModel parseResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        GenerateProjectModel obj = null;
        try {
            obj = mapper.readValue(response, GenerateProjectModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException( "Cannot decode ProjectModel "+ response);
        }
        return obj;
    }

    @ClientEndpoint
    public static class ClientTestSession {

        public static boolean opened=false;
        public static String response=null;

        public ClientTestSession () {
        }

        @OnOpen
        public void open(Session session) {
            opened=true;
        }

        @OnMessage
        void message(String msg) throws DecodeException {
            response=msg;
        }

        @OnError
        void error (Throwable error) {
            System.err.println("Error "+ error.getMessage());
        }

    }


}
