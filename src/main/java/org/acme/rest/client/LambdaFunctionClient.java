package org.acme.rest.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/users/simulate")
public interface LambdaFunctionClient {
    @GET
    @Produces(MediaType.TEXT_HTML)
    Uni<String> simulate();
}
