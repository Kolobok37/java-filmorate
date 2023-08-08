package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Map;

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
        log.info("Запрошен пользователь %d", userId);
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
        log.info("Запрошен список друзей пользователя %d", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Запрошен список общих друзей пользователей %d и %d", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final ValidationException e) {
        return Map.of("error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final NotFoundException e) {
        return Map.of("error: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handle(final Exception e) {
        return Map.of("error: ", e.getMessage());
    }
}
