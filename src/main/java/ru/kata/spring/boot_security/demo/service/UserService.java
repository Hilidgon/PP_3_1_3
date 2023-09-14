package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService {
    List<User> allUsers();
    void add(User user);
    void delete(long id);
    void edit(User user);
    User show(long id);

    User getUserByUsername(String username);
}
