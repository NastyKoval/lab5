package common.request;

import common.domain.model.House;

/**
 * Запрос на фильтрацию по house
 * Команда: filter_greater_than_house house
 */
public class FilterGreaterThanHouseRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final House house;

    public FilterGreaterThanHouseRequest(House house) {
        super("filter_greater_than_house");
        this.house = house;
    }

    public House getHouse() {
        return house;
    }

    @Override
    public Object getArguments() {
        return house;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.HOUSE_ARG;
    }
}