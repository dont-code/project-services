package net.dontcode.prj.generate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnError;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;
import net.dontcode.core.project.DontCodeProjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/generate")
public class GenerateProjectResource {
    private static Logger log = LoggerFactory.getLogger(GenerateProjectResource.class);

    @Inject
    protected GenerateProjectService service;

    public GenerateProjectResource() {
    }


    @OnOpen
    public String onOpen() {
        return "Hello, please describe the application you want to generate.";
    }

    @OnTextMessage()
    public String onMessage(String message) {
        return projectToString(service.generateProjectJson(message));
    }

    @OnError
    public String onError(Throwable throwable) {
        return throwable.getMessage();
    }

    protected String projectToString (GenerateProjectModel prj) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(prj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error decoding project", e);
        }
        return json;
    }
}
