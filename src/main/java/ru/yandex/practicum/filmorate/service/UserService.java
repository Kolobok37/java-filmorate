package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final Storage<User> userStorage;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public void addFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.searchById(userId).addFriends(friendId);
        userStorage.searchById(friendId).addFriends(userId);
        log.info("Пользователь {} добавил пользователя {} в друзья", userId, friendId);

    }

    public void deleteFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        userStorage.searchById(userId).deleteFriends(friendId);
        userStorage.searchById(friendId).deleteFriends(userId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        checkExistenceUser(userId);
        checkExistenceUser(friendId);
        return userStorage.searchById(userId).getFriendsId().stream()
                .filter(userStorage.searchById(friendId).getFriendsId()::contains)
                .map(id -> userStorage.searchById(id))
                .collect(Collectors.toList());
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

    public List<User> getFriends(int userId) {
        checkExistenceUser(userId);
        return userStorage.searchById(userId).getFriendsId().stream()
                .map(id -> userStorage.searchById(id))
                .collect(Collectors.toList());
    }

    private void checkExistenceUser(int id) {
        if (!userStorage.checkExistence(id)) {
            log.warn("Ошибка: пользователь {} не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }
}
