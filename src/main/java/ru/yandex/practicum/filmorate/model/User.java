package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;
    private final Set<Integer> friendsId = new HashSet<>();
    private final Set<Integer> filmsLikesId = new HashSet<>();

    public User(Integer userId, String email, String login, String name, LocalDate birthday) {
        this.id = userId;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriends(int idFriends) {
        friendsId.add(idFriends);
    }

    public void deleteFriends(int idFriends) {
        friendsId.remove(idFriends);
    }
}
