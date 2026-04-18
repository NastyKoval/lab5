package client.presentation;

import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.command_cl.CommandTypeEnum;
import client.presentation.application.request.*;
import client.presentation.application.validator.InputValidator;
import server.application.command.CommandRegistryServer;
import server.application.response.Response;
import domain.model.*;
import domain.enums.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Консольный интерфейс пользователя.
 */
public class ConsoleUI {

    private final Scanner scanner;
    private final CommandRegistry registry;
    private final InputValidator validator;
    private final CommandRegistryServer server;
    private boolean running;

    /**
     * Конструктор ConsoleUI.
     */
    public ConsoleUI(CommandRegistry registry, CommandRegistryServer server, InputValidator validator) {
        this.scanner = new Scanner(System.in);
        this.registry = registry;
        this.validator = new InputValidator();
        this.server = server;
        this.running = true;
    }

    /**
     * Запускает консольное приложение.
     */
    public void start() {
        System.out.println("Добро пожаловать в приложение управления квартирами :)");
        System.out.println("Введите 'help' для получения справки по командам.");
        System.out.println();

        while (running) {
            try {
                System.out.print("$ ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                processInput(input);

            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        System.out.println("Программа завершена.");
        scanner.close();
    }

    /**
     * Обрабатывает ввод пользователя.
     */
    private void processInput(String input) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        // Проверяем существование команды
        if (!registry.hasCommand(commandName)) {
            System.err.println("Неизвестная команда: " + commandName);
            System.out.println("Введите 'help' для получения справки.");
            return;
        }

        // Создаём Request с валидацией
        Request request = createRequest(commandName, arguments);
        if (request == null) {
            return;  // Ошибка уже выведена в createRequest
        }

        // Выполняем команду на сервере
        Response response = server.execute(request);

        // Выводим результат (исправлено: getMessage(), не getOutput())
        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    /**
     * Создаёт Request объект с валидацией аргументов.
     */
    private Request createRequest(String commandName, String arguments) {
        CommandTypeEnum type = registry.getCommandType(commandName);
        Map<String, Object> args = new HashMap<>();

        // Валидация и сбор аргументов в зависимости от типа команды
        switch (type) {
            case NO_ARGS:
                // Аргументы не нужны
                break;

            case ID_ARG:
                if (!validator.isInteger(arguments)) {
                    System.err.println("ID должен быть целым числом!");
                    return null;
                }
                int id = Integer.parseInt(arguments.trim());
                if (id <= 0) {
                    System.err.println("ID должен быть больше 0!");
                    return null;
                }
                args.put("id", id);
                break;

            case FLAT_ARG:
                Flat flat = parseFlat(arguments);
                if (flat == null) {
                    return null;  // Ошибка уже выведена в parseFlat
                }
                args.put("flat", flat);
                break;

            case ID_AND_FLAT:
                String[] updateArgs = arguments.split("\\s+", 2);
                if (updateArgs.length < 2) {
                    System.err.println("Требуется ID и данные квартиры!");
                    return null;
                }
                if (!validator.isInteger(updateArgs[0])) {
                    System.err.println("ID должен быть целым числом!");
                    return null;
                }
                int updateId = Integer.parseInt(updateArgs[0].trim());
                if (updateId <= 0) {
                    System.err.println("ID должен быть больше 0!");
                    return null;
                }
                Flat updateFlat = parseFlat(updateArgs[1]);
                if (updateFlat == null) {
                    return null;
                }
                args.put("id", updateId);
                args.put("flat", updateFlat);
                break;

            case LONG_ARG:
                if (!validator.isLong(arguments)) {
                    System.err.println("Значение должно быть числом!");
                    return null;
                }
                long value = Long.parseLong(arguments.trim());
                args.put("value", value);
                break;

            case HOUSE_ARG:
                House house = parseHouse(arguments);
                if (house == null) {
                    return null;
                }
                args.put("house", house);
                break;

            case STRING_ARG:
                if (!validator.validateString(arguments, "Имя файла")) {
                    System.err.println("Имя файла не может быть пустым!");
                    return null;
                }
                args.put("fileName", arguments.trim());
                break;
        }

        // Финальная проверка аргументов
        if (!validator.checkArgs(args, type)) {
            System.err.println("Неверные аргументы для команды: " + commandName);
            return null;
        }

        // Создаём и возвращаем Request через реестр
        return registry.buildRequest(commandName, args);
    }

    /**
     * Парсит строку в объект Flat.
     */
    private Flat parseFlat(String arguments) {
        try {
            String[] args = arguments.split("\\s+");
            if (args.length < 8) {  // Минимум: name area x y [rooms] [bathrooms] furnish view
                System.err.println("Недостаточно данных для квартиры!");
                System.out.println("Пример: Название 50.5 10 20.5 2 1 FINE GOOD");
                return null;
            }

            String name = args[0];
            if (!validator.validateString(name, "Название")) {
                System.err.println("Название не может быть пустым!");
                return null;
            }

            double area = Double.parseDouble(args[1]);
            if (!validator.isPositive(area, "Площадь")) {
                System.err.println("Площадь должна быть больше 0!");
                return null;
            }

            int x = Integer.parseInt(args[2]);
            float y = Float.parseFloat(args[3]);
            Coordinates coordinates = new Coordinates(x, y);

            Integer numberOfRooms = null;
            if (args.length > 4 && !args[4].equals("null")) {
                numberOfRooms = Integer.parseInt(args[4]);
                if (!validator.isPositive(numberOfRooms, "Комнаты")) {
                    System.err.println("Количество комнат должно быть больше 0!");
                    return null;
                }
            }

            Long numberOfBathrooms = null;
            if (args.length > 5 && !args[5].equals("null")) {
                numberOfBathrooms = Long.parseLong(args[5]);
                if (!validator.isPositive(numberOfBathrooms, "Ванные")) {
                    System.err.println("Количество ванных должно быть больше 0!");
                    return null;
                }
            }

            Furnish furnish = null;
            if (args.length > 6) {
                if (!validator.validateFurnish(args[6])) {
                    System.err.println("Неверное значение Furnish! Доступные: FINE, BAD, LITTLE, DESIGNER, NONE");
                    return null;
                }
                furnish = Furnish.valueOf(args[6].toUpperCase());
            }

            View view = View.GOOD;  // default
            if (args.length > 7) {
                if (!validator.validateView(args[7])) {
                    System.err.println("Неверное значение View! Доступные: GOOD, BAD, TERRIBLE");
                    return null;
                }
                view = View.valueOf(args[7].toUpperCase());
            }

            // Парсинг House (опционально)
            House house = null;
            if (args.length > 11) {
                house = parseHouse(String.join(" ", java.util.Arrays.copyOfRange(args, 8, args.length)));
            }

            return new Flat(name, coordinates, area, numberOfRooms, numberOfBathrooms, furnish, view, house);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    /**
     * Парсит строку в объект House.
     */
    private House parseHouse(String arguments) {
        try {
            String[] args = arguments.split("\\s+");
            if (args.length < 4) {
                System.err.println("Недостаточно данных для дома!");
                System.out.println("Пример: Дом 2000 10 4 2");
                return null;
            }

            String name = args[0];
            if (!validator.validateString(name, "Название дома")) {
                System.err.println("Название дома не может быть пустым!");
                return null;
            }

            Long year = Long.parseLong(args[1]);
            if (!validator.isInRange(year, 1, 774, "Год")) {
                System.err.println("Год должен быть от 1 до 774!");
                return null;
            }

            Long numberOfFloors = Long.parseLong(args[2]);
            if (!validator.isInRange(numberOfFloors, 1, 64, "Этажи")) {
                System.err.println("Количество этажей должно быть от 1 до 64!");
                return null;
            }

            Integer numberOfFlatsOnFloor = Integer.parseInt(args[3]);
            if (!validator.isPositive(numberOfFlatsOnFloor, "Квартир на этаже")) {
                System.err.println("Количество квартир на этаже должно быть больше 0!");
                return null;
            }

            Long numberOfLifts = null;
            if (args.length > 4) {
                numberOfLifts = Long.parseLong(args[4]);
                if (!validator.isPositive(numberOfLifts, "Лифты")) {
                    System.err.println("Количество лифтов должно быть больше 0!");
                    return null;
                }
            }

            return new House(name, year, numberOfFloors, numberOfFlatsOnFloor, numberOfLifts);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа: " + e.getMessage());
            return null;
        }
    }

    /**
     * Останавливает приложение.
     */
    public void stop() {
        running = false;
    }
}