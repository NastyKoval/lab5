package client.presentation.application.command_cl;

public enum CommandTypeEnum {
    NO_ARGS,       // show, help, info
    ID_ARG,        // remove_by_id
    FLAT_ARG,      // add
    ID_AND_FLAT,   // update
    LONG_ARG,      // count_less_than_bathrooms
    HOUSE_ARG,     // filter_greater_than_house
    STRING_ARG;    // execute_script

    /**
     * Проверяет требует ли команда ID.
     */
    public boolean requiresId() {
        return this == ID_ARG || this == ID_AND_FLAT;
    }

    /**
     * Проверяет требует ли команда Flat.
     */
    public boolean requiresFlat() {
        return this == FLAT_ARG || this == ID_AND_FLAT;
    }

    /**
     * Проверяет требует ли команда Long.
     */
    public boolean requiresLong() {
        return this == LONG_ARG;
    }

    /**
     * Проверяет требует ли команда House.
     */
    public boolean requiresHouse() {
        return this == HOUSE_ARG;
    }

    /**
     * Проверяет требует ли команда String.
     */
    public boolean requiresString() {
        return this == STRING_ARG;
    }

    /**
     * Проверяет требует ли команда аргументы.
     */
    public boolean requiresArgs() {
        return this != NO_ARGS;
    }
}




