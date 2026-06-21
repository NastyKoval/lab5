package common.request;

public enum CommandType {
    // Публичные команды (не требуют авторизации)
    LOGIN,
    REGISTER,
    HELP,
    EXIT,

    // Команды без параметров
    INFO,
    SHOW,
    CLEAR,
    SAVE,
    REMOVE_FIRST,
    REMOVE_HEAD,
    PRINT_FIELD_DESCENDING_FURNISH,

    // Команды с простыми аргументами (ID, числа, строки)
    REMOVE_BY_ID,
    COUNT_LESS_THAN_NUMBER_OF_BATHROOMS,
    EXECUTE_SCRIPT,
    FILTER_LESS_THAN_FURNISH,

    // Команды с объектом (Flat, House)
    ADD,
    ADD_IF_MIN,
    FILTER_GREATER_THAN_HOUSE,

    // Команда с аргументами и объектом
    UPDATE;

    public boolean requiresData() {
        return this == ADD || this == ADD_IF_MIN || this == FILTER_GREATER_THAN_HOUSE || this == UPDATE;
    }

    public boolean requiresArguments() {
        return this == REMOVE_BY_ID ||
                this == COUNT_LESS_THAN_NUMBER_OF_BATHROOMS ||
                this == EXECUTE_SCRIPT ||
                this == UPDATE ||
                this == FILTER_LESS_THAN_FURNISH;
    }

    public boolean isPublic() {
        return this == LOGIN || this == REGISTER || this == HELP || this == EXIT;
    }

    public static CommandType fromString(String name) {
        for (CommandType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестная команда: " + name);
    }
}