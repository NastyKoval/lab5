package server.application.service;

import common.request.CommandType;
import common.request.Request;
import server.infrastructure.repository.FlatRepository;
import common.domain.model.Flat;
import common.domain.model.House;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с квартирами (бизнес-логика)
 */
public class FlatService {

    private final FlatRepository repository;

    public FlatService(FlatRepository repository) {
        this.repository = repository;
        // вместо nextId  - id теперь генерирует sequence в бд
    }

    // CRUD Операции

    /**
     * Добавляет новую квартиру.
     */
    public void addFlat(Flat flat, int ownerId) {
        validateFlat(flat);
        flat.setOwnerId(ownerId);
        repository.save(flat);
    }

    /**
     * Добавляет квартиру только если она меньше минимальной.
     */
    public boolean addIfMin(Flat flat, int ownerId) {
        validateFlat(flat);

        if (repository.findAll().isEmpty()) {
            flat.setOwnerId(ownerId);
            repository.save(flat);
            return true;
        }

        Flat minFlat = repository.findAll().stream()
                .min(Flat::compareTo)
                .orElseThrow();

        if (flat.compareTo(minFlat) < 0) {
            flat.setOwnerId(ownerId);
            repository.save(flat);
            return true;
        }

        return false;
    }

    /**
     * Удаляет квартиру по ID. Удалить может только владелец квартиры.
     */
    public boolean removeById(int id, int requesterId) {
        Optional<Flat> flatOpt = repository.findById(id);
        if (flatOpt.isEmpty()) {
            return false;
        }

        Flat flat = flatOpt.get();
        checkOwnership(flat, requesterId);

        repository.deleteById(id);
        return true;
    }

    /**
     * Обновляет квартиру по ID. Обновить может только владелец квартиры.
     */
    public boolean updateFlat(int id, Flat newFlat, int requesterId) {
        Optional<Flat> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }

        Flat existing = existingOpt.get();
        checkOwnership(existing, requesterId);

        validateFlat(newFlat);
        newFlat.setId(id);
        newFlat.setOwnerId(existing.getOwnerId()); // владелец не меняется
        repository.update(id, newFlat);
        return true;
    }

    // Проверяет, что текущий пользователь - владелец этого объекта
    private void checkOwnership(Flat flat, int requesterId) {
        if (flat.getOwnerId() == null || flat.getOwnerId() != requesterId) {
            throw new SecurityException(
                    "Изменять можно только свои объекты. Владелец: " + flat.getOwnerId()
            );
        }
    }

    /**
     * Возвращает все квартиры.
     */
    public List<Flat> getAllFlats() {
        return repository.findAll();
    }

    /**
     * Возвращает информацию о коллекции.
     */
    public String getInfo() {
        return "Тип: " + repository.getClass().getSimpleName() +
                "\nКоличество элементов: " + repository.size() +
                "\nДата инициализации: " + java.time.LocalDateTime.now();
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        repository.clear();
    }

    //  СПЕЦИАЛЬНЫЕ МЕТОДЫ

    /**
     * Удаляет и возвращает первый элемент.
     */
    public Optional<Flat> removeHead() {
        List<Flat> flats = repository.findAll();

        if (flats.isEmpty()) {
            return Optional.empty();
        }

        Flat first = flats.get(0);
        repository.deleteById(first.getId());

        return Optional.of(first);
    }

    /**
     * Удаляет первый элемент (возвращает boolean).
     */
    public boolean removeFirst() {
        List<Flat> flats = repository.findAll();

        if (flats.isEmpty()) {
            return false;
        }

        Flat first = flats.get(0);
        repository.deleteById(first.getId());
        return true;
    }

    /**
     * Подсчитывает количество квартир с numberOfBathrooms меньше заданного.
     */
    public long countLessThanBathrooms(Long threshold) {
        return repository.findAll().stream()
                .filter(flat -> flat.getNumberOfBathrooms() != null)
                .filter(flat -> flat.getNumberOfBathrooms() < threshold)
                .count();
    }

    /**
     * Фильтрует квартиры по house.
     */
    public List<Flat> filterGreaterThanHouse(House house) {
        return repository.findAll().stream()
                .filter(flat -> flat.getHouse() != null)
                .filter(flat -> flat.getHouse().compareTo(house) > 0)
                .toList();
    }

    /**
     * Выводит значения поля furnish в порядке убывания.
     */
    public List<String> printFieldDescendingFurnish() {
        return repository.findAll().stream()
                .map(Flat::getFurnish)
                .sorted((f1, f2) -> f2.compareTo(f1))
                .map(Object::toString)
                .toList();
    }


    /**
     * Валидирует аргументы запроса.
     */
    public ValidationResult validateArguments(Request request) {
        List<String> errors = new ArrayList<>();

        CommandType type = request.getType();
        Object data = request.getData();
        String[] arguments = request.getArguments();

        switch (type) {
            case ADD, ADD_IF_MIN -> {
                if (data instanceof Flat flat) {
                    validateFlat(flat, errors);
                } else {
                    errors.add("Квартира не может быть null");
                }
            }
            case UPDATE -> {
                if (arguments == null || arguments.length == 0) {
                    errors.add("ID должен быть больше 0");
                } else {
                    try {
                        int id = Integer.parseInt(arguments[0]);
                        if (id <= 0) {
                            errors.add("ID должен быть больше 0");
                        }
                    } catch (NumberFormatException e) {
                        errors.add("ID должен быть числом");
                    }
                }
                if (data instanceof Flat flat) {
                    validateFlat(flat, errors);
                } else {
                    errors.add("Квартира не может быть null");
                }
            }
            case REMOVE_BY_ID -> {
                if (arguments == null || arguments.length == 0) {
                    errors.add("ID должен быть больше 0");
                } else {
                    try {
                        int id = Integer.parseInt(arguments[0]);
                        if (id <= 0) {
                            errors.add("ID должен быть больше 0");
                        }
                    } catch (NumberFormatException e) {
                        errors.add("ID должен быть числом");
                    }
                }
            }
            case FILTER_GREATER_THAN_HOUSE -> {
                if (data instanceof House house) {
                    validateHouse(house, errors);
                } else {
                    errors.add("Дом не может быть null");
                }
            }
            case COUNT_LESS_THAN_NUMBER_OF_BATHROOMS -> {
                if (arguments == null || arguments.length == 0) {
                    errors.add("Аргумент должен быть указан");
                } else {
                    try {
                        Long.parseLong(arguments[0]);
                    } catch (NumberFormatException e) {
                        errors.add("Аргумент должен быть числом");
                    }
                }
            }
            default -> {
                // Остальные команды не требуют валидации аргументов
            }
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    /**
     * Валидирует объект Flat.
     */
    private void validateFlat(Flat flat, List<String> errors) {
        if (flat.getName() == null || flat.getName().trim().isEmpty()) {
            errors.add("Название не может быть пустым");
        }

        if (flat.getArea() == null || flat.getArea() <= 0) {
            errors.add("Площадь должна быть больше 0");
        }

        if (flat.getCoordinates() == null) {
            errors.add("Координаты не могут быть null");
        } else if (flat.getCoordinates().getX() == null) {
            errors.add("Координата X не может быть null");
        }

        if (flat.getNumberOfRooms() != null && flat.getNumberOfRooms() <= 0) {
            errors.add("Количество комнат должно быть больше 0");
        }

        if (flat.getNumberOfBathrooms() != null && flat.getNumberOfBathrooms() <= 0) {
            errors.add("Количество ванных должно быть больше 0");
        }

        if (flat.getFurnish() == null) {
            errors.add("Furnish не может быть null");
        }

        if (flat.getView() == null) {
            errors.add("View не может быть null");
        }

        if (flat.getHouse() != null) {
            validateHouse(flat.getHouse(), errors);
        }
    }

    /**
     * Валидирует объект House.
     */
    private void validateHouse(House house, List<String> errors) {
        if (house.getName() == null || house.getName().trim().isEmpty()) {
            errors.add("Название дома не может быть пустым");
        }

        if (house.getYear() == null || house.getYear() <= 0) {
            errors.add("Год должен быть больше 0");
        } else if (house.getYear() > 774) {
            errors.add("Год не может быть больше 774");
        }

        if (house.getNumberOfFloors() == null || house.getNumberOfFloors() <= 0) {
            errors.add("Количество этажей должно быть больше 0");
        } else if (house.getNumberOfFloors() > 64) {
            errors.add("Количество этажей не может быть больше 64");
        }

        if (house.getNumberOfFlatsOnFloor() != null && house.getNumberOfFlatsOnFloor() <= 0) {
            errors.add("Количество квартир на этаже должно быть больше 0");
        }

        if (house.getNumberOfLifts() != null && house.getNumberOfLifts() <= 0) {
            errors.add("Количество лифтов должно быть больше 0");
        }
    }

    //  ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    private void validateFlat(Flat flat) {
        List<String> errors = new ArrayList<>();
        validateFlat(flat, errors);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }

    //  МЕТОДЫ ДЛЯ КОМАНД (обёртки)

    /**
     * Возвращает коллекцию (для команды show).
     */
    public List<Flat> getCollection() {
        return repository.findAll();
    }

    /**
     * Сохраняет коллекцию в БД (для команды save).
     */
    public void save() {
        repository.saveAll(repository.findAll());
    }

    /**
     * Подсчитывает количество квартир с numberOfBathrooms меньше заданного.
     * (для countLessThanBathrooms)
     */
    public long countLessThanNumberOfBathrooms(Long value) {
        return countLessThanBathrooms(value);
    }

    /**
     * Удаляет все квартиры, принадлежащие указанному пользователю.
     * @param ownerId ID владельца
     * @return количество удалённых квартир
     */
    public int removeFlatsByOwnerId(int ownerId) {
        return repository.removeByOwnerId(ownerId);
    }

    /**
     * Выполняет скрипт из файла.
     */
    public boolean executeScript(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                System.out.println("Выполняется команда " + lineNumber + ": " + line);
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка выполнения скрипта: " + e.getMessage());
        }
    }

    /**
     * Возвращает текст справки (для команды help).
     */
    public String getHelpText() {
        return """
                Доступные команды:
                help - вывести справку
                info - вывести информацию о коллекции
                show - вывести все элементы
                add - добавить новый элемент
                update - обновить элемент
                remove_by_id - удалить элемент по ID
                clear - очистить коллекцию
                save - сохранить коллекцию в БД
                remove_first - удалить первый элемент
                remove_head - удалить первый элемент (head)
                count_less_than_number_of_bathrooms - количество элементов меньше заданного
                filter_less_than_furnish - фильтровать по типу отделки
                print_field_descending_furnish - вывести furnish по убыванию
                add_if_min - добавить, если минимальный
                filter_greater_than_house - фильтровать по дому
                execute_script - выполнить скрипт
                """;
    }


}