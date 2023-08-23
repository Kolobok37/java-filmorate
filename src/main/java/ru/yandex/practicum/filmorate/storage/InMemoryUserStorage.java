package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements StorageUser {
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (!validationUserDate(user) || checkExistence(user.getId())) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        addingUserDate(user);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User update(User user) {
        if (!validationUserDate(user)) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        addingUserDate(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь \"" + user.getLogin() + "\"");
        return user;
    }

    @Override
    public User searchById(int id) {
        return users.get(id);
    }

    @Override
    public void addingUserDate(User user) {
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


    @Override
    public void addFriends(int userId, int friendId) {
        if (searchById(friendId).getFriends().containsKey(userId)) {
            if (searchById(friendId).getFriends().get(userId) == StatusFriendship.unconfirmed) {
                if (searchById(userId).getFriends().get(friendId) == StatusFriendship.confirmed) {
                    searchById(friendId).addFriends(userId, StatusFriendship.confirmed);
                }
                log.info("Пользователь {} добавил пользователя {} в друзья.", userId, friendId);
            } else if (searchById(userId).getFriends().get(friendId) == StatusFriendship.unconfirmed) {
                log.info("Пользователь {} повторно отправил заявку {} в друзья.", userId, friendId);
                throw new ValidationException("Пользователь уже отправил заявку.");
            } else {
                log.info("Пользователь {} уже в друзьях у {}.", friendId, userId);
                throw new ValidationException("Пользователи уже друзья.");
            }
        } else {
            searchById(userId).addFriends(friendId, StatusFriendship.unconfirmed);
            searchById(friendId).addFriends(userId, StatusFriendship.confirmed);
            log.info("Пользователь {} отправил заявку в друзья {}.", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        return searchById(userId).getFriends().keySet().stream()
                .map(id -> searchById(id))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        searchById(userId).deleteFriends(friendId);
        searchById(friendId).deleteFriends(userId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return searchById(userId).getFriends().keySet().stream()
                .filter(searchById(otherId).getFriends().keySet()::contains)
                .map(id -> searchById(id))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkExistence(int id) {
        return users.containsKey(id);
    }
}
