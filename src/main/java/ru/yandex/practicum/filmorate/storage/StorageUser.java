package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public interface StorageUser extends Storage<User>{
    Logger log = LoggerFactory.getLogger(UserController.class);

    void addFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    void deleteFriends(int userId, int friendId);

    List<User> getCommonFriends(int userId, int otherId);
    default boolean validationUserDate(User user) {
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

    default void addingUserDate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
