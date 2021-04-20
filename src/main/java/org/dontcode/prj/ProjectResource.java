package org.dontcode.prj;

import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/project")
@ApplicationScoped
public class ProjectResource {
    private static Logger log = LoggerFactory.getLogger(ProjectResource.class);

    @Inject
    @MongoClientName("projects")
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "projects-database-name")
    String projectDbName;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Document> listProjects () {
        Multi<Document> ret = getProjects().find();
        return ret;
    }

    @GET
    @Path("/{projectName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Uni<Response> getProject (String projectName) {
        Uni<Response> ret = getProjects().find(new Document().append("name", projectName)).toUni().map(document -> {
           if( document != null) {
               return Response.ok(document).build();
           } else {
               return Response.status(Response.Status.NOT_FOUND).build();
           }
        });
        return ret;
    }

    @PUT
    @Path("/{projectName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> updateProject (String projectName, Document body) {
        Uni<Response> ret = getProjects().findOneAndReplace(new Document().append("name", projectName), body).map(document -> {
            if( document != null) {
                return Response.ok(document).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        });
        return ret;
    }

    @DELETE
    @Path("/{projectName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteProject (String projectName) {
        Uni<Response> ret = getProjects().findOneAndDelete(new Document().append("name", projectName)).map(document -> {
            if( document != null) {
                return Response.ok(document).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        });
        return ret;
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> insertProject(Document body) {
        System.out.println("Received"+ body);
        return getProjects().insertOne(body).map(result -> Response.ok(body).build());
    }

    protected ReactiveMongoCollection<Document> getProjects() {
        return getDatabase().getCollection("projects", Document.class);
    }
    protected <T> ReactiveMongoCollection<T> getProjects(Class<T> clazz) {
        return getDatabase().getCollection("projects", clazz);
    }

    protected ReactiveMongoDatabase getDatabase () {
        return mongoClient.getDatabase(projectDbName);
    }
}
