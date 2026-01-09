package com.cinema.management.service;

import com.cinema.management.entity.Movie;
import com.cinema.management.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
/**
 * Сервис для управления операциями с фильмами в системе кинотеатра.
 * <p>
 * Этот сервис предоставляет бизнес-логику для работы с фильмами, включая
 * CRUD операции, поиск и фильтрацию фильмов по различным критериям.
 * Сервис инкапсулирует все операции, связанные с фильмами, и обеспечивает
 * согласованность данных.
 * </p>
 */
@Service
public class MovieService {
    /**
     * Репозиторий для доступа к данным о фильмах в базе данных.
     * <p>
     * Spring Data JPA автоматически создает реализацию этого интерфейса
     * на основе объявленных методов.
     * </p>
     */
    @Autowired
    private MovieRepository movieRepository;
    /**
     * Возвращает список всех фильмов, отсортированных по названию.
     * <p>
     * Метод используется для отображения полного каталога фильмов.
     * Сортировка по названию обеспечивает удобную навигацию для пользователей.
     * </p>
     *
     * @return список всех фильмов в алфавитном порядке
     * @apiNote Генерируемый SQL: SELECT * FROM movies ORDER BY title ASC
     * @example
     * <pre>
     *  Возвращает:
     *  1. "Аватар"
     *  2. "Интерстеллар"
     *  3. "Титаник"
     * </pre>
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAllByOrderByTitleAsc();
    }
    /**
     * Находит фильм по его уникальному идентификатору.
     * <p>
     * Возвращает {@link Optional} для безопасной обработки случаев,
     * когда фильм с указанным ID не найден в базе данных.
     * </p>
     *
     * @param id уникальный идентификатор фильма
     * @return {@link Optional} с найденным фильмом или пустой Optional
     * @throws IllegalArgumentException если id равен null
     * @apiNote Генерируемый SQL: SELECT * FROM movies WHERE id = ?
     */
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }
    /**
     * Сохраняет фильм в базе данных.
     * <p>
     * Метод выполняет как создание новых фильмов, так и обновление существующих.
     * Если переданный объект Movie имеет id = null, выполняется INSERT.
     * Если id != null, выполняется UPDATE существующей записи.
     * </p>
     *
     * @param movie объект фильма для сохранения
     * @return сохраненный фильм (с присвоенным id для новых записей)
     * @throws IllegalArgumentException если movie равен null
     * @apiNote JPA автоматически определяет нужную операцию (save = insert или update)
     */
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    /**
     * Удаляет фильм по его идентификатору.
     * <p>
     * При удалении фильма также автоматически удаляются все связанные билеты
     * благодаря каскадным операциям, определенным в сущности Movie.
     * </p>
     *
     * @param id идентификатор фильма для удаления
     * @throws IllegalArgumentException если id равен null
     * @apiNote SQL: DELETE FROM movies WHERE id = ?
     */
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
    /**
     * Выполняет поиск фильмов по названию с поддержкой частичного совпадения.
     * <p>
     * Поиск не зависит от регистра (case-insensitive) и находит фильмы,
     * в названии которых содержится указанная подстрока.
     * </p>
     *
     * @param title подстрока для поиска в названиях фильмов
     * @return список фильмов, отсортированных по названию
     * @throws IllegalArgumentException если title равен null
     * @apiNote SQL: SELECT * FROM movies WHERE LOWER(title) LIKE LOWER('%?%') ORDER BY title
     * @example
     * <pre>
     * При поиске "star" найдет:
     * - "Star Wars"
     * - "Stardust"
     * </pre>
     */
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.searchByTitle(title);
    }
    /**
     * Возвращает все фильмы указанного жанра.
     * <p>
     * Используется для фильтрации каталога по жанрам и отображения
     * фильмов конкретного жанра на странице жанра.
     * </p>
     *
     * @param genreId идентификатор жанра
     * @return список фильмов указанного жанра, отсортированных по названию
     * @throws IllegalArgumentException если genreId равен null
     * @apiNote SQL: SELECT * FROM movies WHERE genre_id = ? ORDER BY title ASC
     */
    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenreIdOrderByTitleAsc(genreId);
    }

}