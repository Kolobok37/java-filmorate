package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDbStorage")
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select user_id, name, login, email,birthday ";
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
        jdbcTemplate.update("insert into users(email,login,name,birthday) values (?,?,?,?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        for (Integer filmId : user.getFilmsLikesId()) {
            jdbcTemplate.update("insert into like_films(user_id,film_id) values(?,?)", user.getId(), filmId);
        }
        Set<Integer> friendId = user.getFriends().keySet();
        friendId.stream().forEach(id ->
                jdbcTemplate.update("insert into friendShip(user_id,friend_id,friendship_status) values(?,?,?)",
                        user.getId(), id, user.getFriends().get(id)));

        return null;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("update users set email = ?, login = ?, name = ?, birthday = ?" +
                "where user_id = ?", user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return null;
    }

    @Override
    public boolean checkExistence(int idT) {
        return false;
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
        String sqlQueryFriends = "SELECT  friend_id, fs.friendship_status " +
                "FROM friendship AS f " +
                "LEFT OUTER JOIN friendship_status AS fs ON f.friendship_status =fs.friendship_status_ID " +
                "WHERE f.user_id=?";
        //jdbcTemplate.query(sqlQueryLikesFilm, this::mapRowToLikes,user.getId());
        List<Friend> friends = jdbcTemplate.query(sqlQueryFriends, this::mapRowToFriends, user.getId());
        for (Friend friend : friends) {
            user.getFriends().put(friend.getId(), friend.getStatusFriendship());
        }
        return user;
    }

    private Integer mapRowToLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("film_id");
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
            case "request":
                return StatusFriendship.request;
        }
        return null;
    }
}
