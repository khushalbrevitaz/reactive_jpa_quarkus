package org.acme.controller;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniJoin;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.acme.entity.User;
import org.acme.rest.client.LambdaFunctionClient;
import org.acme.service.UserService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@Path("/users")
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    @RestClient
    LambdaFunctionClient lambdaFunctionClient;

    /**
     * simple api returning hello with an artificial delay
     *
     * @return
     */
    @GET
    @Path("/simulate")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> simulate() {
        log.info("simulation request time = {}", System.currentTimeMillis());
        final Random random = new Random();
        return Uni.createFrom()
                .item("hello")
                .onItem()
                .delayIt()
                .by(Duration.ofMillis(random.nextInt(101) + 100));
    }

    /**
     * simple api returning hello without any artificial delay
     *
     * @return
     */
    @GET
    @Path("/simulate2")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> simulate2() {
        log.info("simulation request @ = {}", System.currentTimeMillis());
        return Uni.createFrom().item("hello");
    }

    /**
     * api making a call to lambda function. Lambda function create an artificial delay and then responds.
     *
     * @return
     */
    @GET
    @Path("/simulate3")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> callSimulateUserApi() {
        log.info("calling AWS lambda for simulation");
        return lambdaFunctionClient.simulate();
    }

    @GET
    @Path("/findOrCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<User> findOrCreateUser(@QueryParam("email") String email) {
        return userService.findOrCreateUser(email);
    }

    @GET
    @Path("/simulate4")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Integer> makeDelay() {
        return userService.callStoredProcedureWithFakeDelay();
    }

    @GET
    @Path("/simulate5")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> simulate5() throws Exception {
        log.info("simulate5-non-blocking: word count");
        return countWordsInFileReactive();
    }

    @GET
    @Path("/simulate6")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<String>> simulate4() throws Exception {

        log.info("simulate4-non-blocking: random delay + word count + stored procedure call");

        Uni<String> wordCountUni = countWordsInFileReactive();

        final Random random = new Random();
        Uni<String> simpleDelayUni = Uni.createFrom()
                .item("hello")
                .onItem()
                .delayIt()
                .by(Duration.ofMillis(random.nextInt(100) + 50));

        Uni<String> spUni = userService.callStoredProcedureWithFakeDelay().onItem().transform(Object::toString);

        UniJoin.Builder<String> builder = Uni.join().builder();
        builder.add(simpleDelayUni);
        builder.add(wordCountUni);
        builder.add(spUni);

        return builder.joinAll().andCollectFailures();
    }

    private Uni<String> countWordsInFileReactive() throws URISyntaxException {

        // Convert URL to file path (optional)
        java.nio.file.Path path = new File("/deployments/sample.txt").toPath();

        //java.nio.file.Path path = Paths.get("sample.txt"); // this is for local testing
        return Uni.createFrom().item(path)
                .onItem()
                .transformToMulti(p -> Multi.createFrom().items(() -> {
                    try {
                        return Files.lines(p);  // Use iterator for lazy processing
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }))
                .flatMap(line -> Multi.createFrom().items(line.split("\\s+")))
                .filter(word -> !word.isEmpty())
                .collect()
                .asList()
                .onItem()
                .transform(l -> String.valueOf(l.size()));
    }
}
