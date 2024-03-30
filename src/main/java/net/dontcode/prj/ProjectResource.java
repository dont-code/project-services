package net.dontcode.prj;

import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

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
    public Multi<Document> listProjects (UriInfo info, @RestHeader("DbName") String dbName) {
        log.debug("Hostname = {}, DbName Header = {}", info.getAbsolutePath(), dbName);
/*        Multi<Document> ret = Multi.createFrom().emitter(multiEmitter -> {
            for (int i=0;i<100000;i++) {
                multiEmitter.emit(Document.parse("""
                        {
                        "projectId":"EGFERGG",
                        "status":"PIZZADAZRFERFERF",
                        "data":{
                                "value":"name"
                            }
                        }
                        """));
            }
            multiEmitter.complete();
        });*/
        Multi<Document> ret = getProjects(dbName).find().map(document -> {
            changeIdToString(document);
            return document;
        });
        return ret;
    }

    @GET
    @Path("/{projectName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Uni<Response> getProject (String projectName, @HeaderParam("DbName") String dbName) {
        Uni<Response> ret = getProjects(dbName).find(new Document().append("name", projectName)).toUni().map(document -> {
           if( document != null) {
               changeIdToString(document);
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
    public Uni<Response> updateProject (String projectName, @HeaderParam("DbName") String dbName, Document body) {
        changeIdToObjectId(body);
        Uni<Response> ret = getProjects(dbName).findOneAndReplace(new Document().append("_id", body.get("_id")), body).map(document -> {
            if( document != null) {
                changeIdToString(document);
                return Response.ok(document).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        });
        return ret;
    }

    protected void changeIdToObjectId(Document body) {
        body.put("_id", new ObjectId(body.getString("_id")));
    }

    protected void changeIdToString(Document body) {
        body.put("_id", body.getObjectId("_id").toHexString());
    }

    @DELETE
    @Path("/{projectName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteProject (String projectName, @HeaderParam("DbName") String dbName) {
        Uni<Response> ret = getProjects(dbName).findOneAndDelete(new Document().append("name", projectName)).map(document -> {
            if( document != null) {
                changeIdToString(document);
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
    public Uni<Response> insertProject(Document body, @HeaderParam("DbName") String dbName) {
        //System.out.println("Received"+ body);
        return getProjects(dbName).insertOne(body).map(result -> {
            changeIdToString(body);
            return Response.ok(body).build();
        });
    }

    protected ReactiveMongoCollection<Document> getProjects(String dbName) {
        return getDatabase(dbName).getCollection("projects", Document.class);
    }

    /*protected ReactiveMongoCollection<Document> getProjects() {
        return getDatabase().getCollection("projects", Document.class);
    }
    protected <T> ReactiveMongoCollection<T> getProjects(Class<T> clazz) {
        return getDatabase().getCollection("projects", clazz);
    }

    protected ReactiveMongoDatabase getDatabase () {
        return mongoClient.getDatabase(projectDbName);
    }*/


    protected ReactiveMongoDatabase getDatabase (String dbName) {
        if( dbName==null) dbName = projectDbName;
        return mongoClient.getDatabase(dbName);
    }
}
