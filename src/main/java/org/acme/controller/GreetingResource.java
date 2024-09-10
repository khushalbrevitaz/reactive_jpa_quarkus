package org.acme.controller;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.User;
import org.acme.service.UserService;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.List;

@Path("/rest")
public class GreetingResource {

    @Inject
    UserService userService;

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/users")
//    public Uni<List<User>> getUser() {
//        return userService.getUser();
//    }

    @GET
    @Path("/findOrCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<User> findOrCreateUser(@QueryParam("email") String email) {
//        checkConfigs();
        return userService.findOrCreateUser(email);
    }
//
//    @PostConstruct
//    public void checkConfigs() {
//        String hibernateGeneration = ConfigProvider.getConfig().getValue("quarkus.hibernate-orm.database.generation", String.class);
//        int maxPoolSize = ConfigProvider.getConfig().getValue("quarkus.datasource.reactive.max-size", Integer.class);
//        long connectionTimeout = ConfigProvider.getConfig().getValue("quarkus.datasource.reactive.acquisition-timeout", Long.class);
//        int retryAttempts = ConfigProvider.getConfig().getValue("quarkus.datasource.jdbc.acquire-retry-attempts", Integer.class);
//
//        System.out.println("Hibernate Generation: " + hibernateGeneration);
//        System.out.println("Max Pool Size: " + maxPoolSize);
//        System.out.println("Connection Timeout: " + connectionTimeout);
//        System.out.println("Acquire Retry Attempts: " + retryAttempts);
//    }

    @GET
    @Path("/reactiveSP")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Integer> makeDelay() {
        return userService.invokeSleepProcedure();
    }
}
