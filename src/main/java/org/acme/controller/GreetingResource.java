package org.acme.controller;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.User;
import org.acme.service.UserService;

import java.util.List;

@Path("/rest")
public class GreetingResource {

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    public Uni<List<User>> getUser() {
        return userService.getUser();
    }

    @GET
    @Path("/findOrCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<User> findOrCreateUser(@QueryParam("email") String email) {
        return userService.findOrCreateUser(email);
    }
}
