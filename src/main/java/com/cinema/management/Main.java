package com.cinema.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для управления кинотеатром.
 *
 * <p>Приложение предоставляет функционал для управления:</p>
 * <ul>
 *   <li>Фильмами (добавление, редактирование, удаление, просмотр)</li>
 *   <li>Жанрами фильмов</li>
 *   <li>Сеансами показа</li>
 *   <li>Продажей билетов</li>
 * </ul>
 *
 * <p>Приложение использует:</p>
 * <ul>
 *   <li>Spring Boot 3.x</li>
 *   <li>Spring Data JPA</li>
 *   <li>PostgreSQL базу данных</li>
 *   <li>Thymeleaf для HTML шаблонов</li>
 * </ul>
 *
 * @author Система управления кинотеатром
 * @version 1.0
 */
@SpringBootApplication
public class Main {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}