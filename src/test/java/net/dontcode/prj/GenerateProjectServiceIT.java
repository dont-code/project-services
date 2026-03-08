package net.dontcode.prj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import jakarta.websocket.*;
import net.dontcode.prj.generate.GenerateProjectModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Vector;

@QuarkusIntegrationTest
public class GenerateProjectServiceIT {

    @TestHTTPResource("/generate")
    URI uri;

    @Test
    public void testSimpleApplication () throws DeploymentException, IOException, InterruptedException {
        TestClientGenerateApplication wsClient=new TestClientGenerateApplication();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(wsClient, uri)) {
            // Wait the data to be saved in the database
            for (int i = 0; i < 10; i++) {
                Thread.sleep(50);
                if (wsClient.opened) {
                    break;
                }
            }
            Assertions.assertTrue(wsClient.opened, "Session was not opened");

            String response=wsClient.waitForMessage(10);
            Assertions.assertNotNull(response);

            session.getBasicRemote().sendText("Please create a cooking recipe application");

            response=wsClient.waitForMessage(200);
            Assertions.assertNotNull(response);

            GenerateProjectModel model=mapToProject(response);
            Assertions.assertNotNull(model.response());

            session.getBasicRemote().sendText("Change it to support images for each type of ingredients");
            response=wsClient.waitForMessage(200);
            Assertions.assertNotNull(response);

            model=mapToProject(response);
            Assertions.assertNotNull(model.response());
        }

        /*DontCodeProjectModel response=service.generateProjectJson("Please create a cooking recipe application");
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.content().creation().entities().length > 0);*/
    }

    protected GenerateProjectModel mapToProject (String response) {
        ObjectMapper mapper = new ObjectMapper();
        GenerateProjectModel model;
        try {
            model = mapper.readValue(response, GenerateProjectModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error decoding project", e);
        }
        return model;

    }


    @ClientEndpoint()
    public class TestClientGenerateApplication {

        public boolean opened=false;
        public List<String> receivedMessages = new Vector<>();
        public Session session;

        public TestClientGenerateApplication () {

        }

        @OnOpen
        public void open(Session session) {
            this.session=session;
            opened=true;
        }

        @OnMessage
        void message(String msg) throws DecodeException {
            //MESSAGES.add(msg);
            //System.out.println(msg);
            receivedMessages.add(msg);
        }

        @OnError
        void error (Throwable error) {
            System.err.println("Error "+ error.getMessage());
        }

        public String waitForMessage (int maxTry) throws InterruptedException {
            for (int i = 0; i < maxTry; i++) {
                Thread.sleep(50);
                if (!receivedMessages.isEmpty()) {
                    break;
                }
            }

            Assertions.assertFalse(receivedMessages.isEmpty());

            return receivedMessages.removeLast();
        }
    }

}


