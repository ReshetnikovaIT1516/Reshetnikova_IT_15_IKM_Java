package com.cinema.management.repository;

import com.cinema.management.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Репозиторий для работы с сущностью {@link Movie} (фильмы).
 * <p>
 * Этот интерфейс предоставляет доступ к операциям с базой данных для сущности Movie.
 * Включает как методы, сгенерированные Spring Data JPA на основе соглашений об именовании,
 * так и кастомные JPQL-запросы для сложных операций поиска.
 * </p>
 * <p>
 * Репозиторий поддерживает различные сценарии поиска фильмов: по жанру,
 * по части названия (регистронезависимый поиск) и по году выпуска.
 * </p>
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    /**
     * Находит все фильмы, отсортированные по названию в алфавитном порядке (A-Z).
     * <p>
     * Spring Data JPA автоматически генерирует запрос на основе имени метода:
     * <pre>
     * SELECT * FROM movies ORDER BY title ASC
     * </pre>
     * Этот метод используется для отображения списка всех фильмов в пользовательском интерфейсе.
     * </p>
     *
     * @return список всех фильмов, отсортированных по названию
     */
    List<Movie> findAllByOrderByTitleAsc();
    /**
     * Находит все фильмы определенного жанра, отсортированные по названию.
     * <p>
     * Spring Data JPA автоматически генерирует запрос на основе имени метода:
     * <pre>
     * SELECT * FROM movies WHERE genre_id = ? ORDER BY title ASC
     * </pre>
     * Метод использует имя поля сущности (genre) и его идентификатор (id) для построения запроса.
     * </p>
     *
     * @param genreId идентификатор жанра для фильтрации
     * @return список фильмов указанного жанра, отсортированных по названию
     */
    List<Movie> findByGenreIdOrderByTitleAsc(Long genreId);
    /**
     * Выполняет регистронезависимый поиск фильмов по части названия.
     * <p>
     * Использует кастомный JPQL-запрос для реализации сложной логики поиска:
     * - Поиск по части строки (LIKE с % с обеих сторон)
     * - Регистронезависимость (LOWER для приведения к нижнему регистру)
     * - Сортировка результатов по названию
     * </p>
     * @param title часть названия фильма для поиска (не должно быть {@code null})
     * @return список фильмов, содержащих указанную часть в названии
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY m.title")
    List<Movie> searchByTitle(@Param("title") String title);
    /**
     * Находит все фильмы, выпущенные в указанном году.
     * <p>
     * Использует кастомный JPQL-запрос для точного поиска по году выпуска.
     * Запрос включает сортировку по названию для удобного отображения результатов.
     * </p>
     * @param year год выпуска фильма для поиска
     * @return список фильмов, выпущенных в указанном году
     */
    @Query("SELECT m FROM Movie m WHERE m.releaseYear = :year ORDER BY m.title")
    List<Movie> findByReleaseYear(@Param("year") Integer year);
}