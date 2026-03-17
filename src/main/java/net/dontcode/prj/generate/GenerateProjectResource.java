package net.dontcode.prj.generate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnError;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebSocket(path = "/generate")
public class GenerateProjectResource {
    private static Logger log = LoggerFactory.getLogger(GenerateProjectResource.class);

    @Inject
    protected GenerateProjectService service;

    public GenerateProjectResource() {
    }

    @OnTextMessage()
    public Uni<String> onMessage(WebSocketConnection connection, String message) throws IOException {
       return Uni.createFrom().item(() -> {
        try {
                GenerateProjectModel model = service.generateProjectJson(connection.id(),message);
                return projectToString(model);
            } catch (Throwable err) {
                log.error("Error calling Application Generator", err);
                return errorResponse (err);
            }
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @OnError
    public String onError(Throwable throwable) {
        return errorResponse(throwable);
    }

    protected String errorResponse(Throwable throwable) {
        return projectToString(new GenerateProjectModel(null, throwable.getMessage(), null));
    }

    protected String projectToString (GenerateProjectModel prj) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(prj);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting GenerateProjectModel to JSON " + prj);
            throw new RuntimeException("Error decoding project", e);
        }
        return json;
    }
}
