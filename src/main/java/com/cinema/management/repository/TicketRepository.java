package com.cinema.management.repository;

import com.cinema.management.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Репозиторий для работы с сущностью {@link Ticket} (билеты).
 * <p>
 * Этот интерфейс предоставляет доступ к операциям с базой данных для сущности Ticket.
 * Включает специализированные методы для поиска билетов по различным критериям:
 * фильмам, покупателям и временным интервалам. Все методы поддерживают сортировку
 * по дате продажи в порядке убывания (последние покупки первыми).
 * </p>
 * <p>
 * Репозиторий использует возможности Spring Data JPA для автоматической генерации
 * сложных запросов на основе соглашений об именовании методов.
 * </p>
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    /**
     * Находит все билеты, отсортированные по дате продажи в порядке убывания.
     * <p>
     * Spring Data JPA автоматически генерирует запрос:
     * <pre>
     * SELECT * FROM tickets ORDER BY date DESC
     * </pre>
     * Этот метод используется для отображения списка всех билетов,
     * где последние покупки показываются первыми.
     * </p>
     *
     * @return список всех билетов, отсортированных по дате (сначала новые)
     */
    List<Ticket> findAllByOrderByDateDesc();
    /**
     * Выполняет регистронезависимый поиск билетов по имени покупателя.
     * <p>
     * Spring Data JPA автоматически генерирует запрос:
     * <pre>
     * SELECT * FROM tickets
     * WHERE LOWER(customer_name) LIKE LOWER(CONCAT('%', ?, '%'))
     * ORDER BY date DESC
     * </pre>
     * Метод поддерживает поиск по части имени.
     * Результаты сортируются по дате продажи в порядке убывания.
     * </p>
     *
     * @param customerName часть имени покупателя для поиска
     * @return список билетов, где имя покупателя содержит указанную строку
     */
    List<Ticket> findByCustomerNameContainingIgnoreCaseOrderByDateDesc(String customerName);
}