package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTest {
    private final UserDbStorage userStorage;

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void searchById() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id1 = userStorage.getAll().stream().findFirst().get().getId();
        assertTrue(id1 == (userStorage.searchById(id1).getId()));
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkGetFriends() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id1 = userStorage.getAll().stream().findFirst().get().getId();
        int id2 = id1++;
        int id3 = id2++;
        User userTest2 = new User(2, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest2);
        User userTest3 = new User(3, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest3);
        System.out.println(userStorage.getAll());
        userStorage.addFriends(id1, id2);
        userStorage.addFriends(id1, id3);
        assertTrue((userStorage.searchById(id1).getFriends().size() == 2));
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkDeleteFriends() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id1 = userStorage.getAll().stream().findFirst().get().getId();
        int id2 = id1++;
        User userTest2 = new User(2, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest2);
        System.out.println(userStorage.getAll());
        userStorage.addFriends(id1, id2);
        userStorage.deleteFriends(id1, id2);
        assertTrue((userStorage.searchById(id1).getFriends().isEmpty()));
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkCommonFriends() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id1 = userStorage.getAll().stream().findFirst().get().getId();
        int id2 = id1++;
        int id3 = id2++;
        User userTest2 = new User(2, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest2);
        User userTest3 = new User(3, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest3);
        System.out.println(userStorage.getAll());
        userStorage.addFriends(id1, id3);
        userStorage.addFriends(id2, id3);
        assertTrue((userStorage.getCommonFriends(id1, id2).get(0).getId() == id3));
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkAddFriends() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id1 = userStorage.getAll().stream().findFirst().get().getId();
        int id2 = id1++;
        User userTest2 = new User(2, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest2);
        System.out.println(userStorage.getAll());
        userStorage.addFriends(id1, id2);
        assertTrue((userStorage.searchById(id1).getFriends().containsKey(id2)));
    }

    @Test
    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    void getAll() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        User userTest2 = new User(2, "alex@yandex.ru2", "login2", "name2", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest2);
        assertEquals(2, userStorage.getAll().size());
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkCreateUser() {
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id = userStorage.getAll().stream().findFirst().get().getId();
        assertTrue(id == (userStorage.searchById(id).getId()));
    }

    @Sql(statements = {
            "DELETE FROM friendship;",
            "DELETE FROM users;",
    })
    @Test
    void checkUpdateUser() {
        User userTest = new User(1, "alex@yandex.ru", "login11", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int id = userStorage.getAll().stream().findFirst().get().getId();
        User userTest2 = new User(1, "alex@yandex.ru", "login2", "name1", LocalDate.of(2000, 01, 01));
        userTest2.setId(id);
        userStorage.update(userTest2);
        assertTrue("login2".equals(userStorage.searchById(id).getLogin()));
    }
}