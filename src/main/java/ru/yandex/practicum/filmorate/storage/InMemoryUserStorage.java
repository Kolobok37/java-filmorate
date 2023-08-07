package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) {
        addingUserDate(user);
        if (!validationUser(user)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
        }
        users.put(user.getUserId(), user);
        log.info("Добавлен пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User updateUser(User user) {
        addingUserDate(user);
        if (!users.containsKey(user.getUserId())) {
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "User update unknown");
        }
        if (!validationUser(user)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
        }
        users.put(user.getUserId(), user);
        log.info("Обновлён пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    private void addingUserDate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getUserId() == 0) {
            if (users.size() == 0) {
                user.setUserId(1);
            } else {
                int a = users.keySet().stream().max((id1, id2) -> id1 - id2).get();
                user.setUserId(++a);
            }
        }
    }

    private boolean validationUser(User user) {
        if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
            log.warn("Данные заполнены неполностью.");
            return false;
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Задан неверный e-mail.");
            return false;
        }
        if (user.getLogin().isBlank()) {
            log.warn("Задан пустой логин.");
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
        if (user.getUserId() == 0) {
            if (users.size() == 0) {
                user.setUserId(1);
            } else {
                int a = users.keySet().stream().max((id1, id2) -> id1 - id2).get();
                user.setUserId(++a);
            }
        }
        return true;
    }
}
