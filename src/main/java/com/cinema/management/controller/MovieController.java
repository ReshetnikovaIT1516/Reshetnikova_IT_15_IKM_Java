package com.cinema.management.controller;

import com.cinema.management.entity.Genre;
import com.cinema.management.entity.Movie;
import com.cinema.management.service.GenreService;
import com.cinema.management.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
/**
 * Контроллер для управления фильмами в системе кинотеатра.
 * <p>
 * Этот контроллер обрабатывает все HTTP-запросы, связанные с операциями CRUD
 * (Создание, Чтение, Обновление, Удаление) для сущности {@link Movie}.
 * Предоставляет функциональность поиска фильмов по различным критериям
 * и фильтрации по жанрам.
 * </p>
 * <p>
 * Контроллер взаимодействует с пользователем через веб-интерфейс,
 * валидирует входящие данные и делегирует бизнес-логику сервисному слою.
 * </p>
 */
@Controller
@RequestMapping("/movies")
public class MovieController {
    /**
     * Сервис для работы с фильмами.
     * <p>
     * Spring автоматически внедряет зависимость, предоставляя доступ
     * ко всем операциям бизнес-логики, связанным с фильмами:
     * поиск, фильтрация, сохранение, удаление и т.д.
     * </p>
     */
    @Autowired
    private MovieService movieService;
    /**
     * Сервис для работы с жанрами.
     * <p>
     * Используется для получения списка всех жанров при создании/редактировании
     * фильмов, а также для отображения информации о жанре при фильтрации.
     * </p>
     */
    @Autowired
    private GenreService genreService;
    /**
     * Отображает список фильмов с поддержкой поиска и фильтрации.
     * <p>
     * Обрабатывает GET-запросы к {@code /movies} и поддерживает два
     * необязательных параметра запроса:
     * </p>
     * <ul>
     *   <li>{@code search} - поиск фильмов по названию (частичное совпадение)</li>
     *   <li>{@code genreId} - фильтрация фильмов по идентификатору жанра</li>
     * </ul>
     * <p>
     * Если ни один параметр не указан, отображаются все фильмы.
     * </p>
     *
     * @param search строка поиска по названию фильма (может быть {@code null})
     * @param genreId идентификатор жанра для фильтрации (может быть {@code null})
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "movies/list"
     */
    @GetMapping
    public String listMovies(@RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "genreId", required = false) Long genreId,
                             Model model) {
        List<Movie> movies;

        if (search != null && !search.trim().isEmpty()) {
            movies = movieService.searchMoviesByTitle(search.trim());
            model.addAttribute("search", search);
        } else if (genreId != null) {
            movies = movieService.getMoviesByGenre(genreId);
            Optional<Genre> genre = genreService.getGenreById(genreId);
            genre.ifPresent(g -> model.addAttribute("filterGenre", g));
        } else {
            movies = movieService.getAllMovies();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("genres", genreService.getAllGenres());
        return "movies/list";
    }
    /**
     * Отображает форму для создания нового фильма.
     * <p>
     * Обрабатывает GET-запрос к {@code /movies/create}. Создает новый
     * объект {@link Movie} с предустановленными значениями по умолчанию:
     * </p>
     * <ul>
     *   <li>Цена билета: 300 рублей</li>
     *   <li>Длительность: 120 минут</li>
     *   <li>Год выпуска: текущий год (2024)</li>
     * </ul>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "movies/form"
     */
    @GetMapping("/create")
    public String createMovieForm(Model model) {
        Movie movie = new Movie();
        movie.setTicketPrice(300);
        movie.setDurationMinutes(120);
        movie.setReleaseYear(2024);

        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreService.getAllGenres());
        return "movies/form";
    }
    /**
     * Отображает форму для редактирования существующего фильма.
     * <p>
     * Обрабатывает GET-запрос к {@code /movies/edit/{id}}. Находит фильм
     * по указанному идентификатору и, если он существует, добавляет его
     * в модель для предзаполнения формы. Если фильм не найден, перенаправляет
     * на список фильмов с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор редактируемого фильма
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "movies/form" или перенаправление на "/movies"
     */
    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());
            model.addAttribute("genres", genreService.getAllGenres());
            return "movies/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Фильм не найден");
            return "redirect:/movies";
        }
    }
    /**
     * Обрабатывает сохранение фильма (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос к {@code /movies/save}. Выполняет следующие шаги:
     * </p>
     * <ol>
     *   <li>Валидация данных с использованием аннотаций Bean Validation</li>
     *   <li>Дополнительная проверка рейтинга (должен быть от 0.0 до 10.0)</li>
     *   <li>Сохранение фильма в базе данных</li>
     *   <li>Перенаправление с сообщением об успехе</li>
     * </ol>
     *
     * @param movie объект фильма, заполненный из формы
     * @param result объект для хранения ошибок валидации
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "movies/form" при ошибках или перенаправление на "/movies"
     */
    @PostMapping("/save")
    public String saveMovie(@Valid @ModelAttribute Movie movie,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.getAllGenres());
            return "movies/form";
        }

        /** Проверка рейтинга
         */
        if (movie.getRating() != null) {
            if (movie.getRating().compareTo(new BigDecimal("0.0")) < 0 ||
                    movie.getRating().compareTo(new BigDecimal("10.0")) > 0) {
                result.rejectValue("rating", "error.movie", "Рейтинг должен быть от 0.0 до 10.0");
                model.addAttribute("genres", genreService.getAllGenres());
                return "movies/form";
            }
        }

        movieService.saveMovie(movie);
        redirectAttributes.addFlashAttribute("success", "Фильм успешно сохранен");
        return "redirect:/movies";
    }
    /**
     * Удаляет фильм по идентификатору.
     * <p>
     * Обрабатывает GET-запрос к {@code /movies/delete/{id}}. Пытается найти
     * фильм по идентификатору и, если он существует, удаляет его из базы данных.
     * Если фильм не найден, возвращается сообщение об ошибке.
     * </p>
     *
     * @param id идентификатор удаляемого фильма
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/movies" с сообщением об успехе или ошибке
     */
    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Фильм успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("error", "Фильм не найден");
        }
        return "redirect:/movies";
    }
    /**
     * Отображает детальную информацию о фильме.
     * <p>
     * Обрабатывает GET-запрос к {@code /movies/view/{id}}. Находит фильм
     * по идентификатору и отображает полную информацию о нем. Если фильм
     * не найден, перенаправляет на список фильмов с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемого фильма
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "movies/view" или перенаправление на "/movies"
     */
    @GetMapping("/view/{id}")
    public String viewMovie(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());
            return "movies/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Фильм не найден");
            return "redirect:/movies";
        }
    }
}