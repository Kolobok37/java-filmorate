package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate birthday;
    private final HashMap<Integer, StatusFriendship> friends = new HashMap<>();

    private final Set<Integer> filmsLikesId = new HashSet<>();

    public User(Integer userId, String email, String login, String name, LocalDate birthday) {
        this.id = userId;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriends(int idFriend, StatusFriendship statusFriendship) {
        friends.put(idFriend,statusFriendship);
    }

    public void deleteFriends(int idFriend) {
        friends.remove(idFriend);
    }
}
