package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDbStorage")
public class UserDbStorage implements StorageUser {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriends(int userId, int friendId) {
        User friend = searchById(friendId);
        User user = searchById(userId);
        if (friend.getFriends().containsKey(userId)) {
            if (friend.getFriends().get(userId) == StatusFriendship.confirmed) {
                log.info("Пользователь {} уже в друзьях у {}.", friendId, userId);
                throw new ValidationException("Пользователи уже друзья.");
            }
            if(friend.getFriends().get(userId) == StatusFriendship.unconfirmed){
                jdbcTemplate.update("insert into friendship(user_id,friend_id,friendship_status) values(?,?,?)", friendId, userId, 1);
                log.info("Пользователь {} добавил в друзья {}.", userId, friendId);
            }
        } else {
            jdbcTemplate.update("insert into friendship(user_id,friend_id,friendship_status) values(?,?,?)", userId, friendId, 1);
            jdbcTemplate.update("insert into friendship(user_id,friend_id,friendship_status) values(?,?,?)", friendId, userId, 2);
            log.info("Пользователь {} добавил в друзья {}.", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sqlQuery = "SELECT friend_id " +
                "FROM friendship " +
                "WHERE user_id = ? AND friendship_status = ?";
        List<Integer> friendsId = jdbcTemplate.query(sqlQuery, this::mapRowToFriendsId, userId, "1");
        List<User> friends = friendsId.stream().map(id->searchById(id)).collect(Collectors.toList());
        return friends;
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        String sqlQuery = "delete from friendship where user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        jdbcTemplate.update(sqlQuery, friendId,userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sqlQuery = "SELECT one.friend_id " +
                "FROM friendship AS one " +
                "JOIN (SELECT * " +
                "FROM friendship AS two " +
                "WHERE user_id = ? AND two.friendship_status = 1) " +
                "AS twos ON one.friend_id = twos.friend_id " +
                "WHERE one.user_id = ? AND one.friendship_status = 1;";
        List<Integer> friendsId = jdbcTemplate.query(sqlQuery, this::mapRowToFriendsId, userId, otherId);
        List<User> friends = friendsId.stream().map(id->searchById(id)).collect(Collectors.toList());
        return friends;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select user_id, name, login, email,birthday " +
                "from users;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User searchById(int id) {
        String sqlQuery = "select user_id, name, login, email,birthday " +
                "from users where user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public User create(User user) {
        if (!validationUserDate(user)) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        addingUserDate(user);
        jdbcTemplate.update("insert into users(email,login,name,birthday) values (?,?,?,?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        for (Integer filmId : user.getFilmsLikesId()) {
            jdbcTemplate.update("insert into like_films(user_id,film_id) values(?,?)", user.getId(), filmId);
        }
        user.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class));
        user.getFriends().keySet().stream()
                .forEach(id ->
                        jdbcTemplate.update("insert into friendShip(user_id,friend_id,friendship_status) values(?,?,?)",
                                user.getId(), id, user.getFriends().get(id)));
        return user;
    }

    @Override
    public User update(User user) {
        if (!validationUserDate(user)) {
            throw new ValidationException("Ошибка ввода данных пользователя");
        }
        addingUserDate(user);
        jdbcTemplate.update("update users set email = ?, login = ?, name = ?, birthday = ?" +
                "where user_id = ?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        for (Integer filmId : user.getFilmsLikesId()) {
            jdbcTemplate.update("insert into like_films(user_id,film_id) values(?,?)", user.getId(), filmId);
        }
        user.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class));
        user.getFriends().keySet().stream()
                .forEach(id ->
                        jdbcTemplate.update("insert into friendShip(user_id,friend_id,friendship_status) values(?,?,?)",
                                user.getId(), id, user.getFriends().get(id)));
        return user;
    }

    @Override
    public boolean checkExistence(int id) {
        String sqlQuery = "select count(user_id) " +
                "from users where user_id = ?";
        int sing = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (sing == 1) {
            return true;
        } else {
            return false;
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        String sqlQueryLikesFilm = "select film_id " +
                "from like_films where user_id = ?";
        user.getFilmsLikesId().addAll(jdbcTemplate.query(sqlQueryLikesFilm, this::mapRowToLikes, user.getId()));
        String sqlQueryFriends = "SELECT  f.friend_id, fs.friendship_status " +
                "FROM friendship AS f " +
                "LEFT JOIN friendship_status AS fs ON f.friendship_status =fs.friendship_status_ID " +
                "WHERE f.user_id=?";
        List<Friend> friends = jdbcTemplate.query(sqlQueryFriends, this::mapRowToFriends, user.getId());
        for (Friend friend : friends) {
            user.getFriends().put(friend.getId(), friend.getStatusFriendship());
        }
        return user;
    }

    private Integer mapRowToLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("film_id");
    }

    private Integer mapRowToFriendsId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("friend_id");
    }

    private Friend mapRowToFriends(ResultSet resultSet, int rowNum) throws SQLException {
        Friend friend = Friend.builder()
                .id(resultSet.getInt("friend_id"))
                .statusFriendship(StringToFriendshipStatus(resultSet.getString("friendship_status")))
                .build();
        return friend;
    }

    private StatusFriendship StringToFriendshipStatus(String status) {
        switch (status) {
            case "confirmed":
                return StatusFriendship.confirmed;
            case "unconfirmed":
                return StatusFriendship.unconfirmed;
        }
        return null;
    }
}