package org.example;

import client.presentation.ConsoleUI;
import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.validator.InputValidator;
import server.application.command.CommandRegistryServer;
import server.application.service.FlatService;
import server.infrastructure.file.FileManager;
import server.infrastructure.repository.FlatRepositoryImpl;

/**
 * Точка входа в приложение FlatManager.
 *
 * <p>Создаёт все необходимые компоненты и запускает приложение:</p>
 * <ul>
 *   <li>FileManager - работа с файлами</li>
 *   <li>FlatRepositoryImpl - хранилище данных (LinkedList)</li>
 *   <li>FlatService - бизнес-логика</li>
 *   <li>CommandRegistryServer - серверный реестр команд</li>
 *   <li>CommandRegistry - клиентский реестр команд</li>
 *   <li>InputValidator - валидатор ввода</li>
 *   <li>ConsoleUI - консольный интерфейс</li>
 * </ul>
 *
 * <p>Требования:</p>
 * <ul>
 *   <li>Имя файла передаётся через аргумент командной строки</li>
 *   <li>Данные хранятся в формате CSV</li>
 *   <li>При запуске коллекция заполняется из файла</li>
 *   <li>ID генерируются автоматически</li>
 * </ul>
 */
public class Main {

    /**
     * Главная точка входа в приложение.
     *
     * @param args аргументы командной строки
     *             args[0] - путь к файлу с данными (CSV)
     */
    public static void main(String[] args) {

        System.out.println("FlatManager - Управление квартирами  ");

        try {
            // Проверка аргументов
            if (args.length == 0) {
                System.err.println(" Ошибка: не указан файл данных");
                System.err.println("Использование: java Main <путь_к_файлу.csv>");
                System.err.println("Пример: java Main data/flats.csv");
                System.exit(1);
            }

            String fileName = args[0];
            System.out.println(" Файл данных: " + fileName);

            // Инфаструктура(бд)
            // FileManager - чтение/запись CSV файлов
            FileManager fileManager = new FileManager(fileName);

            // FlatRepositoryImpl - хранилище (LinkedList) + загрузка из файла
            FlatRepositoryImpl repository = new FlatRepositoryImpl(fileName);
            System.out.println(" Репозиторий инициализирован");

            // Бизнес логика
            // FlatService - валидация, генерация ID, CRUD операции
            FlatService flatService = new FlatService(repository);
            System.out.println(" Сервис готов к работе");

            // Сервер
            // CommandRegistryServer - регистрация и выполнение команд
            CommandRegistryServer server = new CommandRegistryServer(flatService);
            System.out.println(" Сервер команд запущен\n");

            // Клиент
            // CommandRegistry - создание Request объектов
            CommandRegistry registry = new CommandRegistry();

            // InputValidator - проверка ввода пользователя
            InputValidator validator = new InputValidator();

            // ConsoleUI - консольный интерфейс
            ConsoleUI consoleUI = new ConsoleUI(registry, server, validator);


            System.out.println("Приложение готово к работе");
            System.out.println("Введите 'help' для списка команд");

            consoleUI.start();

        } catch (IllegalArgumentException e) {
            System.err.println("\n Ошибка инициализации: " + e.getMessage());
            System.err.println("Проверьте правильность указания файла.");
            System.exit(2);

        } catch (SecurityException e) {
            System.err.println("\n Ошибка доступа к файлу: " + e.getMessage());
            System.err.println("Проверьте права доступа к файлу.");
            System.exit(3);

        } catch (Exception e) {
            System.err.println("\n Критическая ошибка при запуске: " + e.getMessage());
            e.printStackTrace();
            System.exit(4);
        }
    }
}