/**
 * Основной JavaScript для управления кинотеатром
 * Упрощенная версия с основными функциями
 */

document.addEventListener("DOMContentLoaded", function() {
    console.log("Кинотеатр - JavaScript загружен");

    // ========== ПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ ==========
    document.querySelectorAll('a[href*="delete"], .btn-danger').forEach(element => {
        element.addEventListener('click', function(e) {
            if (this.hasAttribute('onclick')) return; // Если уже есть onclick

            const itemName = this.getAttribute('data-item-name') ||
                           this.closest('tr')?.querySelector('td:nth-child(2)')?.textContent?.trim() ||
                           'эту запись';

            if (!confirm(`Вы уверены, что хотите удалить "${itemName}"?`)) {
                e.preventDefault();
                return false;
            }
        });
    });

    // ========== ВАЛИДАЦИЯ ФОРМ ==========
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    alert(`Пожалуйста, заполните обязательное поле`);
                    field.focus();
                    isValid = false;
                    e.preventDefault();
                }
            });

            return isValid;
        });
    });

    // ========== БЫСТРЫЙ ПОИСК В ТАБЛИЦАХ ==========
    const searchInputs = document.querySelectorAll('input[name="customer"], input[name="search"], input[placeholder*="Поиск"]');
    searchInputs.forEach(input => {
        input.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            const table = this.closest('.container')?.querySelector('table') ||
                         document.querySelector('.table');

            if (!table) return;

            const rows = table.querySelectorAll('tbody tr');
            let hasVisibleRows = false;

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(searchTerm)) {
                    row.style.display = '';
                    hasVisibleRows = true;
                } else {
                    row.style.display = 'none';
                }
            });

            // Показываем сообщение если ничего не найдено
            const tbody = table.querySelector('tbody');
            let noResults = tbody.querySelector('.no-results-message');

            if (!searchTerm) {
                if (noResults) noResults.remove();
                return;
            }

            if (!hasVisibleRows) {
                if (!noResults) {
                    noResults = document.createElement('tr');
                    noResults.className = 'no-results-message';
                    noResults.innerHTML = `<td colspan="100" class="text-center py-4 text-muted">Ничего не найдено</td>`;
                    tbody.appendChild(noResults);
                }
                noResults.style.display = '';
            } else if (noResults) {
                noResults.remove();
            }
        });
    });

    // ========== ПЛАВНАЯ ПРОКРУТКА ДЛЯ ЯКОРЕЙ ==========
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;

            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // ========== АВТОФОКУС НА ФОРМАХ ==========
    document.querySelectorAll('form').forEach(form => {
        const firstInput = form.querySelector('input:not([type="hidden"]):not([disabled]), select:not([disabled]), textarea:not([disabled])');
        if (firstInput && !firstInput.value) {
            setTimeout(() => firstInput.focus(), 100);
        }
    });

    // ========== ПОДСВЕТКА АКТИВНОЙ СТРАНИЦЫ В НАВИГАЦИИ ==========
    function highlightActiveNavItem() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            link.classList.remove('active');

            if (href === '/' && (currentPath === '/' || currentPath === '/index')) {
                link.classList.add('active');
            } else if (href !== '/' && currentPath.startsWith(href)) {
                link.classList.add('active');
            }
        });
    }

    highlightActiveNavItem();

    // ========== АВТОМАТИЧЕСКОЕ СКРЫТИЕ УВЕДОМЛЕНИЙ ==========
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // ========== РАСЧЕТ СТОИМОСТИ БИЛЕТОВ ==========
    function setupTicketPriceCalculator() {
        const priceInput = document.querySelector('input[name="ticketPrice"], input[name="price"]');
        const countInput = document.querySelector('input[name="count"]');
        const totalDisplay = document.querySelector('.total-price');

        if (priceInput && countInput) {
            function calculateTotal() {
                const price = parseFloat(priceInput.value) || 0;
                const count = parseInt(countInput.value) || 1;
                const total = price * count;

                if (totalDisplay) {
                    totalDisplay.textContent = `${total} руб.`;
                }
            }

            priceInput.addEventListener('input', calculateTotal);
            countInput.addEventListener('input', calculateTotal);
            calculateTotal();
        }
    }

    setupTicketPriceCalculator();

    // ========== УПРАВЛЕНИЕ СТАТУСОМ КНОПКИ ОТПРАВКИ ==========
    forms.forEach(form => {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            form.addEventListener('submit', function() {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Сохранение...';
            });
        }
    });

    // ========== ПОДСВЕТКА ПУСТЫХ ПОЛЕЙ ФОРМ ==========
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                if (this.hasAttribute('required') && !this.value.trim()) {
                    this.classList.add('is-invalid');
                } else {
                    this.classList.remove('is-invalid');
                }
            });
        });
    });

    // ========== ОБРАБОТКА ОШИБОК AJAX (если будете добавлять) ==========
    window.addEventListener('error', function(e) {
        console.error('JavaScript ошибка:', e.error);
    });

    // ========== ДОБАВЛЕНИЕ ПОДСКАЗОК К КНОПКАМ ==========
    const actionButtons = document.querySelectorAll('.action-buttons .btn');
    actionButtons.forEach(btn => {
        if (!btn.title && btn.querySelector('i')) {
            const iconClass = btn.querySelector('i').className;
            if (iconClass.includes('bi-eye')) btn.title = 'Просмотр';
            else if (iconClass.includes('bi-pencil')) btn.title = 'Редактировать';
            else if (iconClass.includes('bi-trash')) btn.title = 'Удалить';
            else if (iconClass.includes('bi-plus')) btn.title = 'Добавить';
        }
    });
});

// ========== УТИЛИТНЫЕ ФУНКЦИИ ==========

/**
 * Форматирует дату в русском формате
 */
function formatRussianDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).replace(',', '');
}

/**
 * Форматирует продолжительность фильма
 */
function formatMovieDuration(minutes) {
    if (!minutes) return '';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours} ч ${mins} мин` : `${mins} мин`;
}

/**
 * Показывает простое уведомление
 */
function showSimpleMessage(message, type = 'info') {
    // Создаем элемент уведомления
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 1050;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;

    document.body.appendChild(alertDiv);

    // Автоматически скрываем через 5 секунд
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

/**
 * Проверяет, является ли значение числом
 */
function isNumber(value) {
    return !isNaN(parseFloat(value)) && isFinite(value);
}

/**
 * Ограничивает ввод только цифрами
 */
function allowOnlyNumbers(input) {
    input.addEventListener('input', function() {
        this.value = this.value.replace(/[^\d]/g, '');
    });
}

/**
 * Запрещает ввод отрицательных чисел
 */
function preventNegativeNumbers(input) {
    input.addEventListener('input', function() {
        if (this.value < 0) this.value = 0;
    });
}