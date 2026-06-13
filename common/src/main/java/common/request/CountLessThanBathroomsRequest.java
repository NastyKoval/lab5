package common.request;

/**
 * Запрос на подсчёт элементов с меньшим numberOfBathrooms
 * Команда: count_less_than_number_of_bathrooms numberOfBathrooms
 */
public class CountLessThanBathroomsRequest extends Request {

    private static final long serialVersionUID = 1L;
    private final long numberOfBathrooms;

    public CountLessThanBathroomsRequest(long numberOfBathrooms) {
        super("count_less_than_number_of_bathrooms");
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public long getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    @Override
    public Object getArguments() {
        return numberOfBathrooms;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.LONG_ARG;
    }
}