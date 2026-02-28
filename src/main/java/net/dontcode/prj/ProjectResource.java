package net.dontcode.prj;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.bson.Document;
import org.jboss.resteasy.reactive.RestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/project")
@ApplicationScoped
public class ProjectResource {
    private static Logger log = LoggerFactory.getLogger(ProjectResource.class);

    @Inject
    ProjectService projectService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Document> listProjects (UriInfo info, @RestHeader("DbName") String dbName) {
        log.debug("Hostname = {}, DbName Header = {}", info.getAbsolutePath(), dbName);
        return projectService.listProjects(info, dbName);
    }

    @GET
    @Path("/{projectName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Uni<Response> getProject (String projectName, @HeaderParam("DbName") String dbName) {
        return projectService.getProject(projectName, dbName);
    }

    @PUT
    @Path("/{projectName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateProject (String projectName, @HeaderParam("DbName") String dbName, Document body) {
        return projectService.updateProject(projectName, dbName, body);
    }

    @DELETE
    @Path("/{projectName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteProject (String projectName, @HeaderParam("DbName") String dbName) {
        return projectService.deleteProject(projectName, dbName);
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertProject(Document body, @HeaderParam("DbName") String dbName) {
        return projectService.insertProject(body, dbName);
    }

}
