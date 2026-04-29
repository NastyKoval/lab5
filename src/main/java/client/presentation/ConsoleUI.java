package client.presentation;

import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.command_cl.CommandTypeEnum;
import client.presentation.application.request.HelpRequest;
import client.presentation.application.request.Request;
import client.presentation.application.validator.InputValidator;
import domain.parser.CommandStringParser;
import domain.parser.ParseException;
import server.application.command.CommandRegistryServer;
import server.application.response.Response;
import domain.model.*;
import domain.enums.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;
    private final CommandRegistry registry;
    private final InputValidator validator;
    private final CommandRegistryServer server;
    private final CommandStringParser commandParser;
    private boolean running;
    private int scriptDepth = 0; // Защита от рекурсии
    private static final int MAX_SCRIPT_DEPTH = 5;

    public ConsoleUI(CommandRegistry registry, CommandRegistryServer server, InputValidator validator) {
        this.scanner = new Scanner(System.in);
        this.registry = registry;
        this.validator = validator;
        this.server = server;
        this.commandParser = new CommandStringParser(validator);
        this.running = true; // для выхода из бесконечного цикла
    }

    public void start() {
        System.out.println("Добро пожаловать в приложение управления квартирами");
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

            } catch (NoSuchElementException e) {
                System.out.println("\nПолучен сигнал завершения (Ctrl+D)");
                running = false;
            }
            catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        System.out.println("Программа завершена.");
        scanner.close();
    }

    private void processInput(String input) {
        processInput(input, false);
    }

    private void processInput(String input, boolean isScriptMode) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        if (!registry.hasCommand(commandName)) {
            System.err.println("Неизвестная команда: " + commandName);
            System.out.println("Введите 'help' для получения справки.");
            return;
        }

        // Обработка help - добавляем клиентские команды
        if (commandName.equals("help")) {
            printHelp();
            return;
        }

        // Обработка execute_script
        if (commandName.equals("execute_script")) {
            executeScriptFile(arguments);
            return;
        }

        // Обработка exit
        if (commandName.equals("exit")) {
            running = false;
            return;
        }

        // Обработка add / add_if_min
        if (commandName.equals("add") || commandName.equals("add_if_min")) {
            if (isScriptMode) {
                handleFlatCommand(commandName, arguments);
            } else {
                handleInteractiveFlatCommand(commandName);
            }
            return;
        }

        // Обработка update
        if (commandName.equals("update")) {
            if (isScriptMode) {
                handleUpdateCommand(arguments);
            } else {
                handleInteractiveUpdateCommand(arguments);
            }
            return;
        }

        // Остальные команды
        Request request = createRequest(commandName, arguments);
        if (request == null) {
            return;
        }

        Response response = server.execute(request);
        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    // Вывод справки с серверными и клиентскими командами
    private void printHelp() {
        // Серверные команды
        Request helpRequest = new HelpRequest();
        Response response = server.execute(helpRequest);
        if (response != null && response.isSuccess()) {
            System.out.println("Серверные команды:");
            System.out.println(response.getMessage());
        }

        // Клиентские команды
        System.out.println("Клиентские команды:");
        System.out.println("execute_script {file} - выполнить скрипт из файла");
    }

    // Выполняет команды из файла
    private void executeScriptFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.err.println("Укажите имя файла: execute_script <filename>");
            return;
        }

        fileName = fileName.trim();

        if (scriptDepth >= MAX_SCRIPT_DEPTH) {
            System.err.println("Превышена максимальная вложенность скриптов");
            return;
        }

        java.io.File file = new java.io.File(fileName);
        if (!file.exists()) {
            System.err.println("Файл не найден: " + fileName);
            return;
        }

        /*if (!file.canRead()) {
            System.err.println("Нет прав на чтение файла: " + fileName);
            return;
        }*/

        // Открываем файл
        System.out.println("Выполнение скрипта: " + fileName);
        scriptDepth++;
        // Читаем пострончо
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            // Читаем одну строку за раз
            while ((line = reader.readLine()) != null && running) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                System.out.println("[" + lineNumber + "] " + line);
                processInput(line, true);// Выполняем команду
            }

            System.out.println("Скрипт завершён");

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } finally {
            scriptDepth--; // Защищаем от рекурсии
        }
    }

    // Создаёт Request через парсер
    private Request createRequest(String commandName, String arguments) {
        try {
            return commandParser.parseCommand(commandName, arguments, registry);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    // Обработка add / add_if_min в скрипте
    private void handleFlatCommand(String commandName, String arguments) {
        Request request = createRequest(commandName, arguments);
        if (request == null) {
            return;
        }

        Response response = server.execute(request);
        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    // Обработка add / add_if_min в интерактивном режиме
    private void handleInteractiveFlatCommand(String commandName) {
        System.out.println("Команда: " + commandName);
        Flat flat = readFlatInteractively();// Спрашиваем данные квартиры
        if (flat == null) {
            System.err.println("Ввод прерван");
            return;
        }
        // Создаем Request
        Map<String, Object> args = new HashMap<>();
        args.put("flat", flat);

        Request request = registry.buildRequest(commandName, args);
        Response response = server.execute(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    // Обработка update в скрипте
    private void handleUpdateCommand(String arguments) {
        Request request = createRequest("update", arguments);
        if (request == null) {
            return;
        }

        Response response = server.execute(request);
        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    // Обработка update в интерактивном режиме
    private void handleInteractiveUpdateCommand(String arguments) {
        if (arguments.trim().isEmpty()) {
            System.err.println("Укажите ID: update <id>");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(arguments.trim());
            if (id <= 0) {
                System.err.println("ID должен быть больше 0");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("ID должен быть числом");
            return;
        }

        System.out.println("Обновление квартиры с ID=" + id);
        Flat flat = readFlatInteractively();

        if (flat == null) {
            System.err.println("Ввод прерван");
            return;
        }

        Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        args.put("flat", flat);

        Request request = registry.buildRequest("update", args);
        Response response = server.execute(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println(response.getMessage());
            }
        }
    }

    // Интерактивный ввод данных квартиры
    private Flat readFlatInteractively() {
        try {
            System.out.print("Название: ");
            String name = scanner.nextLine().trim();
            if (!validator.validateString(name, "Название")) {
                System.err.println("Название не может быть пустым");
                return null;
            }

            System.out.print("Площадь: ");
            double area = Double.parseDouble(scanner.nextLine().trim());
            if (!validator.isPositive(area, "Площадь")) {
                System.err.println("Площадь должна быть больше 0");
                return null;
            }

            System.out.print("Координата X: ");
            int x = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Координата Y: ");
            float y = Float.parseFloat(scanner.nextLine().trim());
            Coordinates coordinates = new Coordinates(x, y);

            System.out.print("Количество комнат (или нажмите Enter): ");
            String roomsInput = scanner.nextLine().trim();
            Integer numberOfRooms = roomsInput.isEmpty() ? null : Integer.parseInt(roomsInput);
            if (numberOfRooms != null && !validator.isPositive(numberOfRooms, "Комнаты")) {
                System.err.println("Количество комнат должно быть больше 0");
                return null;
            }

            System.out.print("Количество ванных: ");
            String bathsInput = scanner.nextLine().trim();
            Long numberOfBathrooms = bathsInput.isEmpty() ? null : Long.parseLong(bathsInput);
            if (numberOfBathrooms != null && !validator.isPositive(numberOfBathrooms, "Ванные")) {
                System.err.println("Количество ванных должно быть больше 0");
                return null;
            }

            System.out.print("Furnish (FINE/BAD/LITTLE/DESIGNER/NONE): ");
            String furnishStr = scanner.nextLine().trim().toUpperCase();
            if (!validator.validateFurnish(furnishStr)) {
                System.err.println("Неверное значение Furnish");
                return null;
            }
            Furnish furnish = Furnish.valueOf(furnishStr);

            System.out.print("View (GOOD/BAD/TERRIBLE): ");
            String viewStr = scanner.nextLine().trim().toUpperCase();
            if (!validator.validateView(viewStr)) {
                System.err.println("Неверное значение View");
                return null;
            }
            View view = View.valueOf(viewStr);

            House house = null;
            System.out.print("Название дома (или Enter для пропуска): ");
            String houseName = scanner.nextLine().trim();

            if (!houseName.isEmpty()) {
                System.out.print("Год постройки (1-774): ");
                Long year = Long.parseLong(scanner.nextLine().trim());
                if (!validator.isInRange(year, 1, 774, "Год")) {
                    System.err.println("Год должен быть от 1 до 774");
                    return null;
                }

                System.out.print("Этажи (1-64): ");
                Long floors = Long.parseLong(scanner.nextLine().trim());
                if (!validator.isInRange(floors, 1, 64, "Этажи")) {
                    System.err.println("Этажи должны быть от 1 до 64");
                    return null;
                }

                System.out.print("Квартир на этаже: ");
                Integer flatsOnFloor = Integer.parseInt(scanner.nextLine().trim());
                if (!validator.isPositive(flatsOnFloor, "Квартир на этаже")) {
                    System.err.println("Должно быть больше 0");
                    return null;
                }

                System.out.print("Лифтов (или Enter): ");
                String liftsInput = scanner.nextLine().trim();
                Long lifts = liftsInput.isEmpty() ? null : Long.parseLong(liftsInput);

                house = new House(houseName, year, floors, flatsOnFloor, lifts);
            }

            return new Flat(name, coordinates, area, numberOfRooms, numberOfBathrooms, furnish, view, house);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка числа: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return null;
        }
    }

    public void stop() {
        running = false;
    }
}