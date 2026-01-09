В Main.java:
* @SpringBootApplication - запускает всё приложение

В контроллерах:
* @Controller + @RequestMapping - делает класс веб-контроллером

* @GetMapping - показывает страницу

* @PostMapping - принимает данные формы

* @PathVariable - берёт ID из URL (/edit/1)

* @RequestParam - берёт параметры из ?search=...

* @ModelAttribute + @Valid - принимает и проверяет данные формы

В сервисах:
* @Service - делает класс сервисом

* @Autowired - вставляет репозитории

* @Transactional - включает транзакции БД

В репозиториях:
* @Repository - делает интерфейс репозиторием

* @Query + @Param - пишет свои SQL запросы

В сущностях:
* @Entity + @Table - создаёт таблицу в БД

* @Id + @GeneratedValue - делает поле ID

* @Column - настраивает колонки

* @ManyToOne/@OneToMany - создаёт связи между таблицами

* @NotBlank/@NotNull/@Size - проверяет данные при вводе

* @Min/@Max - проверяет числа

* @Override - переопределяет toString() 