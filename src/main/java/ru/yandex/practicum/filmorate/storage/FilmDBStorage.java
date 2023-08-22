package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("filmDbStorage")
public class FilmDBStorage implements StorageFilm {
    private final JdbcTemplate jdbcTemplate;

    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        String sqlQuery = "SELECT film_id ,name, description, release_date,duration,rate, mpa " +
                "FROM films " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO like_films(film_id,user_id)" +
                "VALUES(?,?)", filmId, userId);
        int rate = searchById(filmId).getRate();
        rate++;
        jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", rate, filmId);

    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "delete from like_films where film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        int rate = searchById(filmId).getRate();
        rate--;
        jdbcTemplate.update("UPDATE films SET rate = ? WHERE film_id = ?", rate, filmId);
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT genre_id, genre " +
                "FROM genre ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT mpa_id, mpa " +
                "FROM mpa ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT film_id ,name, description, release_date,duration,rate, mpa " +
                "FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film searchById(int id) {
        String sqlQuery = "SELECT film_id, name, description, release_date,duration,rate, mpa " +
                "FROM films " +
                "where film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public Film create(Film film) {
        if (!validationFilm(film)) {
            throw new ValidationException("Ошабка ввода данных фильма");
        }
        jdbcTemplate.update("insert into films(name,description,release_date,duration,rate,mpa) values (?,?,?,?,?,?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId());
        film.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class));
        film.getUserIdLikedFilm().stream().forEach(id ->
                jdbcTemplate.update("INSERT INTO like_films(user_id,film_id) VALUES(?,?)",
                        id, film.getId()));
        if (film.getGenres() != null) {
            film.getGenres().stream().forEach(genre ->
                    jdbcTemplate.update("INSERT INTO film_genre(film_id,genre_id) VALUES(?,?)",
                            film.getId(), genre.getId()));
        }

        log.info("Добавлен фильм " + film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("update films set  name = ?, description = ?, release_date = ?," +
                        " duration = ?,rate=?, mpa = ? " +
                        "where film_id = ?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        film.getUserIdLikedFilm().stream().forEach(id ->
                jdbcTemplate.update("REPLACE INTO like_films(user_id,film_id) VALUES(?,?)",
                        id, film.getId()));
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream().forEach(genre ->
                    jdbcTemplate.update("REPLACE INTO film_genre(film_id,genre_id) VALUES(?,?)",
                            film.getId(), genre.getId()));
        }
        log.info("Обновлён фильм \"" + film.getName() + "\"");
        return film;
    }

    @Override
    public boolean checkExistence(int id) {
        String sqlQuery = "select count(film_id) " +
                "from films where film_id = ?";
        int sing = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (sing == 1) {
            return true;
        } else {
            return false;
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa"));
        Film film = new Film(resultSet.getInt("film_id"), resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(), resultSet.getInt("duration"),
                resultSet.getInt("rate"), mpa);
        String sqlQueryUserLikes = "SELECT user_id " +
                "from like_films where film_id = ?";
        film.getUserIdLikedFilm().addAll(jdbcTemplate.query(sqlQueryUserLikes, this::mapRowToLikes, film.getId()));
        String sqlQueryGenre = "SELECT genre_id " +
                "FROM film_genre " +
                "WHERE film_id=?";
        film.getGenres().addAll(jdbcTemplate.query(sqlQueryGenre, this::mapRowToGenre, film.getId()));
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        return genre;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        return mpa;
    }

    private Integer mapRowToLikes(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getInt("user_id");
    }


}
