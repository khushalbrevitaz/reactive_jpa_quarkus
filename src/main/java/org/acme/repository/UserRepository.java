package org.acme.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entity.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public Uni<User> findByEmail(String email) {
        return find("email", email).firstResult();
    }
}