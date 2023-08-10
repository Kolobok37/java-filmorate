package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements Storage<User> {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        addingUserDate(user);
        if (!validationUserDate(user) || !validationExistenceUser(user)) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User update(User user) {
        addingUserDate(user);
        if (!validationUserDate(user)) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User searchById(int id) {
        return users.get(id);
    }

    private void addingUserDate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == null || user.getId() == 0) {
            if (users.size() == 0) {
                user.setId(1);
            } else {
                int a = users.keySet().stream().max((id1, id2) -> id1 - id2).get();
                user.setId(++a);
            }
        }
    }

    private boolean validationUserDate(User user) {
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
        return true;
    }

    private boolean validationExistenceUser(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Такой пользователь уже существует");
            return false;
        }
        return true;
    }

    public boolean checkExistence(int id) {
        return users.containsKey(id);
    }
}
