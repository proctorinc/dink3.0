package com.dink3.user;

import com.dink3.jooq.tables.daos.UserDao;
import com.dink3.jooq.tables.pojos.User;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final UserDao usersDao;

    public UserRepository(Configuration configuration) {
        this.usersDao = new UserDao(configuration);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersDao.findById(id));
    }

    public Optional<User> findByEmail(String email) {
        return usersDao.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void save(User user) {
        usersDao.insert(user);
    }
} 