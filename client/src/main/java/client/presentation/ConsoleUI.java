package client.presentation;

import client.network.UdpClient;
import client.parser.CommandStringParser;
import client.parser.ParseException;
import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.validator.InputValidator;
import common.domain.enums.Furnish;
import common.domain.enums.View;
import common.domain.model.Coordinates;
import common.domain.model.Flat;
import common.domain.model.House;
import common.domain.model.User;
import common.request.CommandType;
import common.request.Request;
import common.response.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class ConsoleUI {

    private String currentLogin = "";
    private String currentPassword = "";
    private User currentUser = null;
    private boolean isAuthenticated = false;
    private final Scanner scanner;
    private final CommandRegistry registry;
    private final InputValidator validator;
    private final CommandStringParser commandParser;
    private boolean running;
    private int scriptDepth = 0;
    private static final int MAX_SCRIPT_DEPTH = 5;

    public ConsoleUI(CommandRegistry registry, InputValidator validator) {
        this.scanner = new Scanner(System.in);
        this.registry = registry;
        this.validator = validator;
        this.commandParser = new CommandStringParser(validator);
        this.running = true;
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

            } catch (Exception e) {
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

        // Клиентские команды
        if (commandName.equals("login") || commandName.equals("register")) {
            handleAuth(commandName, arguments);
            return;
        }

        if (commandName.equals("help")) {
            printHelp();
            return;
        }

        if (commandName.equals("execute_script")) {
            executeScriptFile(arguments);
            return;
        }

        if (commandName.equals("exit")) {
            running = false;
            return;
        }

        // Интерактивный ввод для add/update
        if (commandName.equals("add") || commandName.equals("add_if_min")) {
            if (isScriptMode) {
                handleFlatCommand(commandName, arguments);
            } else {
                handleInteractiveFlatCommand(commandName);
            }
            return;
        }

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

        Response response = UdpClient.send(request);
        printResponse(response);
    }

    private void printHelp() {
        Request request = new Request(CommandType.HELP, null, null, currentUser);
        Response response = UdpClient.send(request);

        if (response != null && response.isSuccess()) {
            System.out.println("Серверные команды");
            if (response.getData() != null) {
                System.out.println(response.getData());
            } else if (response.getMessage() != null) {
                System.out.println(response.getMessage());
            }
        }

        System.out.println("\nКлиентские команды");
        System.out.println("execute_script <file> - выполнить скрипт из файла");
        System.out.println("exit - завершить работу клиента");
        System.out.println("help - показать эту справку");
    }

    private void handleAuth(String commandName, String arguments) {
        String[] parts = arguments.split("\\s+", 2);
        if (parts.length < 2) {
            System.err.println("Использование: " + commandName + " <login> <password>");
            return;
        }

        String login = parts[0];
        String password = parts[1];

        CommandType type = commandName.equals("login") ? CommandType.LOGIN : CommandType.REGISTER;
        Request request = new Request(
                type,
                new String[]{login, password},
                null,
                null
        );

        Response response = UdpClient.send(request);

        if (response != null && response.isSuccess()) {
            if (response.getData() instanceof User) {
                this.currentUser = (User) response.getData();
                this.currentLogin = login;
                this.currentPassword = password;
                this.isAuthenticated = true;
                System.out.println("Авторизация успешна: " + login);
            } else {
                this.currentLogin = login;
                this.currentPassword = password;
                this.isAuthenticated = true;
                System.out.println(response.getMessage() != null ? response.getMessage() : "Авторизация успешна");
            }
        } else {
            System.err.println(response != null ? response.getMessage() : "Ошибка авторизации");
            this.isAuthenticated = false;
        }
    }

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

        System.out.println("Выполнение скрипта: " + fileName);
        scriptDepth++;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null && running) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                System.out.println("[" + lineNumber + "] " + line);
                processInput(line, true);
            }

            System.out.println("Скрипт завершён");

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } finally {
            scriptDepth--;
        }
    }

    private Request createRequest(String commandName, String arguments) {
        try {
            CommandType type = CommandType.fromString(commandName.toUpperCase());

            String[] argsArray = null;
            Object data = null;

            if (type.requiresArguments() && !arguments.isEmpty()) {
                argsArray = new String[]{arguments};
            }

            if (type.requiresData()) {
                data = commandParser.parseData(arguments, type);
            }

            return new Request(type, argsArray, data, currentUser);

        } catch (Exception e) {
            System.err.println("Ошибка создания запроса: " + e.getMessage());
            return null;
        }
    }

    private void handleFlatCommand(String commandName, String arguments) {
        Request request = createRequest(commandName, arguments);
        if (request == null) {
            return;
        }

        Response response = UdpClient.send(request);
        printResponse(response);
    }

    private void handleInteractiveFlatCommand(String commandName) {
        System.out.println("Команда: " + commandName);
        Flat flat = readFlatInteractively();

        if (flat == null) {
            System.err.println("Ввод прерван");
            return;
        }

        CommandType type = CommandType.fromString(commandName.toUpperCase());
        Request request = new Request(type, null, flat, currentUser);

        Response response = UdpClient.send(request);
        printResponse(response);
    }

    private void handleUpdateCommand(String arguments) {
        Request request = createRequest("update", arguments);
        if (request == null) {
            return;
        }

        Response response = UdpClient.send(request);
        printResponse(response);
    }

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

        Request request = new Request(
                CommandType.UPDATE,
                new String[]{String.valueOf(id)},
                flat,
                currentUser
        );

        Response response = UdpClient.send(request);
        printResponse(response);
    }

    private Flat readFlatInteractively() {
        try {
            System.out.print("Название: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.err.println("Название не может быть пустым");
                return null;
            }

            System.out.print("Площадь: ");
            double area = Double.parseDouble(scanner.nextLine().trim());
            if (area <= 0) {
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
            if (numberOfRooms != null && numberOfRooms <= 0) {
                System.err.println("Количество комнат должно быть больше 0");
                return null;
            }

            System.out.print("Количество ванных: ");
            String bathsInput = scanner.nextLine().trim();
            Long numberOfBathrooms = bathsInput.isEmpty() ? null : Long.parseLong(bathsInput);
            if (numberOfBathrooms != null && numberOfBathrooms <= 0) {
                System.err.println("Количество ванных должно быть больше 0");
                return null;
            }

            System.out.print("Furnish (FINE/BAD/LITTLE/DESIGNER/NONE): ");
            String furnishStr = scanner.nextLine().trim().toUpperCase();
            Furnish furnish = Furnish.valueOf(furnishStr);

            System.out.print("View (GOOD/BAD/TERRIBLE): ");
            String viewStr = scanner.nextLine().trim().toUpperCase();
            View view = View.valueOf(viewStr);

            House house = null;
            System.out.print("Название дома (или Enter для пропуска): ");
            String houseName = scanner.nextLine().trim();

            if (!houseName.isEmpty()) {
                System.out.print("Год постройки (1-774): ");
                Long year = Long.parseLong(scanner.nextLine().trim());
                if (year < 1 || year > 774) {
                    System.err.println("Год должен быть от 1 до 774");
                    return null;
                }

                System.out.print("Этажи (1-64): ");
                Long floors = Long.parseLong(scanner.nextLine().trim());
                if (floors < 1 || floors > 64) {
                    System.err.println("Этажи должны быть от 1 до 64");
                    return null;
                }

                System.out.print("Квартир на этаже: ");
                Integer flatsOnFloor = Integer.parseInt(scanner.nextLine().trim());
                if (flatsOnFloor <= 0) {
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

    private void printResponse(Response response) {
        if (response == null) {
            System.err.println("Нет ответа от сервера");
            return;
        }

        if (response.isSuccess()) {
            if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                System.out.println(response.getMessage());
            }
            if (response.getData() != null) {
                if (response.getData() instanceof Collection) {
                    ((Collection<?>) response.getData()).forEach(System.out::println);
                } else {
                    System.out.println(response.getData());
                }
            }
        } else {
            System.err.println(response.getMessage() != null ? response.getMessage() : "Ошибка");
        }
    }

    public void stop() {
        running = false;
    }
}