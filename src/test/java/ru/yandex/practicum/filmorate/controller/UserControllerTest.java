package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private static UserController userController;

    @BeforeAll
    static void createUserController() {
        userController = new UserController();
    }

    @Test
    void createUser_checkEmailIsBlank() {
        User userTest = new User(1, "", "login1", "name1", "01-01-2000");
        assertThrows(ValidationException.class, () -> userController.createUser(userTest));
    }

    @Test
    void createUser_checkLoginIsBlank() {
        User userTest = new User(1, "test1@yandex.ru", "", "name1", "01-01-2000");
        assertThrows(ValidationException.class, () -> userController.createUser(userTest));
    }

    @Test
    void createUser_checkEmailIsDontHasDogs() {
        User userTest = new User(1, "test1yandex.ru", "login1", "name1", "01-01-2000");
        assertThrows(ValidationException.class, () -> userController.createUser(userTest));
    }

    @Test
    void createUser_checkLoginHasSpace() {
        User userTest = new User(1, "test1@yandex.ru", "lo gin1", "name1", "01-01-2000");
        assertThrows(ValidationException.class, () -> userController.createUser(userTest));
    }

    @Test
    void createUser_checkBirthday() {
        User userTest = new User(1, "test1@yandex.ru", "login1", "name1", "01-01-2300");
        assertThrows(ValidationException.class, () -> userController.createUser(userTest));
    }

    @Test
    void createUser_checkNameIsNull() {
        User userTest = new User(1, "test1@yandex.ru", "login1", "", "01-01-2000");
        userController.createUser(userTest);
        assertEquals(userController.getUsers().get(0).getName(), "login1");
    }
}