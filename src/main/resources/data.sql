-- Удаляем существующие таблицы (если есть)
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS genres;

-- Создаем таблицу жанров
CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- Создаем таблицу фильмов
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    genre_id BIGINT NOT NULL,
    ticket_price INT NOT NULL CHECK (ticket_price >= 100 AND ticket_price <= 1000),
    duration_minutes INT NOT NULL CHECK (duration_minutes >= 60),
    release_year INT NOT NULL CHECK (release_year >= 1900 AND release_year <= 2100),
    rating DECIMAL(3,1) CHECK (rating >= 0.0 AND rating <= 10.0),
    description VARCHAR(500),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- Создаем таблицу билетов
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    count INT NOT NULL CHECK (count >= 1),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_name VARCHAR(200) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- Вставляем начальные данные
INSERT INTO genres (title, description) VALUES
('Боевик', 'Фильмы с большим количеством экшена и спецэффектов'),
('Комедия', 'Юмористические фильмы для поднятия настроения'),
('Драма', 'Серьезные фильмы с глубоким сюжетом'),
('Фантастика', 'Фильмы о будущем и технологиях'),
('Ужасы', 'Страшные фильмы с элементами мистики');

INSERT INTO movies (title, genre_id, ticket_price, duration_minutes, release_year, rating, description) VALUES
('Мстители: Финал', 1, 500, 181, 2019, 8.4, 'Завершение саги о супергероях'),
('Джентельмены', 2, 450, 113, 2019, 8.1, 'Криминальная комедия Гая Ричи'),
('Джокер', 3, 400, 122, 2019, 8.4, 'История становления злодея'),
('Дюна', 4, 550, 155, 2021, 8.0, 'Эпическая фантастическая сага'),
('Оно', 5, 350, 135, 2017, 7.3, 'Экранизация романа Стивена Кинга');

INSERT INTO tickets (movie_id, count, date, customer_name) VALUES
(1, 2, '2024-01-05 19:30:00', 'Иван Петров'),
(2, 1, '2024-01-05 20:00:00', 'Мария Сидорова'),
(3, 3, '2024-01-06 18:00:00', 'Алексей Иванов'),
(4, 2, '2024-01-06 21:00:00', 'Екатерина Смирнова'),
(5, 1, '2024-01-07 22:00:00', 'Дмитрий Кузнецов');