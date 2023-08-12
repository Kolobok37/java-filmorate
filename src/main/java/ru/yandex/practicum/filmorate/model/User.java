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
    private final Set<Integer> friendshipRequest = new HashSet<>();

    private final Set<Integer> filmsLikesId = new HashSet<>();

    public User(Integer userId, String email, String login, String name, LocalDate birthday) {
        this.id = userId;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void requestingFriendship(int idFriend) {
        friendsId.add(idFriend);
    }

    public void addFriends(int idFriend) {
        friendsId.add(idFriend);
    }

    public boolean checkRequestFriendship(int idFriend){
        if (friendshipRequest.contains(idFriend)) {
            return true;
        }
        else return false;
    }

    public boolean checkFriendship(int idFriend){
        if (friendsId.contains(idFriend)) {
            return true;
        }
        else return false;
    }

    public void deleteFriends(int idFriend) {
        friendsId.remove(idFriend);
    }
}
