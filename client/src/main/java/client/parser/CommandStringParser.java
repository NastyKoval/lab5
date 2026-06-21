package client.parser;

import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.validator.InputValidator;
import common.domain.enums.Furnish;
import common.domain.enums.View;
import common.domain.model.Coordinates;
import common.domain.model.Flat;
import common.domain.model.House;
import common.request.CommandType;
import common.request.Request;

import java.io.Serializable;
import java.util.Arrays;

public class CommandStringParser {

    private final InputValidator validator;

    public CommandStringParser(InputValidator validator) {
        this.validator = validator;
    }

    public Request parseCommand(String commandName, String arguments, CommandRegistry registry) throws ParseException {
        CommandType type = registry.getCommandType(commandName);

        // Определяем простые аргументы и сложные данные
        String[] argsArray = null;
        Serializable data = null;

        // Обрабатываем в зависимости от типа команды
        if (type.requiresArguments()) {
            // Простые аргументы (числа, строки) → в массив
            if (arguments != null && !arguments.trim().isEmpty()) {
                argsArray = new String[]{arguments.trim()};
            }
        }

        if (type.requiresData()) {
            // Сложные объекты (Flat, House) → парсим и кладём в data
            data = parseData(arguments, type);
        }

        // Создаём Request в новом формате
        return new Request(type, argsArray, data, null); // User подставится в ConsoleUI
    }

    private Flat parseFlat(String arguments) throws ParseException {
        String[] args = arguments.split("\\s+");
        if (args.length < 8) {
            throw new ParseException("Недостаточно данных для квартиры");
        }

        String name = args[0];
        if (!validator.validateString(name, "Название")) {
            throw new ParseException("Название не может быть пустым");
        }

        double area = Double.parseDouble(args[1]);
        if (!validator.isPositive(area, "Площадь")) {
            throw new ParseException("Площадь должна быть больше 0");
        }

        int x = Integer.parseInt(args[2]);
        float y = Float.parseFloat(args[3]);
        Coordinates coordinates = new Coordinates(x, y);

        Integer numberOfRooms = null;
        if (args.length > 4 && !args[4].equals("null")) {
            numberOfRooms = Integer.parseInt(args[4]);
            if (!validator.isPositive(numberOfRooms, "Комнаты")) {
                throw new ParseException("Количество комнат должно быть больше 0");
            }
        }

        Long numberOfBathrooms = null;
        if (args.length > 5 && !args[5].equals("null")) {
            numberOfBathrooms = Long.parseLong(args[5]);
            if (!validator.isPositive(numberOfBathrooms, "Ванные")) {
                throw new ParseException("Количество ванных должно быть больше 0");
            }
        }

        Furnish furnish = null;
        if (args.length > 6) {
            if (!validator.validateFurnish(args[6])) {
                throw new ParseException("Неверное значение Furnish");
            }
            furnish = Furnish.valueOf(args[6].toUpperCase());
        }

        View view = View.GOOD;
        if (args.length > 7) {
            if (!validator.validateView(args[7])) {
                throw new ParseException("Неверное значение View");
            }
            view = View.valueOf(args[7].toUpperCase());
        }

        House house = null;
        if (args.length > 11) {
            house = parseHouse(String.join(" ", Arrays.copyOfRange(args, 8, args.length)));
        }

        return new Flat(name, coordinates, area, numberOfRooms, numberOfBathrooms, furnish, view, house);
    }

    private House parseHouse(String arguments) throws ParseException {
        String[] args = arguments.split("\\s+");
        if (args.length < 4) {
            throw new ParseException("Недостаточно данных для дома");
        }

        String name = args[0];
        if (!validator.validateString(name, "Название дома")) {
            throw new ParseException("Название дома не может быть пустым");
        }

        Long year = Long.parseLong(args[1]);
        if (!validator.isInRange(year, 1, 774, "Год")) {
            throw new ParseException("Год должен быть от 1 до 774");
        }

        Long numberOfFloors = Long.parseLong(args[2]);
        if (!validator.isInRange(numberOfFloors, 1, 64, "Этажи")) {
            throw new ParseException("Количество этажей должно быть от 1 до 64");
        }

        Integer numberOfFlatsOnFloor = Integer.parseInt(args[3]);
        if (!validator.isPositive(numberOfFlatsOnFloor, "Квартир на этаже")) {
            throw new ParseException("Количество квартир на этаже должно быть больше 0");
        }

        Long numberOfLifts = null;
        if (args.length > 4) {
            numberOfLifts = Long.parseLong(args[4]);
            if (!validator.isPositive(numberOfLifts, "Лифты")) {
                throw new ParseException("Количество лифтов должно быть больше 0");
            }
        }

        return new House(name, year, numberOfFloors, numberOfFlatsOnFloor, numberOfLifts);
    }

    private Integer parseInteger(String value, String fieldName) throws ParseException {
        try {
            int result = Integer.parseInt(value.trim());
            if (result <= 0) {
                throw new ParseException(fieldName + " должен быть больше 0");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new ParseException(fieldName + " должен быть целым числом");
        }
    }

    private Long parseLong(String value, String fieldName) throws ParseException {
        try {
            long result = Long.parseLong(value.trim());
            if (result <= 0) {
                throw new ParseException(fieldName + " должен быть больше 0");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new ParseException(fieldName + " должен быть числом");
        }
    }

    /**
     * Парсит данные для команды в зависимости от типа.
     * Возвращает Flat, House, Integer, Long или null.
     */
    public Serializable parseData(String input, CommandType type) throws ParseException {
        return switch (type) {
            case ADD, ADD_IF_MIN, UPDATE -> parseFlat(input);
            case FILTER_GREATER_THAN_HOUSE -> parseHouse(input);
            case REMOVE_BY_ID -> parseInteger(input, "ID");
            case COUNT_LESS_THAN_NUMBER_OF_BATHROOMS -> parseLong(input, "Значение");
            default -> null;
        };
    }
}