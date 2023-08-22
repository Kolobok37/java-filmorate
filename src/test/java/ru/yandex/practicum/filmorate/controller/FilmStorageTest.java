package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final FilmDBStorage filmStorage;

    private final UserDbStorage userStorage;

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void checkCreateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int id = filmStorage.getAll().stream().findFirst().get().getId();
        assertTrue("name1".equals(filmStorage.searchById(id).getName()));
    }

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void checkUpdateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int id = filmStorage.getAll().stream().findFirst().get().getId();
        Film filmTest2 = new Film(id, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.update(filmTest2);
        assertTrue("name2".equals(filmStorage.searchById(id).getName()));
    }


    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void CheckPopularFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int id1 = filmStorage.getAll().stream().findFirst().get().getId();
        int id2 = ++id1;
        int id3 = ++id2;
        Film filmTest2 = new Film(id2, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 2, mpa);
        filmStorage.create(filmTest2);
        Film filmTest3 = new Film(id3, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 3, mpa);
        filmStorage.create(filmTest3);
        System.out.println(filmStorage.getAll());
        assertEquals(id3, filmStorage.getPopularFilm(1).stream().findFirst().get().getId());
    }

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void getAll() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        Film filmTest2 = new Film(2, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 2, mpa);
        filmStorage.create(filmTest2);
        Film filmTest3 = new Film(3, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 3, mpa);
        filmStorage.create(filmTest3);
        assertEquals(3, filmStorage.getAll().size());
    }

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void ckecksearchById() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int id1 = filmStorage.getAll().stream().findFirst().get().getId();
        System.out.println(id1);
        int id2 = ++id1;
        Film filmTest2 = new Film(id2, "name2", "description2", LocalDate.of(2000, 01, 01), 100, 2, mpa);
        filmStorage.create(filmTest2);
        assertEquals(id1, filmStorage.searchById(id1).getId());
    }

    @Test
    void checkGetGenres() {
        assertEquals(6, filmStorage.getGenres().size());
    }

    @Test
    void checkGetAllMpa() {
        assertEquals(5, filmStorage.getAllMpa().size());
    }

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void checkAddLike() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int idFilm = filmStorage.getAll().stream().findFirst().get().getId();
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int idUser = userStorage.getAll().stream().findFirst().get().getId();
        filmStorage.addLike(idFilm, idUser);
        assertTrue(idUser == filmStorage.searchById(idFilm).getUserIdLikedFilm().stream().findFirst().get());
        assertTrue(idFilm == userStorage.searchById(idUser).getFilmsLikesId().stream().findFirst().get());
    }

    @Sql(statements = {
            "DELETE FROM like_films;",
            "DELETE FROM films;",
            "DELETE FROM film_genre;",
    })
    @Test
    void deleteLike() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Film filmTest = new Film(1, "name1", "description1", LocalDate.of(2000, 01, 01), 100, 1, mpa);
        filmStorage.create(filmTest);
        int idFilm = filmStorage.getAll().stream().findFirst().get().getId();
        User userTest = new User(1, "alex@yandex.ru", "login1", "name1", LocalDate.of(2000, 01, 01));
        userStorage.create(userTest);
        int idUser = userStorage.getAll().stream().findFirst().get().getId();
        filmStorage.addLike(idFilm, idUser);
        filmStorage.deleteLike(idFilm, idUser);
        assertTrue(filmStorage.searchById(idFilm).getUserIdLikedFilm().stream().findFirst().isEmpty());
        assertTrue(userStorage.searchById(idUser).getFilmsLikesId().stream().findFirst().isEmpty());
    }
}
