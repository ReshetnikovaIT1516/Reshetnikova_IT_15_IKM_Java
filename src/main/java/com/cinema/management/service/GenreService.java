package com.cinema.management.service;

import com.cinema.management.entity.Genre;
import com.cinema.management.repository.GenreRepository;
import com.cinema.management.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 * Сервис для управления операциями с жанрами фильмов.
 * <p>
 * Этот класс предоставляет бизнес-логику для работы с жанрами, включая
 * создание, чтение, обновление и удаление (CRUD) жанров.
 * Он действует как промежуточный слой между контроллерами и репозиториями.
 * </p>
 * <p>
 * <strong>Роль в архитектуре приложения:</strong>
 * <ul>
 *   <li>Контроллер → Сервис → Репозиторий → База данных</li>
 *   <li>Содержит бизнес-логику и правила валидации</li>
 *   <li>Управляет транзакциями</li>
 *   <li>Обрабатывает исключения</li>
 * </ul>
 * </p>
 */
@Service
public class GenreService {
    /**
     * Репозиторий для работы с жанрами в базе данных.
     * <p>
     * Spring автоматически внедряет реализацию этого интерфейса,
     * сгенерированную Spring Data JPA.
     * </p>
     */
    @Autowired
    private GenreRepository genreRepository;
    /**
     * Репозиторий для работы с фильмами.
     * <p>
     * Используется для проверки наличия фильмов в жанре
     * перед его удалением.
     * </p>
     */
    @Autowired
    private MovieRepository movieRepository;
    /**
     * Возвращает список всех жанров, отсортированных по названию.
     * <p>
     * Метод делегирует выполнение запроса репозиторию, который
     * использует Spring Data JPA для генерации SQL-запроса.
     * </p>
     *
     * @return список всех жанров в алфавитном порядке по названию
     * @apiNote SQL: SELECT * FROM genres ORDER BY title ASC
     */
    public List<Genre> getAllGenres() {
        /** Делегируем запрос репозиторию
         */
        return genreRepository.findAllByOrderByTitleAsc();
    }
    /**
     * Находит жанр по его уникальному идентификатору.
     * <p>
     * Возвращает {@link Optional}, который может быть пустым,
     * если жанр с указанным ID не найден.
     * </p>
     *
     * @param id идентификатор жанра для поиска
     * @return {@link Optional} содержащий найденный жанр или пустой
     * @throws IllegalArgumentException если id равен null
     * @apiNote SQL: SELECT * FROM genres WHERE id = ?
     */
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }
    /**
     * Сохраняет жанр в базе данных.
     * <p>
     * Если жанр уже существует (имеет id), выполняется обновление.
     * Если жанр новый (id равен null), выполняется вставка.
     * </p>
     *
     * @param genre объект жанра для сохранения
     * @return сохраненный жанр (с присвоенным id, если был новый)
     * @throws IllegalArgumentException если genre равен null
     * @apiNote SQL: INSERT INTO genres ... или UPDATE genres ...
     */
    public Genre saveGenre(Genre genre) {
        /** JPA автоматически определяет: insert или update
         */
        return genreRepository.save(genre);
    }
    /**
     * Удаляет жанр по идентификатору, если в нем нет фильмов.
     * <p>
     * Метод выполняется в транзакции для обеспечения атомарности операции.
     * Сначала проверяется существование жанра, затем наличие фильмов в этом жанре.
     * </p>
     *
     * @param id идентификатор жанра для удаления
     * @return {@code true} если жанр был удален, {@code false} если удаление невозможно
     * @apiNote Логика:
     *          1. Проверить существование жанра
     *          2. Проверить наличие фильмов в жанре
     *          3. Удалить жанр, если фильмов нет
     * @transactional Гарантирует, что операция выполнится полностью или не выполнится вообще
     */
    @Transactional
    public boolean deleteGenre(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            /** Проверяем, есть ли фильмы в этом жанре
             */
            List<com.cinema.management.entity.Movie> movies = movieRepository.findByGenreIdOrderByTitleAsc(id);
            if (movies.isEmpty()) {
                genreRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
    /**
     * Проверяет существование жанра с указанным названием.
     * <p>
     * Используется для валидации уникальности названия жанра
     * перед созданием нового или обновлением существующего.
     * </p>
     *
     * @param title название жанра для проверки
     * @return {@code true} если жанр с таким названием уже существует,
     *         {@code false} в противном случае
     * @throws IllegalArgumentException если title равен null или пустой
     * @apiNote SQL: SELECT COUNT(*) > 0 FROM genres WHERE title = ?
     */
    public boolean genreExists(String title) {
        return genreRepository.existsByTitle(title);
    }
}