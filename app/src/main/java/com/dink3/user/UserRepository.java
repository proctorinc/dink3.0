package com.dink3.user;

import com.dink3.jooq.tables.daos.UsersDao;
import com.dink3.jooq.tables.pojos.Users;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final UsersDao usersDao;

    public UserRepository(Configuration configuration) {
        this.usersDao = new UsersDao(configuration);
    }

    public Optional<Users> findById(Integer id) {
        return Optional.ofNullable(usersDao.findById(id));
    }

    public Optional<Users> findByEmail(String email) {
        return usersDao.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void save(Users user) {
        usersDao.insert(user);
    }
} 