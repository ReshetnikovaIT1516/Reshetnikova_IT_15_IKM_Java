package com.cinema.management.controller;

import com.cinema.management.entity.Movie;
import com.cinema.management.entity.Ticket;
import com.cinema.management.service.MovieService;
import com.cinema.management.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Контроллер для управления билетами в системе кинотеатра.
 * <p>
 * Этот контроллер обрабатывает все операции, связанные с продажей,
 * просмотром и управлением билетами. Включает расширенное логирование
 * для отслеживания действий пользователей и диагностики проблем.
 * </p>
 * <p>
 * Контроллер поддерживает создание билетов как для конкретных фильмов,
 * так и с выбором фильма из списка, а также предоставляет функциональность
 * поиска билетов по покупателю.
 * </p>
 */
@Controller
@RequestMapping("/tickets")
public class TicketController {
    /**
     * Логгер для записи информационных сообщений, предупреждений и ошибок.
     * <p>
     * Используется для:
     * - Отслеживания выполнения операций
     * - Диагностики проблем
     * - Проверка действий пользователей
     * </p>
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    /**
     * Сервис для работы с билетами.
     * <p>
     * Предоставляет доступ к бизнес-логике операций с билетами:
     * создание, чтение, обновление, удаление, поиск и фильтрация.
     * </p>
     */
    @Autowired
    private TicketService ticketService;
    /**
     * Сервис для работы с фильмами.
     * <p>
     * Используется для получения списка фильмов при создании/редактировании
     * билетов, а также для предварительной проверки наличия фильмов в системе.
     * </p>
     */
    @Autowired
    private MovieService movieService;
    /**
     * Отображает список билетов с возможностью фильтрации по покупателю.
     * <p>
     * Обрабатывает GET-запросы к {@code /tickets}. Поддерживает необязательный
     * параметр {@code customer} для поиска билетов по имени покупателя.
     * </p>
     * <p>
     * Метод включает расширенное логирование для отслеживания количества
     * доступных фильмов и предупреждения, если фильмы отсутствуют.
     * </p>
     * @param customer имя покупателя для фильтрации (может быть {@code null})
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "tickets/list"
     */
    @GetMapping
    public String listTickets(@RequestParam(value = "customer", required = false) String customer,
                              Model model) {
        logger.info("GET /tickets - список билетов");
        List<Ticket> tickets;
        List<Movie> movies = movieService.getAllMovies(); // Получаем фильмы

        logger.info("Количество фильмов в базе: {}", movies.size());
        if (movies.isEmpty()) {
            logger.warn("В базе данных нет фильмов! Невозможно продать билеты.");
        }

        if (customer != null && !customer.trim().isEmpty()) {
            tickets = ticketService.getTicketsByCustomer(customer.trim());
            model.addAttribute("customer", customer);
            logger.info("Поиск билетов для покупателя: {}", customer);
        } else {
            tickets = ticketService.getAllTickets();
            logger.info("Получение всех билетов, найдено: {}", tickets.size());
        }

        model.addAttribute("tickets", tickets);
        /** Добавляем фильмы в модель
         */
        model.addAttribute("movies", movies);
        return "tickets/list";
    }
    /**
     * Отображает форму для создания нового билета.
     * <p>
     * Обрабатывает GET-запрос к {@code /tickets/create}. Создает новый
     * объект {@link Ticket} с предустановленными значениями:
     * </p>
     * <ul>
     *   <li>Количество билетов: 1</li>
     *   <li>Дата продажи: текущее время</li>
     * </ul>
     * <p>
     * Метод логирует количество доступных фильмов для выбора.
     * </p>
     *
     * @param model объект для передачи данных в представление
     * @return имя Thymeleaf шаблона "tickets/form"
     */
    @GetMapping("/create")
    public String createTicketForm(Model model) {
        logger.info("GET /tickets/create - форма создания билета");
        Ticket ticket = new Ticket();
        ticket.setCount(1);
        ticket.setDate(LocalDateTime.now());

        List<Movie> movies = movieService.getAllMovies();
        logger.info("Доступно фильмов для выбора: {}", movies.size());

        model.addAttribute("ticket", ticket);
        model.addAttribute("movies", movies);
        return "tickets/form";
    }
    /**
     * Отображает форму для создания билета для конкретного фильма.
     * <p>
     * Обрабатывает GET-запрос к {@code /tickets/create/{movieId}}.
     * Автоматически предварительно выбирает указанный фильм в форме.
     * </p>
     * <p>
     * Используется, например, когда пользователь хочет купить билет
     * на конкретный фильм со страницы просмотра фильма.
     * </p>
     *
     * @param movieId идентификатор фильма, для которого создается билет
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "tickets/form" или перенаправление на "/movies"
     */
    @GetMapping("/create/{movieId}")
    public String createTicketForMovie(@PathVariable Long movieId,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        logger.info("GET /tickets/create/{} - форма создания билета для фильма", movieId);

        Optional<Movie> movie = movieService.getMovieById(movieId);
        if (movie.isPresent()) {
            logger.info("Фильм найден: ID={}, Название={}", movie.get().getId(), movie.get().getTitle());

            Ticket ticket = new Ticket();
            ticket.setMovie(movie.get());
            ticket.setCount(1);
            ticket.setDate(LocalDateTime.now());

            List<Movie> movies = movieService.getAllMovies();
            logger.info("Всего фильмов в базе: {}", movies.size());

            model.addAttribute("ticket", ticket);
            model.addAttribute("movies", movies);
            return "tickets/form";
        } else {
            logger.warn("Фильм с ID {} не найден", movieId);
            redirectAttributes.addFlashAttribute("error", "Фильм не найден");
            return "redirect:/movies";
        }
    }
    /**
     * Обрабатывает сохранение билета (создание или обновление).
     * <p>
     * Обрабатывает POST-запрос к {@code /tickets/save}. Выполняет:
     * </p>
     * <ol>
     *   <li>Валидацию данных с использованием аннотаций Bean Validation</li>
     *   <li>Логирование деталей сохраняемого билета</li>
     *   <li>Сохранение билета в базе данных</li>
     *   <li>Перенаправление с сообщением об успехе</li>
     * </ol>
     *
     * @param ticket объект билета, заполненный из формы
     * @param result объект для хранения ошибок валидации
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "tickets/form" при ошибках или перенаправление на "/tickets"
     */
    @PostMapping("/save")
    public String saveTicket(@Valid @ModelAttribute Ticket ticket,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        logger.info("POST /tickets/save - сохранение билета");

        if (result.hasErrors()) {
            logger.warn("Ошибки валидации при сохранении билета: {}", result.getAllErrors());
            model.addAttribute("movies", movieService.getAllMovies());
            return "tickets/form";
        }

        logger.info("Сохранение билета: фильм={}, покупатель={}, количество={}",
                ticket.getMovie() != null ? ticket.getMovie().getId() : "null",
                ticket.getCustomerName(),
                ticket.getCount());

        ticketService.saveTicket(ticket);
        redirectAttributes.addFlashAttribute("success", "Билет успешно сохранен");
        return "redirect:/tickets";
    }
    /**
     * Удаляет билет по идентификатору.
     * <p>
     * Обрабатывает GET-запрос к {@code /tickets/delete/{id}}. Удаляет
     * билет из базы данных и логирует операцию.
     * </p>
     * <p>
     * В отличие от других контроллеров, здесь нет предварительной проверки
     * существования билета, так как операция удаления неизменна.
     * </p>
     *
     * @param id идентификатор удаляемого билета
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return перенаправление на "/tickets" с сообщением об успехе
     */
    @GetMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("GET /tickets/delete/{} - удаление билета", id);
        ticketService.deleteTicket(id);
        redirectAttributes.addFlashAttribute("success", "Билет успешно удален");
        return "redirect:/tickets";
    }
    /**
     * Отображает детальную информацию о билете.
     * <p>
     * Обрабатывает GET-запрос к {@code /tickets/view/{id}}. Находит билет
     * по идентификатору и отображает полную информацию о нем, включая
     * связанный фильм и расчет общей стоимости.
     * </p>
     *
     * @param id идентификатор просматриваемого билета
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "tickets/view" или перенаправление на "/tickets"
     */
    @GetMapping("/view/{id}")
    public String viewTicket(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("GET /tickets/view/{} - просмотр билета", id);
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.get());
            return "tickets/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Билет не найден");
            return "redirect:/tickets";
        }
    }
    /**
     * Отображает форму для редактирования существующего билета.
     * <p>
     * Обрабатывает GET-запрос к {@code /tickets/edit/{id}}. Находит билет
     * по идентификатору и, если он существует, добавляет его в модель
     * для предзаполнения формы. Если билет не найден, перенаправляет
     * на список билетов с сообщением об ошибке.
     * </p>
     *
     * @param id идентификатор редактируемого билета
     * @param model объект для передачи данных в представление
     * @param redirectAttributes атрибуты для передачи данных при перенаправлении
     * @return имя Thymeleaf шаблона "tickets/form" или перенаправление на "/tickets"
     */
    @GetMapping("/edit/{id}")
    public String editTicketForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("GET /tickets/edit/{} - форма редактирования билета", id);
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.get());
            model.addAttribute("movies", movieService.getAllMovies());
            return "tickets/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Билет не найден");
            return "redirect:/tickets";
        }
    }
}