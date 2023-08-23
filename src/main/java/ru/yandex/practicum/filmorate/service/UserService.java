package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.StorageUser;

import java.util.List;

@Service
public class UserService {

    @Autowired
    @Qualifier("userDbStorage")
    private StorageUser userStorage;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public void addFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.addFriends(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        checkExistenceUser(userId);
        return userStorage.getFriends(userId);
    }

    public void deleteFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.deleteFriends(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        checkExistenceUser(userId);
        checkExistenceUser(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUser(int userId) {
        checkExistenceUser(userId);
        return userStorage.searchById(userId);
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        checkExistenceUser(user.getId());
        return userStorage.update(user);
    }

    private void checkExistenceUser(int id) {
        if (!userStorage.checkExistence(id)) {
            log.warn("Ошибка: пользователь {} не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}
