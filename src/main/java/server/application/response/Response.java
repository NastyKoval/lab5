package server.application.response;

import domain.model.Flat;
import java.util.List;

/**
 * Ответ сервера на запрос.
 */
public class Response {

    private final boolean success;      // статус: успех/ошибка
    private final String message;       // текстовое сообщение
    private final Object data;          // любые дополнительные данные
    private final List<Flat> flats;     // список квартир

    // Конструктор
    public Response(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        // если data это список квартир то сохраняем отдельно для удобства
        this.flats = (data instanceof List) ? (List<Flat>) data : null;
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public List<Flat> getFlats() { return flats; }
}