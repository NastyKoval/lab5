package server;

import server.application.service.AuthService;
import server.application.service.FlatService;
import server.connection.ThreadPoolManager;
import server.connection.UdpServer;
import server.infrastructure.database.DatabaseManager;
import server.infrastructure.repository.FlatRepository;
import server.infrastructure.repository.FlatRepositoryImpl;
import server.infrastructure.repository.UserRepository;

/**
 * Точка входа сервера.
 * Инициализирует все компоненты и запускает UDP-сервер.
 */
public class MainServer {

    public static void main(String[] args) {

        // Инициализация базы данных
        // Подключаемся через SSH-туннель: ssh -L 5432:pg:5432 <логин>@<сервер>
        String dbUrl = "jdbc:postgresql://localhost:5432/studs";
        String dbUser = "s505056";
        String dbPassword = "6mskl75cq9tHb3Ao";
        String dbDriver = "org.postgresql.Driver";

        DatabaseManager dbManager = new DatabaseManager(dbUrl, dbUser, dbPassword, dbDriver);

        // Инициализация пользователей и сервиса авторизации
        UserRepository userRepository = new UserRepository(dbManager);
        AuthService authService = new AuthService(userRepository);

        // Создаём таблицу квартир (после users, т.к. flats ссылается на users)
        dbManager.initFlatsSchema();

        // Инициализация репозитория и сервиса для работы с квартирами
        FlatRepository flatRepository = new FlatRepositoryImpl(dbManager);
        FlatService flatService = new FlatService(flatRepository);

        // Создание менеджера пулов потоков для многопоточной обработки
        ThreadPoolManager threadPoolManager = new ThreadPoolManager();

        // Хук для сохранения данных при корректном завершении работы сервера
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered, saving collection...");
            try {
                flatRepository.saveAll(flatRepository.findAll());
                System.out.println("Collection saved successfully");
            } catch (Exception e) {
                System.err.println("Ошибка при сохранении: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        // Создание и запуск UDP-сервера
        UdpServer server = new UdpServer(8080, flatService, authService, threadPoolManager);

        System.out.println("Server started on port 8080");
        server.start();
    }
}