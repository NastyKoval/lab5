package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

import domain.model.Flat;

/**
 * Запрос на добавление если значение меньше минимального
 * Команда: add_if_min {element}
 */
public class AddIfMinRequest extends Request {

    private final Flat flat;

    public AddIfMinRequest(Flat flat) {
        super("add_if_min");
        this.flat = flat;
    }

    public Flat getFlat() {
        return flat;
    }

    @Override
    public Object getArguments() {
        return flat;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.FLAT_ARG;
    }
}