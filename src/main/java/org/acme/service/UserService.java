package org.acme.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.User;
import org.acme.repository.UserRepository;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import java.time.Duration;

import static io.vertx.mutiny.pgclient.PgPool.client;


@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);
//    @Inject
//    PgPool client;
//
//    @Inject
//    Vertx vertx;

    @Inject
    UserRepository userRepository;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<User> findOrCreateUser(String email) {
        LOG.info("Executing findOrCreateUser on thread: " + Thread.currentThread().getName());

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
       return userRepository.findByEmail(email)
               .onItem().ifNull().switchTo(() -> {
                    // Ensure this operation stays in the reactive context
                    User newUser = new User(email);
                    return Panache.withTransaction(() -> userRepository.persist(newUser))
                            .replaceWith(newUser);
                });

    }



    public Uni<Integer> invokeRandomSleepProcedure() {
        var startTime = System.currentTimeMillis();
        LOG.info("Invoking random_sleep stored procedure on thread: " + Thread.currentThread().getName());
        return sessionFactory.withSession(session ->
                session.createNativeQuery("CALL random_sleep()")
                        .executeUpdate());
//                        .onItem().invoke(() -> {
//                            // Calculate and log the time taken after the procedure is executed
////                            var took = System.currentTimeMillis() - startTime;
////                            LOG.info("Completed random_sleep stored procedure in non-blocking. Took " + took + " ms");
//                        })
//                        .replaceWithVoid());

    }



    public Uni<Integer> invokeSleepProcedure() {
        var startTime = System.currentTimeMillis();
        LOG.info("Invoking random_sleep stored procedure on thread: " + Thread.currentThread().getName());
        return sessionFactory.withSession(session ->
                session.createNativeQuery("CALL random_sleep()")
                        .executeUpdate());

    }

//    public Uni<Void> invokeRandomSleepProcedure() {
//var startTime = System.currentTimeMillis();
//    LOG.info("Invoking random_sleep stored procedure on thread: " + Thread.currentThread().getName());
//    return client.preparedQuery("CALL random_sleep()")
//        .execute()
//        .onItem().invoke(() -> {
//        // Calculate and log the time taken after the procedure is executed
//        var took = System.currentTimeMillis() - startTime;
//        LOG.info("Completed random_sleep stored procedure. Took " + took + " ms");
//    })
//            .replaceWithVoid();
//
//    }

}


