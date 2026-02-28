package net.dontcode.prj;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import net.dontcode.core.project.DontCodeProjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/test")
public class PrjTestResource {
    private static Logger log = LoggerFactory.getLogger(PrjTestResource.class);

//    @Inject
//    GenerateProjectService generateProjectService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testAsIde(String update) {
        log.debug("Receiving from test");
        log.trace("{}", update);
      //  previewServiceClient.receiveUpdate(update);
        return Response.ok().build();
    }

    @GET
    @Path("/redirect")
    public Uni<Response> redirect() {
        return Uni.createFrom().item(Response.temporaryRedirect(URI.create("http://localhost:8083/project")).build());
    }

    @GET
    @Path("/cookie")
    public Uni<Response> cookie(@CookieParam("Test") String testCookie) {
        int nextValue=0;
        if (testCookie!=null) {
            try {
                nextValue = Integer.valueOf(testCookie);
                nextValue++;
            } catch (Throwable e) {
                nextValue=-1;
            }
        }
        return Uni.createFrom().item(Response.ok("Cookie got:"+testCookie).cookie(
                new NewCookie(new Cookie("Test", Integer.toString(nextValue), "/", "localhost"), null,
                        60*60, false)).build());
    }

/*    @GET
    @Path("/generator")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> generator(@QueryParam("message") String message) {
            // We create a session context just to call the session scoped service
        ManagedContext sessionContext = null;
        try {
            sessionContext = Arc.requireContainer().sessionContext();
            if (!sessionContext.isActive()) {
                sessionContext.activate();
            }
            DontCodeProjectModel response=generateProjectService.generateProjectJson(message);
            return Uni.createFrom().item(Response.ok().entity(response).build());

        } finally {
           if (sessionContext != null) {
            sessionContext.terminate();
           }
        }
    }*/
}
