package org.acme.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;


@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<Void> findOrCreateUser(String email) {
        LOG.info("Executing findOrCreateUser on thread: " + Thread.currentThread().getName());

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return invokeRandomSleepProcedure();
    }

    private Uni<Void> invokeRandomSleepProcedure() {
        var startTime = System.currentTimeMillis();
        LOG.info("Invoking random_sleep stored procedure on thread: " + Thread.currentThread().getName());
        return sessionFactory.withSession(session ->
                session.createNativeQuery("CALL random_sleep()")
                        .executeUpdate()
                        .onItem().invoke(() -> {
                            // Calculate and log the time taken after the procedure is executed
                            var took = System.currentTimeMillis() - startTime;
                            LOG.info("Completed random_sleep stored procedure. Took " + took + " ms");
                        })
                        .replaceWithVoid());

    }

//    public Uni<Integer> invokeRandomSleepProcedure() {
//        // Calling the stored procedure reactively
//        return session.createNativeQuery("CALL random_sleep(?)")
//                .setParameter(1, Mutiny.Session.QueryParameterMode.OUT)
//                .getSingleResult()
//                .map(result -> (Integer) result);
//    }


}


