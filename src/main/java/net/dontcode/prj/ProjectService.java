package net.dontcode.prj;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProjectService {
    private static Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    @MongoClientName("projects")
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "projects-database-name")
    String projectDbName;

    public Multi<Document> listProjects (UriInfo info, String dbName) {
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

    public Uni<Response> getProject (String projectName, String dbName) {
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

    public Uni<Response> updateProject (String projectName, String dbName, Document body) {
        changeIdToObjectId(body);
        Uni<Response> ret = getProjects(dbName).findOneAndReplace(new Document().append("_id", body.get("_id")), body,
                new FindOneAndReplaceOptions().upsert(false).returnDocument(ReturnDocument.AFTER)).map(document -> {
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

    public Uni<Response> deleteProject (String projectName, String dbName) {
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

    public Uni<Response> insertProject(Document body,String dbName) {
        //System.out.println("Received"+ body);
        return getProjects(dbName).insertOne(body).map(result -> {
            changeIdToString(body);
            return Response.ok(body).build();
        });
    }

    protected ReactiveMongoCollection<Document> getProjects(String dbName) {
        return getDatabase(dbName).getCollection("projects", Document.class);
    }

    protected ReactiveMongoDatabase getDatabase (String dbName) {
        if( dbName==null) dbName = projectDbName;
        return mongoClient.getDatabase(dbName);
    }

}
