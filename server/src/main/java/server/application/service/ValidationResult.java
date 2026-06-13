package server.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Результат валидации аргументов запроса.
 *
 * <p>Содержит:</p>
 * <ul>
 *   <li>Статус валидации (успех/ошибка)</li>
 *   <li>Список ошибок (если есть)</li>
 * </ul>
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    /**
     * Конструктор.
     *
     * @param valid true если валидация прошла успешно
     * @param errors список ошибок (пустой если valid=true)
     */
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    /**
     * Проверяет успешна ли валидация.
     *
     * @return true если нет ошибок
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Получает список всех ошибок.
     *
     * @return неизменяемый список ошибок
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Получает все ошибки в виде одной строки.
     *
     * @return строку с ошибками, разделёнными точкой с запятой
     *
     * <p>Пример: "Поле не может быть пустым; Площадь должна быть больше 0"</p>
     */
    public String getErrorMessage() {
        if (errors.isEmpty()) {
            return "";
        }
        return String.join("; ", errors);
    }

    /**
     * Добавляет ошибку в список.
     *
     * @param error текст ошибки
     */
    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errors.add(error);
        }
    }

    /**
     * Создаёт успешный результат валидации.
     *
     * @return ValidationResult с valid=true и пустым списком ошибок
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Создаёт неудачный результат с одной ошибкой.
     *
     * @param error текст ошибки
     * @return ValidationResult с valid=false и одной ошибкой
     */
    public static ValidationResult fail(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors);
    }

    /**
     * Создаёт неудачный результат со списком ошибок.
     *
     * @param errors список ошибок
     * @return ValidationResult с valid=false и списком ошибок
     */
    public static ValidationResult fail(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Проверяет содержит ли результат ошибки.
     *
     * @return true если есть хотя бы одна ошибка
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Возвращает количество ошибок.
     *
     * @return количество ошибок
     */
    public int getErrorCount() {
        return errors.size();
    }

    @Override
    public String toString() {
        if (valid) {
            return "ValidationResult{valid=true}";
        } else {
            return "ValidationResult{valid=false, errors=" + errors + "}";
        }
    }
}