package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return userService.getUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUsers(@PathVariable int userId) {
        log.info("Запрошен пользователь {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping(value = "/users", consumes = {"application/json"})
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(value = "/users", consumes = {"application/json"})
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public User addFriends(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriends(userId, friendId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public User deleteFriends(@PathVariable int userId, @PathVariable int friendId) {
        userService.deleteFriends(userId, friendId);
        return userService.getUser(userId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        log.info("Запрошен список друзей пользователя {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Запрошен список общих друзей пользователей {} и {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }
}
