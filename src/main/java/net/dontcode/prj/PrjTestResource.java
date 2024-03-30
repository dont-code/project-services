package net.dontcode.prj;

import io.smallrye.mutiny.Uni;
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
}
