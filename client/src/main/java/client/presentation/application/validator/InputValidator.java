package client.presentation.application.validator;

import common.request.CommandType;
import common.domain.enums.Furnish;
import common.domain.enums.View;

import java.util.Map;

/**
 * Валидатор ввода пользователя на стороне клиента
 */
public class InputValidator {

    // Проверка что ввод не пустой
    public boolean validateInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    // Проверка что строка не пустая
    public boolean validateString(String value, String fieldName) {
        return value != null && !value.trim().isEmpty();
    }

    // Проверка что строка — целое число (int)
    public boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка что строка — число (double)
    public boolean isDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка что строка — (long)
    public boolean isLong(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Проверка что число больше нуля (Double)
    public boolean isPositive(Double value, String fieldName) {
        return value != null && value > 0;
    }

    // Проверка что число больше нуля (Long)
    public boolean isPositive(Long value, String fieldName) {
        return value != null && value > 0;
    }

    // Проверка что число больше нуля (Integer)
    public boolean isPositive(Integer value, String fieldName) {
        return value != null && value > 0;
    }

    // Проверка что строка — валидное значение enum View
    public boolean validateView(String view) {
        if (view == null || view.isEmpty()) {
            return false;
        }
        try {
            View.valueOf(view.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Проверка что строка — валидное значение enum Furnish
    public boolean validateFurnish(String furnish) {
        if (furnish == null || furnish.isEmpty()) {
            return false;
        }
        try {
            Furnish.valueOf(furnish.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Универсальная проверка enum
    public boolean validateEnum(String value, Class<? extends Enum> enumClass) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, value.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Проверка что аргументы соответствуют типу команды
    public boolean checkArgs(Map<String, Object> args, CommandType type) {
        if (args == null) {
            return type == CommandType.NO_ARGS;
        }

        switch (type) {
            case NO_ARGS:
                return args.isEmpty();

            case ID_ARG:
                return args.containsKey("id") &&
                        args.get("id") instanceof Integer;

            case FLAT_ARG:
                return args.containsKey("flat") &&
                        args.get("flat") != null;

            case ID_AND_FLAT:
                return args.containsKey("id") &&
                        args.containsKey("flat") &&
                        args.get("id") instanceof Integer &&
                        args.get("flat") != null;

            case LONG_ARG:
                return args.containsKey("value") &&
                        args.get("value") instanceof Long;

            case HOUSE_ARG:
                return args.containsKey("house") &&
                        args.get("house") != null;

            case STRING_ARG:
                return args.containsKey("fileName") &&
                        args.get("fileName") instanceof String &&
                        !((String) args.get("fileName")).isEmpty();

            default:
                return false;
        }
    }

    // Проверка что объект не null
    public boolean isNotNull(Object object, String fieldName) {
        return object != null;
    }

    // Проверка что число в диапазоне (Long)
    public boolean isInRange(Long value, long min, long max, String fieldName) {
        return value != null && value >= min && value <= max;
    }

    // Проверка что число в диапазоне (Integer)
    public boolean isInRange(Integer value, int min, int max, String fieldName) {
        return value != null && value >= min && value <= max;
    }
}