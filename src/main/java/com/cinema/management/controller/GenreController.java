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
import java.util.List;
import java.util.Optional;
/**
 * Контроллер для управления жанрами фильмов.
 * <p>
 * Этот класс обрабатывает HTTP-запросы, связанные с операциями CRUD (Create, Read, Update, Delete)
 * для сущности {@link Genre}. Контроллер взаимодействует с пользователем через веб-интерфейс
 * и делегирует бизнес-логику сервисному слою.
 * </p>
 * <p>
 * Все методы контроллера возвращают имена Thymeleaf шаблонов, которые отображаются пользователю.
 * Контроллер использует паттерн MVC (Model-View-Controller) для разделения ответственности.
 * </p>
 */
@Controller
@RequestMapping("/genres")
public class GenreController {
    /**
     * Сервис для работы с жанрами.
     * <p>
     * Spring автоматически внедряет зависимость (Dependency Injection)
     * благодаря аннотации {@code @Autowired}. Это позволяет использовать
     * бизнес-логику сервиса без явного создания экземпляра.
     * </p>
     *
     * @see GenreService
     */
    @Autowired
    private GenreService genreService;
    /**
     * Сервис для работы с фильмами.
     * <p>
     * Используется для получения фильмов определенного жанра
     * при просмотре детальной информации о жанре.
     * </p>
     */
    @Autowired
    private MovieService movieService;
    /**
     * Отображает список всех жанров.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /genres}. Получает все жанры
     * из базы данных через сервисный слой и добавляет их в модель для отображения
     * в Thymeleaf шаблоне.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "genres/list"
     */
    @GetMapping
    public String listGenres(Model model) {
        model.addAttribute("genres", genreService.getAllGenres());
        return "genres/list";
    }
    /**
     * Отображает форму для создания нового жанра.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /genres/create}. Создает новый
     * пустой объект {@link Genre} и добавляет его в модель для привязки к форме.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "genres/form"
     */
    @GetMapping("/create")
    public String createGenreForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "genres/form";
    }
    /**
     * Отображает форму для редактирования существующего жанра.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /genres/edit/{id}}. Находит жанр
     * по указанному идентификатору и, если он существует, добавляет его в модель
     * для предзаполнения формы. Если жанр не найден, перенаправляет на список жанров
     * с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор редактируемого жанра
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "genres/form" или перенаправление на "/genres"
     */
    @GetMapping("/edit/{id}")
    public String editGenreForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Genre> genre = genreService.getGenreById(id);
        if (genre.isPresent()) {
            model.addAttribute("genre", genre.get());
            return "genres/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Жанр не найден");
            return "redirect:/genres";
        }
    }
    /**
     * Обрабатывает сохранение жанра (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос по адресу {@code /genres/save}. Выполняет несколько шагов:
     * </p>
     * <ol>
     *   <li>Валидация данных с использованием аннотаций Bean Validation</li>
     *   <li>Проверка уникальности названия жанра</li>
     *   <li>Сохранение жанра в базе данных</li>
     *   <li>Перенаправление с сообщением об успехе или ошибке</li>
     * </ol>
     *
     * @param genre объект жанра, заполненный из формы
     * @param result объект для хранения ошибок валидации
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "genres/form" при ошибках или перенаправление на "/genres"
     */
    @PostMapping("/save")
    public String saveGenre(@Valid @ModelAttribute Genre genre,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        /** Если есть ошибки валидации, возвращаем форму с сообщениями об ошибках
         */
        if (result.hasErrors()) {
            return "genres/form";
        }

        /** Проверка на уникальность названия
         */
        if (genre.getId() == null) {
            if (genreService.genreExists(genre.getTitle())) {
                result.rejectValue("title", "error.genre", "Жанр с таким названием уже существует");
                return "genres/form";
            }
        } else {
            /** Для существующего жанра проверяем, изменилось ли название
             */
            Optional<Genre> existing = genreService.getGenreById(genre.getId());
            if (existing.isPresent() &&
                    !existing.get().getTitle().equals(genre.getTitle()) &&
                    genreService.genreExists(genre.getTitle())) {
                result.rejectValue("title", "error.genre", "Жанр с таким названием уже существует");
                return "genres/form";
            }
        }
        /** Сохранение жанра в базе данных
         */
        genreService.saveGenre(genre);
        redirectAttributes.addFlashAttribute("success", "Жанр успешно сохранен");
        return "redirect:/genres";
    }
    /**
     * Удаляет жанр по идентификатору.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /genres/delete/{id}}. Пытается удалить
     * жанр, но только если нет связанных фильмов в этом жанре (проверка выполняется
     * в сервисном слое). Если удаление невозможно, возвращается сообщение об ошибке.
     * </p>
     *
     * @param id идентификатор удаляемого жанра
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/genres" с сообщением об успехе или ошибке
     */
    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = genreService.deleteGenre(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Жанр успешно удален");
        } else {
            redirectAttributes.addFlashAttribute("error", "Невозможно удалить жанр. Существуют фильмы в этом жанре.");
        }
        return "redirect:/genres";
    }
    /**
     * Отображает детальную информацию о жанре, включая список фильмов этого жанра.
     * <p>
     * Обрабатывает GET-запрос по адресу {@code /genres/view/{id}}. Находит жанр
     * по идентификатору и все фильмы, принадлежащие этому жанру. Если жанр не найден,
     * перенаправляет на список жанров с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор просматриваемого жанра
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "genres/view" или перенаправление на "/genres"
     */
    @GetMapping("/view/{id}")
    public String viewGenre(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Genre> genre = genreService.getGenreById(id);
        if (genre.isPresent()) {
            model.addAttribute("genre", genre.get());

            /** Загрузка фильмов этого жанра
             */
            List<Movie> movies = movieService.getMoviesByGenre(id);
            model.addAttribute("movies", movies);

            return "genres/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Жанр не найден");
            return "redirect:/genres";
        }
    }
}