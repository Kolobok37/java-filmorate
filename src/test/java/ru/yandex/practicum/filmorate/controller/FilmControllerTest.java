package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private static FilmController filmController;

    @BeforeAll
    static void createUserController() {
        filmController = new FilmController();
    }

    @AfterEach
    void removeAllFilms() {
        filmController.removeAll();
    }

    @Test
    void createFilm_checkNameIsBlank() {
        Film filmTest = new Film(1, "", "description1", LocalDate.of(2000, 01, 01), 2500);
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmTest));
    }

    @Test
    void createFilm_checkDescriptionLengthIsNotOk() {
        Film filmTest = new Film(1, "film1", "1234567890123456789012345678901234567890123456789012" +
                "3456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012" +
                "3456789012345678901234567890123456789012345678901",
                LocalDate.of(2000, 01, 01), 2500);
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmTest));
    }

    @Test
    void createFilm_checkDescriptionLengthIs200() {
        Film filmTest = new Film(1, "film1", "1234567890123456789012345678901234567890123456789012" +
                "3456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012" +
                "345678901234567890123456789012345678901234567890",
                LocalDate.of(2000, 01, 01), 2500);
        filmController.createFilm(filmTest);
        assertEquals(filmController.getFilms().get(0).getDescription().length(), 200);
    }

    @Test
    void createFilm_checkReleaseDateIsEarly() {
        Film filmTest = new Film(1, "film1", "description1", LocalDate.of(1895, 12, 27), 2500);
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmTest));
    }

    @Test
    void createFilm_checkReleaseDateIsNormal() {
        Film filmTest = new Film(1, "film1", "description1", LocalDate.of(1895, 12, 28), 2500);
        filmController.createFilm(filmTest);
        assertEquals(filmController.getFilms().get(0).getReleaseDate(), LocalDate.of(1895, 12, 28));
    }

    @Test
    void createFilm_checkDurationIsNegative() {
        Film filmTest = new Film(1, "film1", "description1", LocalDate.of(2000, 01, 01), -100);
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmTest));
    }
}