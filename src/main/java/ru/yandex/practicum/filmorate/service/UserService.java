package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.getUser(userId).addFriends(friendId);
        userStorage.getUser(friendId).addFriends(userId);
        log.info("Пользователь % добавил пользователя %d в друзья", userId, friendId);

    }

    public void deleteFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.getUser(userId).deleteFriends(friendId);
        userStorage.getUser(friendId).deleteFriends(userId);
        log.info("Пользователь % удалил пользователя %d из друзей", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        return userStorage.getUser(userId).getFriendsId().stream().
                filter(userStorage.getUser(friendId).getFriendsId()::contains).map(id -> userStorage.getUser(id)).
                collect(Collectors.toList());
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int userId) {
        checkExistenceUser(userId);
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        checkExistenceUser(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getFriends(int userId) {
        checkExistenceUser(userId);
        return userStorage.getUser(userId).getFriendsId().stream().
                map(id -> userStorage.getUser(id)).collect(Collectors.toList());
    }

    private void checkExistenceUser(int id) {
        if (!userStorage.checkExistenceUser(id)) {
            log.warn("Ошибка: пользователь %d не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}
