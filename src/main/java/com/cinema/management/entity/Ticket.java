package com.cinema.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
/**
 * Класс-сущность, представляющий билет на фильм в системе управления кинотеатром.
 * <p>
 * Отображается на таблицу "tickets" в базе данных. Каждый билет связан с конкретным фильмом
 * и содержит информацию о покупке.
 * </p>
 */
@Entity
@Table(name = "tickets")
public class Ticket {
    /**
     * Уникальный идентификатор билета.
     * Первичный ключ, генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Фильм, на который куплен билет. Обязательное поле.
     * Связь Many-to-One с сущностью Movie.
     */
    @NotNull(message = "Фильм обязателен")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    /**
     * Количество билетов. Обязательное поле, минимум 1, максимум 50.
     */
    @NotNull(message = "Количество билетов обязательно")
    @Min(value = 1, message = "Количество билетов должно быть не менее 1")
    @Max(value = 50, message = "Количество билетов должно быть не более 50")
    @Column(name = "count", nullable = false)
    private Integer count;
    /**
     * Дата и время покупки билета. Заполняется автоматически текущей датой.
     */
    @Column(name = "date")
    private LocalDateTime date;
    /**
     * Имя покупателя. Обязательное поле, 1-200 символов.
     */
    @NotBlank(message = "Имя покупателя обязательно")
    @Size(min = 1, max = 200, message = "Имя должно быть от 1 до 200 символов")
    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    /**
     * Конструкторы
     * Конструктор по умолчанию. Автоматически устанавливает текущую дату.
     */
    public Ticket() {
        this.date = LocalDateTime.now();
    }
    /**
     * Конструктор с параметрами для удобного создания объектов.
     * Использует setMovie() для установки двусторонней связи.
     */
    public Ticket(Movie movie, Integer count, String customerName) {
        setMovie(movie); // Используем наш setter для установки связи
        this.count = count;
        this.customerName = customerName;
        this.date = LocalDateTime.now();
    }

    /** Геттеры и сеттеры
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    /**
     * Сеттер для установки фильма с управлением двусторонней связью.
     * Удаляет билет из старого фильма и добавляет в новый.
     */
    public void setMovie(Movie movie) {
        /** Если устанавливается тот же самый фильм, ничего не делаем
         */
        if (this.movie != null && this.movie.equals(movie)) {
            return;
        }

        /** Удаляем себя из старого фильма
         */
        if (this.movie != null) {
            Movie oldMovie = this.movie;
            this.movie = null;
            oldMovie.getTickets().remove(this);
        }

        /** Устанавливаем новый фильм
         */
        this.movie = movie;

        /** Добавляем себя в новый фильм
         */
        if (movie != null && !movie.getTickets().contains(this)) {
            movie.getTickets().add(this);
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Рассчитывает общую стоимость билетов (цена фильма × количество).
     *
     * @return общая стоимость или 0, если данные отсутствуют
     */
    public Integer getTotalPrice() {
        if (movie != null && movie.getTicketPrice() != null && count != null) {
            return movie.getTicketPrice() * count;
        }
        return 0;
    }
    /**
     * Возвращает дату в читаемом формате (заменяет 'T' на пробел).
     */
    public String getFormattedDate() {
        if (date == null) return "";
        return date.toString().replace('T', ' ');
    }
    /**
     * Возвращает общую стоимость с валютой.
     */
    public String getFormattedTotalPrice() {
        Integer total = getTotalPrice();
        return total > 0 ? String.format("%d руб.", total) : "";
    }

    /**
     * Безопасно удаляет связь с фильмом.
     * Убирает билет из списка фильма и обнуляет ссылку.
     */
    public void removeFromMovie() {
        if (this.movie != null) {
            Movie currentMovie = this.movie;
            this.movie = null;
            currentMovie.getTickets().remove(this);
        }
    }

    /**
     * Переносит билет на другой фильм.
     *
     * @param newMovie новый фильм для билета
     */
    public void transferToMovie(Movie newMovie) {
        setMovie(newMovie);
    }
    /**
     * Возвращает строковое представление объекта Ticket.
     * Используется для отладки, логирования и удобного вывода в консоль.
     * <p>
     * Пример вывода: "Ticket{id=1, movie=Интерстеллар, count=2, date=2024-01-10T18:30, customerName='Иван Иванов'}"
     * </p>
     *
     * @return строку с основными полями билета
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", movie=" + (movie != null ? movie.getTitle() : "null") +
                ", count=" + count +
                ", date=" + date +
                ", customerName='" + customerName + '\'' +
                '}';
    }
    /**
     * Сравнивает данный объект Ticket с другим объектом на равенство.
     * Два билета считаются равными, если они имеют одинаковый ID.
     * <p>
     * Это стандартный подход для JPA-сущностей, так как ID гарантированно уникален
     * и генерируется базой данных.
     * </p>
     *
     * @param o объект для сравнения
     * @return true, если объекты равны (имеют одинаковый ID)
     */
    @Override
    public boolean equals(Object o) {
        /** 1. Проверка ссылки на тот же объект
         */
        if (this == o) return true;
        /** 2. Проверка типа объекта и null
         */
        if (o == null || getClass() != o.getClass()) return false;
        /** 3. Приведение типа
         */
        Ticket ticket = (Ticket) o;
        /** 4. Сравнение по ID (null безопасно)
         Только если оба ID не null и равны
         */
        return id != null && id.equals(ticket.id);
    }
    /**
     * Возвращает хэш-код объекта Ticket.
     * Для JPA-сущностей используется консистентный хэш-код на основе класса,
     * а не полей, так как ID может быть null до сохранения в БД.
     * <p>
     * <b>Важно:</b> Этот метод должен быть согласован с equals().
     * Если equals() сравнивает по ID, то hashCode() должен давать одинаковый
     * результат для объектов одного класса (но не обязательно для всех объектов
     * с одинаковым ID, так как ID может быть null).
     * </p>
     *
     * @return хэш-код на основе класса объекта
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}