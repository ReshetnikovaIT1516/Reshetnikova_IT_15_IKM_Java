package com.cinema.management.repository;

import com.cinema.management.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 * Репозиторий для работы с сущностью {@link Genre} (жанры фильмов).
 * <p>
 * Этот интерфейс предоставляет доступ к операциям с базой данных для сущности Genre.
 * Расширяет {@link JpaRepository}, что автоматически предоставляет стандартные
 * CRUD-операции (Create, Read, Update, Delete) без необходимости их реализации.
 * </p>
 * <p>
 * Spring Data JPA автоматически генерирует реализации методов на основе их имен
 * в соответствии с соглашениями об именовании. Это позволяет писать сложные запросы
 * к базе данных без написания SQL или JPQL вручную.
 * </p>
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Находит жанр по точному совпадению названия.
     * <p>
     * Метод возвращает {@link Optional}, который содержит жанр, если он найден,
     * или пустой Optional, если жанр с таким названием не существует.
     * </p>
     * <p>
     * Spring Data JPA автоматически генерирует SQL-запрос на основе имени метода:
     * <pre>
     * SELECT * FROM genres WHERE title = ?
     * </pre>
     * </p>
     * @return {@link Optional}, содержащий найденный жанр или пустой, если не найден
     */
    List<Genre> findAllByOrderByTitleAsc();
    /**
     * Проверяет, существует ли жанр с указанным названием.
     * <p>
     * Метод возвращает {@code true}, если жанр с таким названием существует,
     * и {@code false} в противном случае.
     * </p>
     * <p>
     * Spring Data JPA генерирует оптимизированный запрос:
     * <pre>
     * SELECT COUNT(*) > 0 FROM genres WHERE title = ?
     * </pre>
     * Этот запрос более эффективен, чем получение полной записи, так как
     * проверяет только существование.
     * </p>
     *
     * @param title название жанра для проверки (не должно быть {@code null})
     * @return {@code true} если жанр существует, {@code false} в противном случае
     */
    boolean existsByTitle(String title);
}