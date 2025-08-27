package ru.yandex.practicum.filmorate.repository.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.*;

@Slf4j
@Component("filmDb")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_LIKES_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String REMOVE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_DIRECTORS_QUERY = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
    private static final String REMOVE_DIRECTORS_QUERY = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String IS_EXIST_QUERY = "SELECT COUNT(*) FROM films WHERE film_id = ?";
    private static final String FILM_GENRES_QUERY = "SELECT * FROM film_genres AS fg JOIN genres AS g " +
            "ON fg.genre_id = g.genre_id  WHERE film_id = ?";
    private static final String DISLIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String MPA_QUERY = "SELECT m.mpa_id, m.name FROM films AS f JOIN mpa AS m ON " +
            "f.mpa_id = m.mpa_id WHERE film_id = ?";
    private static final String TOP_QUERY = "SELECT f.*, COUNT(l.user_id) FROM films AS f LEFT JOIN likes AS l ON " +
            "f.film_id = l.film_id GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String GET_LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String DIRECTOR_FILMS_LIKES_QUERY = "SELECT f.* FROM films AS f " +
            "JOIN film_directors AS fd ON fd.film_id = f.film_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "WHERE fd.director_id = ? GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";
    private static final String DIRECTOR_FILMS_YEAR_QUERY = "SELECT f.* FROM films AS f " +
            "JOIN film_directors AS fd ON fd.film_id = f.film_id " +
            "WHERE fd.director_id = ? ORDER BY EXTRACT(YEAR FROM f.release_date) ASC";
    private static final String FILM_DIRECTORS_QUERY = "SELECT d.* FROM film_directors AS fd JOIN directors AS d " +
            "ON fd.director_id = d.director_id WHERE fd.film_id = ?";

    private static final String FIND_RECOMMENDATION_FILMS = "SELECT f.*, COUNT(l_all.user_id) " +
            "FROM films AS f " +
            "JOIN likes AS l ON l.film_id = f.film_id " +
            "LEFT JOIN likes AS l_all ON l_all.film_id = f.film_id " +
            "WHERE l.user_id = ? AND f.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?) " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l_all.user_id) DESC " +
            "LIMIT 5;";
    private static final String FILMS_SEARCH_BY_TITLE = "SELECT f.* FROM films AS f " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "WHERE LOWER(f.name) LIKE LOWER('%' || ? || '%') GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";
    private static final String FILMS_SEARCH_BY_DIRECTOR = "SELECT f.* FROM films AS f " +
            "JOIN film_directors AS fd ON fd.film_id = f.film_id " +
            "LEFT JOIN directors AS d ON d.director_id = fd.director_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "WHERE LOWER(d.name) LIKE LOWER('%' || ? || '%') " +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";
    private static final String FILMS_SEARCH_BY_DIRECTOR_OR_TITLE = "SELECT f.* FROM films AS f " +
            "LEFT JOIN film_directors AS fd ON fd.film_id = f.film_id " +
            "LEFT JOIN directors AS d ON d.director_id = fd.director_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "WHERE LOWER(f.name) LIKE LOWER('%' || ? || '%') " +
            "OR LOWER(d.name) LIKE LOWER('%' || ? || '%') " +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";

    private static final String TOP_BY_GENRE_AND_YEAR = """
            SELECT f.*, COUNT(l.user_id) as likes_count\s
            FROM films AS f\s
            LEFT JOIN likes AS l ON f.film_id = l.film_id
            JOIN film_genres AS fg ON f.film_id = fg.film_id
            WHERE fg.genre_id = ? AND YEAR(f.release_date) = ?
            GROUP BY f.film_id\s
            ORDER BY COUNT(l.user_id) DESC\s
            LIMIT ?
            \s""";

    private static final String TOP_BY_GENRE = """
    SELECT f.*, COUNT(l.user_id) as likes_count\s
    FROM films AS f\s
    LEFT JOIN likes AS l ON f.film_id = l.film_id
    JOIN film_genres AS fg ON f.film_id = fg.film_id
    WHERE fg.genre_id = ?
    GROUP BY f.film_id\s
    ORDER BY COUNT(l.user_id) DESC\s
    LIMIT ?
   \s""";

    private static final String TOP_BY_YEAR = """
    SELECT f.*, COUNT(l.user_id) as likes_count\s
    FROM films AS f\s
    LEFT JOIN likes AS l ON f.film_id = l.film_id
    WHERE YEAR(f.release_date) = ?
    GROUP BY f.film_id\s
    ORDER BY COUNT(l.user_id) DESC\s
    LIMIT ?
   \s""";

    private static final String TOP_ALL = """
    SELECT f.*, COUNT(l.user_id) as likes_count\s
    FROM films AS f\s
    LEFT JOIN likes AS l ON f.film_id = l.film_id
    GROUP BY f.film_id\s
    ORDER BY COUNT(l.user_id) DESC\s
    LIMIT ?
   \s""";

    private static final String COMMON_FILMS_QUERY =
            "SELECT f.*, COUNT(l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN likes l1 ON f.film_id = l1.film_id AND l1.user_id = ? " +
                    "JOIN likes l2 ON f.film_id = l2.film_id AND l2.user_id = ? " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY likes_count DESC";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film add(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);
        Mpa mpa = film.getMpa();
        if (mpa != null) {
            jdbc.update("UPDATE films SET mpa_id = ? WHERE film_id = ?", mpa.getId(), id);
        }
        List<Long> genres = film.getGenres().stream()
                .map(Genre::getId)
                .toList();
        addMany(INSERT_GENRES_QUERY, id, genres);
        List<Long> directors = film.getDirectors().stream()
                .map(Director::getId)
                .toList();
        addMany(INSERT_DIRECTORS_QUERY, id, directors);

        return film;
    }

    @Override
    public void update(Film film) {
        long id = film.getId();
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id
        );

        delete(REMOVE_GENRES_QUERY, id);
        addMany(INSERT_GENRES_QUERY, id, film.getGenres().stream().map(Genre::getId).toList());

        delete(REMOVE_DIRECTORS_QUERY, id);
        addMany(INSERT_DIRECTORS_QUERY, id, film.getDirectors().stream().map(Director::getId).toList());
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public Film get(long id) {
        Optional<Film> optionalFilm = findOne(FIND_BY_ID_QUERY, id);
        if (optionalFilm.isEmpty()) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм " + id + " не найден");
        }
        Film film = optionalFilm.get();
        setParameters(film);

        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film : films) {
            setParameters(film);
        }
        return films;
    }

    @Override
    public boolean exists(long id) {
        long count = jdbc.queryForObject(IS_EXIST_QUERY, Long.class, id);
        return count > 0;
    }

    @Override
    public void like(long filmId, long userId) {
        jdbc.update(INSERT_LIKES_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(DISLIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(long size) {
        List<Film> top = findMany(TOP_QUERY, size);
        for (Film film : top) {
            setParameters(film);
        }
        return top;
    }

    @Override
    public List<Film> getTopFilmsByGenreAndYear(long limit, long genreId, int year) {
        List<Film> films = findMany(TOP_BY_GENRE_AND_YEAR, genreId, year, limit);
        for (Film film : films) {
            setParameters(film);
        }
        return films;
    }

    @Override
    public List<Film> getTopFilmsByGenre(long limit, long genreId) {
        List<Film> films = findMany(TOP_BY_GENRE, genreId, limit);
        for (Film film : films) {
            setParameters(film);
        }
        return films;
    }

    @Override
    public List<Film> getTopFilmsByYear(long limit, int year) {
        List<Film> films = findMany(TOP_BY_YEAR, year, limit);
        for (Film film : films) {
            setParameters(film);
        }
        return films;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByLikes(long directorId) {
        List<Film> directorFilmsSortedByLikes = findMany(DIRECTOR_FILMS_LIKES_QUERY, directorId);
        for (Film film : directorFilmsSortedByLikes) {
            setParameters(film);
        }
        return directorFilmsSortedByLikes;
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(long directorId) {
        List<Film> directorFilmsSortedByYear = findMany(DIRECTOR_FILMS_YEAR_QUERY, directorId);
        for (Film film : directorFilmsSortedByYear) {
            setParameters(film);
        }
        return directorFilmsSortedByYear;
    }

    @Override
    public List<Film> getRecommendationFilms(List<Long> usersIdWithSimilarLikes, Long userId) {
        Set<Film> recommendationFilms = new LinkedHashSet<>();
        for (Long userIdWithSimilarLikes : usersIdWithSimilarLikes) {
            List<Film> films = findMany(FIND_RECOMMENDATION_FILMS, userIdWithSimilarLikes, userId);
            for (Film film : films) {
                setParameters(film);
                recommendationFilms.add(film);
                if (recommendationFilms.size() > 4) {
                    return recommendationFilms.stream().toList();
                }
            }
        }
        return recommendationFilms.stream().toList();
    }

    @Override
    public List<Film> getFilmsSearchByTitle(String query) {
        List<Film> filmsSearchByTitle = findMany(FILMS_SEARCH_BY_TITLE, query);
        for (Film film : filmsSearchByTitle) {
            setParameters(film);
        }
        return filmsSearchByTitle;
    }

    @Override
    public List<Film> getFilmsSearchByDirector(String query) {
        List<Film> filmsSearchByDirector = findMany(FILMS_SEARCH_BY_DIRECTOR, query);
//        List<Film> filmsSearchByDirector = findMany(FILMS_SEARCH_BY_DIRECTOR);
        for (Film film : filmsSearchByDirector) {
            setParameters(film);
        }
        return filmsSearchByDirector;
    }

    @Override
    public List<Film> getFilmsSearchByDirectorOrTitle(String query) {
        List<Film> filmsSearchByDirectorOrTitle = findMany(FILMS_SEARCH_BY_DIRECTOR_OR_TITLE, query, query);
        for (Film film : filmsSearchByDirectorOrTitle) {
            setParameters(film);
        }
        return filmsSearchByDirectorOrTitle;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> commonFilms = findMany(COMMON_FILMS_QUERY, userId, friendId);
        for (Film film : commonFilms) {
            setParameters(film);
        }
        return commonFilms;
    }

    private List<Genre> getFilmGenres(long id) {
        GenreRowMapper genreMapper = new GenreRowMapper();
        return jdbc.query(FILM_GENRES_QUERY, genreMapper, id);
    }

    private Mpa getMpa(long id) {
        MpaRowMapper mpaMapper = new MpaRowMapper();
        return jdbc.queryForObject(MPA_QUERY, mpaMapper, id);
    }

    private List<Director> getFilmDirectors(long id) {
        DirectorRowMapper directorMapper = new DirectorRowMapper();
        return jdbc.query(FILM_DIRECTORS_QUERY, directorMapper, id);
    }

    private void setParameters(Film film) {
        long id = film.getId();
        List<Long> likes = jdbc.queryForList(GET_LIKES_QUERY, Long.class, id);
        for (Long userId : likes) {
            film.like(userId);
        }

        List<Genre> genres = getFilmGenres(id);
        for (Genre genre : genres) {
            film.addGenre(genre);
        }

        Mpa rating = getMpa(id);
        film.setMpa(rating);

        List<Director> directors = getFilmDirectors(id);
        for (Director director : directors) {
            film.addDirector(director);
        }
    }
}
