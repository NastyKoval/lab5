package common.util;

public class Validator {

    public static final long MIN_YEAR = 1;
    public static final long MAX_YEAR = 774;
    public static final long MIN_FLOORS = 1;
    public static final long MAX_FLOORS = 64;


    //value строка для проверки
    //fieldName имя поля (для сообщения об ошибке)

    public static void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
    }

    public static void validatePositive(Long value, String fieldName) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(fieldName + " должно быть больше 0");
        }
    }

    public static void validatePositive(Double value, String fieldName) {
        if (value != null && value <=0) {
            throw  new IllegalArgumentException( fieldName + " должно быть больше 0");
        }
    }

    public static void validatePositive(Integer value, String fieldName) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(fieldName + " должно быть больше 0");
        }
    }

    public static void validateInRange(Long value, long min, long max, String fieldName) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(
                    fieldName + " должно быть от " + min + " до " + max
            );
        }
    }

    public static void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " не может быть null");
        }
    }

    public static void validateYear(Long year) {
        validateInRange(year, MIN_YEAR, MAX_YEAR, "Год постройки");
    }

    public static void validateFloors(Long floors) {
        validateInRange(floors, MIN_FLOORS, MAX_FLOORS, "Количество этажей");
    }

    public static void validateId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(id + " должен быть больше 0");
        }
    }
}