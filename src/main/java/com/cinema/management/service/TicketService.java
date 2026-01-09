package com.cinema.management.service;

import com.cinema.management.entity.Ticket;
import com.cinema.management.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Сервис для управления операциями с билетами в системе кинотеатра.
 * <p>
 * Этот сервис предоставляет бизнес-логику для работы с продажей билетов,
 * включая создание, поиск, фильтрацию и удаление билетов. Сервис также
 * обеспечивает связь между фильмами и проданными билетами.
 * </p>
 * <p>
 * <strong>Важная особенность:</strong> Билеты автоматически удаляются
 * при удалении связанного фильма благодаря каскадным операциям.
 * </p>
 */
@Service
public class TicketService {
    /**
     * Репозиторий для доступа к данным о билетах в базе данных.
     * <p>
     * Spring Data JPA автоматически создает реализацию этого интерфейса,
     * предоставляя стандартные CRUD операции и пользовательские запросы.
     * </p>
     */
    @Autowired
    private TicketRepository ticketRepository;
    /**
     * Возвращает список всех билетов, отсортированных по дате покупки (сначала новые).
     * <p>
     * Используется для отображения истории продаж в административной панели.
     * Сортировка по убыванию даты позволяет видеть последние продажи первыми.
     * </p>
     *
     * @return список всех билетов, отсортированных по дате (новые → старые)
     * @apiNote SQL: SELECT * FROM tickets ORDER BY date DESC
     * @example
     * <pre>
     *  Возвращает:
     *  1. Билет от 2024-01-10 (сегодня)
     *  2. Билет от 2024-01-09 (вчера)
     *  3. Билет от 2024-01-08 (позавчера)
     * </pre>
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByDateDesc();
    }
    /**
     * Находит билет по его уникальному идентификатору.
     * <p>
     * Возвращает {@link Optional} для безопасной обработки случаев,
     * когда билет с указанным ID не найден.
     * </p>
     *
     * @param id уникальный идентификатор билета
     * @return {@link Optional} с найденным билетом или пустой Optional
     * @throws IllegalArgumentException если id равен null
     * @apiNote SQL: SELECT * FROM tickets WHERE id = ?
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
    /**
     * Сохраняет билет в базе данных.
     * <p>
     * Метод выполняет как создание новых билетов, так и обновление существующих.
     * При сохранении автоматически вычисляется общая стоимость билетов
     * на основе количества и цены фильма.
     * </p>
     *
     * @param ticket объект билета для сохранения
     * @return сохраненный билет (с присвоенным id для новых записей)
     * @throws IllegalArgumentException если ticket равен null
     * @apiNote JPA автоматически определяет нужную операцию
     * @see Ticket#getTotalPrice() автоматический расчет стоимости
     */
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
    /**
     * Удаляет билет по его идентификатору.
     * <p>
     * Удаление билета не влияет на связанный фильм. Это независимая операция,
     * которая может использоваться для отмены продажи или исправления ошибок.
     * </p>
     *
     * @param id идентификатор билета для удаления
     * @throws IllegalArgumentException если id равен null
     * @apiNote SQL: DELETE FROM tickets WHERE id = ?
     */
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
    /**
     * Ищет билеты по имени покупателя (частичное совпадение, без учета регистра).
     * <p>
     * Используется для поиска истории покупок конкретного клиента или
     * группы клиентов с похожими именами.
     * </p>
     *
     * @param customerName имя покупателя или его часть для поиска
     * @return список билетов, отсортированных по дате (новые → старые)
     * @throws IllegalArgumentException если customerName равен null или пустой
     * @apiNote SQL: SELECT * FROM tickets WHERE LOWER(customer_name) LIKE LOWER('%?%') ORDER BY date DESC
     * @example
     * <pre>
     *  При поиске "Иван" найдет:
     *  - "Иван Иванов"
     *  - "Иван Петров"
     *  - "Алексей Иванов" (содержит "Иванов")
     * </pre>
     */
    public List<Ticket> getTicketsByCustomer(String customerName) {
        return ticketRepository.findByCustomerNameContainingIgnoreCaseOrderByDateDesc(customerName);
    }
    /**
     * Возвращает все билеты, проданные в указанном диапазоне дат.
     * <p>
     * Используется для формирования отчетов о продажах за определенный период,
     * анализа пиковых дней продаж и финансового учета.
     * </p>
     *
     * @param start начальная дата диапазона (включительно)
     * @param end конечная дата диапазона (включительно)
     * @return список билетов, проданных в указанном периоде
     * @throws IllegalArgumentException если start или end равны null
     * @throws IllegalArgumentException если start позже end
     * @apiNote SQL: SELECT * FROM tickets WHERE date BETWEEN ? AND ?
     */

}