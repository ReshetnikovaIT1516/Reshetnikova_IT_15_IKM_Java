package com.cinema.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс-сущность, представляющий фильм в системе управления кинотеатром.
 * <p>
 * Отображается на таблицу "movies" в базе данных. Содержит информацию о фильмах,
 * включая связь с жанром и билетами.
 * </p>
 */
@Entity
@Table(name = "movies")
public class Movie {
    /**
     * Уникальный идентификатор фильма.
     * Первичный ключ, генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Название фильма. Обязательное поле, 1-200 символов.
     */
    @NotBlank(message = "Название фильма не может быть пустым")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    /**
     * Жанр фильма. Обязательное поле, связь Many-to-One с сущностью Genre.
     * Один жанр может принадлежать многим фильмам.
     */
    @NotNull(message = "Жанр обязателен")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
    /**
     * Цена билета на фильм. Обязательное поле, 100-1000 рублей.
     */
    @NotNull(message = "Цена билета обязательна")
    @Min(value = 100, message = "Цена билета должна быть не менее 100")
    @Max(value = 1000, message = "Цена билета не должна превышать 1000")
    @Column(name = "ticket_price", nullable = false)
    private Integer ticketPrice;
    /**
     * Длительность фильма в минутах. Обязательное поле, минимум 60 минут.
     */
    @NotNull(message = "Длительность обязательна")
    @Min(value = 60, message = "Длительность должна быть не менее 60 минут")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    /**
     * Год выпуска фильма. Обязательное поле, 1900-2100 годы.
     */
    @NotNull(message = "Год выпуска обязателен")
    @Min(value = 1900, message = "Год выпуска должен быть не раньше 1900")
    @Max(value = 2100, message = "Год выпуска должен быть не позже 2100")
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;
    /**
     * Рейтинг фильма по 10-балльной шкале. Необязательное поле.
     */
    @DecimalMin(value = "0.0", message = "Рейтинг не может быть меньше 0")
    @DecimalMax(value = "10.0", message = "Рейтинг не может быть больше 10")
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;
    /**
     * Описание фильма. Необязательное поле, до 500 символов.
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;
    /**
     * Список билетов на этот фильм. Связь One-to-Many с сущностью Ticket.
     * Один фильм может иметь много билетов.
     * CascadeType.ALL: операции с фильмом (удаление, обновление) применяются к билетам.
     * cascade = CascadeType.ALL означает:
     * Удалили фильм → удалятся все его билеты
     * Сохранили фильм → сохранятся изменения в билетах
     */
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    /** конструкторы
     */
    public Movie() {
    }

    public Movie(String title, Genre genre, Integer ticketPrice, Integer durationMinutes,
                 Integer releaseYear, BigDecimal rating, String description) {
        this.title = title;
        this.genre = genre;
        this.ticketPrice = ticketPrice;
        this.durationMinutes = durationMinutes;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.description = description;
    }

    /** геттеры и сеттеры
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Integer getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Integer ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** Геттер и сеттер для tickets
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
    /**
     * Возвращает длительность фильма в читаемом формате (часы и минуты).
     */
    public String getFormattedDuration() {
        if (durationMinutes == null) return "";
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return hours > 0 ?
                String.format("%d ч %d мин", hours, minutes) :
                String.format("%d мин", minutes);
    }
    /**
     * Возвращает цену билета с валютой.
     */
    public String getFormattedTicketPrice() {
        return ticketPrice != null ?
                String.format("%d руб.", ticketPrice) :
                "";
    }

    /**
     * Добавляет билет к фильму и устанавливает связь.
     */
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setMovie(this);
    }

    /**
     * Удаляет билет из фильма и разрывает связь.
     */
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setMovie(null);
    }
    /**
     * Возвращает строковое представление объекта Movie.
     * Используется для отладки, логирования и удобного отображения в консоли.
     *
     * Пример вывода: "Movie{id=1, title='Интерстеллар', genre=Фантастика,
     * ticketPrice=400, durationMinutes=169, releaseYear=2014, rating=8.6,
     * description='Космическая драма', ticketsCount=5}"
     *
     * Особенности:
     * - genre выводится как название жанра (genre.getTitle()), а не весь объект Genre
     *   для избежания циклических ссылок (Movie → Genre → Movie...)
     * - tickets выводится только количество (tickets.size()), а не весь список
     *   для краткости и избежания переполнения вывода
     *
     * @return строковое представление объекта Movie
     */
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre=" + (genre != null ? genre.getTitle() : "null") +
                ", ticketPrice=" + ticketPrice +
                ", durationMinutes=" + durationMinutes +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", ticketsCount=" + tickets.size() +
                '}';
    }
}