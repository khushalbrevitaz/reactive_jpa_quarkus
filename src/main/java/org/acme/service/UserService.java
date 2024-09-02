package org.acme.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.entity.User;
import org.acme.repository.UserRepository;
import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    public Uni<List<User>> getUser() {
        return userRepository.listAll();
    }


    public Uni<User> findOrCreateUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return userRepository.findByEmail(email)
                .onItem().ifNull().switchTo(() -> {
                    // If user is not found, create a new one
                    User newUser = new User(email);
                    System.out.println(newUser.toString());
                    return Panache.withTransaction(() -> userRepository.persist(newUser))
                            .replaceWith(newUser);

                });
    }
}

