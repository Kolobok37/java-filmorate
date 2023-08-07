package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriends(Integer userId, Integer friendId) {
        userStorage.getUser(userId).addFriends(friendId);
        userStorage.getUser(friendId).addFriends(userId);
    }

    public void deleteFriends(Integer userId, Integer friendId) {
        userStorage.getUser(userId).deleteFriends(friendId);
        userStorage.getUser(friendId).deleteFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return userStorage.getUser(userId).getFriendsId().stream().
                filter(userStorage.getUser(userId).getFriendsId()::contains).
                map(id -> userStorage.getUser(id)).
                collect(Collectors.toList());
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getFriends(int userId) {
        return userStorage.getUser(userId).getFriendsId().stream().
                map(id->userStorage.getUser(id)).collect(Collectors.toList());
    }
}
