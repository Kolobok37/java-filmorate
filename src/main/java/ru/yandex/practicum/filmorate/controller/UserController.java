package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping(value = "/users", consumes = {"application/json"})
    public User createUser(@RequestBody User user) {
        if (!validationUser(user)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @PutMapping(value = "/users", consumes = {"application/json"})
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
        }
        if (!validationUser(user)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    private boolean validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().isBlank()) {
            log.warn("Задан пустой логин.");
            return false;
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Задан неверный e-mail.");
            return false;
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Задан логин содержащий пробел.");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения задана неверно");
            return false;
        }
        return true;
    }
}
