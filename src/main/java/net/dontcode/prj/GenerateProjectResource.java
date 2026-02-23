package net.dontcode.prj;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/generate")
@ApplicationScoped
public class GenerateProjectResource {
    private static Logger log = LoggerFactory.getLogger(GenerateProjectResource.class);

    @Inject
    ProjectService projectService;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> generateProject(Document body, @HeaderParam("DbName") String dbName) {
        return projectService.insertProject(body, dbName);
    }

}
