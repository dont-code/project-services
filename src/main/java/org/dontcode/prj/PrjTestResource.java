package org.dontcode.prj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
}