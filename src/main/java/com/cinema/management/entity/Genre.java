package com.cinema.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Класс-сущность, представляющий жанр фильма в системе управления кинотеатром.
 * <p>
 * Этот класс отображается на таблицу "genres" в базе данных и содержит информацию
 * о жанрах фильмов. Каждый жанр имеет уникальное название и описание.
 * </p>
 * <p>
 * Класс использует аннотации Jakarta Persistence API (JPA) для настройки
 * соответствия между объектами Java и записями в базе данных,
 * а также аннотации Bean Validation для проверки корректности данных.
 * </p>
 */
@Entity
@Table(name = "genres")
public class Genre {
    /**
     * Уникальный идентификатор жанра.
     * <p>
     * Первичный ключ таблицы. Значение генерируется автоматически
     * базой данных при вставке новой записи.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Название жанра.
     * <p>
     * Обязательное для заполнения поле. Название должно быть уникальным
     * среди всех жанров в системе. Длина названия ограничена 100 символами.
     * </p>
     */
    @NotBlank(message = "Название жанра не может быть пустым")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    @Column(name = "title", nullable = false, unique = true, length = 100)
    private String title;
    /**
     * Описание жанра.
     * <p>
     * Необязательное поле, содержащее дополнительную информацию о жанре.
     * Максимальная длина описания - 500 символов.
     * </p>
     */
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;
    /**
     * Конструктор по умолчанию.
     * <p>
     * Требуется для корректной работы JPA и Hibernate.
     * Используется фреймворками для создания экземпляров класса
     * при извлечении данных из базы данных.
     * </p>
     */
    public Genre() {
    }
    /**
     * Конструктор с параметрами для удобного создания объектов.
     * <p>
     * Используется для тестирования или ручного создания объектов
     * в бизнес-логике приложения.
     * </p>
     */
    public Genre(String title, String description) {
        this.title = title;
        this.description = description;
    }
    /**
     * Возвращает уникальный идентификатор жанра.
     *
     * @return идентификатор жанра
     */
    public Long getId() {
        return id;
    }
    /**
     * Устанавливает уникальный идентификатор жанра.
     * <p>
     * Обычно вызывается фреймворком JPA при загрузке данных из БД.
     * Не рекомендуется изменять ID вручную в бизнес-логике.
     * </p>
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Возвращает название жанра.
     *
     * @return название жанра
     */
    public String getTitle() {
        return title;
    }
    /**
     * Устанавливает название жанра.
     * <p>
     * При установке значения автоматически проверяется валидатором
     * на соответствие требованиям {@code @NotBlank} и {@code @Size}.
     * </p>
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Возвращает описание жанра.
     *
     * @return описание жанра, может быть {@code null}
     */
    public String getDescription() {
        return description;
    }
    /**
     * Устанавливает описание жанра.
     * <p>
     * При установке значения автоматически проверяется валидатором
     * на соответствие требованию {@code @Size(max = 500)}.
     * </p>
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Возвращает строковое представление объекта Genre.
     * <p>
     * Используется для отладки, логирования и отображения
     * объекта в читаемом виде.
     * </p>
     *
     * @return строковое представление объекта в формате:
     *         "Genre{id=1, title='Комедия', description='Веселые фильмы'}"
     */
    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
