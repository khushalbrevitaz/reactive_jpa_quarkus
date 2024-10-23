package org.acme.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.entity.User;
import org.acme.repository.UserRepository;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    UserRepository userRepository;

    @Inject
    Mutiny.SessionFactory sessionFactory;

//    @PostConstruct
//    public void checkConfigs() {
//        String hibernateGeneration = ConfigProvider.getConfig().getValue("quarkus.hibernate-orm.database.generation", String.class);
//        int maxPoolSize = ConfigProvider.getConfig().getValue("quarkus.datasource.reactive.max-size", Integer.class);
//        long connectionTimeout = ConfigProvider.getConfig().getValue("quarkus.datasource.reactive.acquisition-timeout", Long.class);
//        int retryAttempts = ConfigProvider.getConfig().getValue("quarkus.datasource.jdbc.acquire-retry-attempts", Integer.class);
//
//        log.info("---------- Hibernate Generation: {}",  hibernateGeneration);
//        log.info("---------- Max Pool Size: {}", maxPoolSize);
//        log.info("---------- Connection Timeout: {}",  connectionTimeout);
//        log.info("---------- Acquire Retry Attempts: {}",  retryAttempts);
//    }

    public Uni<User> findOrCreateUser(String email) {
        log.info("get or create user by email = {}", email);
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

    public Uni<Integer> callStoredProcedureWithFakeDelay() {
        log.info("calling fake_delay procedure");
        return sessionFactory.withSession(session ->
                session.createNativeQuery("CALL fake_delay()")
                        .executeUpdate());

    }

}


