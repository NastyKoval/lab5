package server.application.service;

import server.infrastructure.repository.FlatRepository;
import client.presentation.application.request.*;
import domain.model.Flat;
import domain.model.House;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с квартирами (бизнес-логика)
 */
public class FlatService {

    private final FlatRepository repository;
    private int nextId;

    public FlatService(FlatRepository repository) {
        this.repository = repository;
        this.nextId = generateNextId();
    }

    // CRUD Операции

    /**
     * Добавляет новую квартиру.
     */
    public void addFlat(Flat flat) {
        validateFlat(flat);
        flat.setId(nextId++);
        repository.save(flat);
    }

    /**
     * Добавляет квартиру только если она меньше минимальной.
     */
    public boolean addIfMin(Flat flat) {
        validateFlat(flat);

        if (repository.findAll().isEmpty()) {
            flat.setId(nextId++);
            repository.save(flat);
            return true;
        }

        Flat minFlat = repository.findAll().stream()
                .min(Flat::compareTo)
                .orElseThrow();

        if (flat.compareTo(minFlat) < 0) {
            flat.setId(nextId++);
            repository.save(flat);
            return true;
        }

        return false;
    }

    /**
     * Удаляет квартиру по ID.
     */
    public boolean removeById(int id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    /**
     * Обновляет квартиру по ID.
     */
    public boolean updateFlat(int id, Flat newFlat) {
        if (!repository.existsById(id)) {
            return false;
        }
        validateFlat(newFlat);
        newFlat.setId(id);
        repository.update(id, newFlat);
        return true;
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
        nextId = 1;
    }

    // ========== СПЕЦИАЛЬНЫЕ МЕТОДЫ ==========

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
     * Фильтрует квартиры по house (больше заданного).
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

    // ========== ВАЛИДАЦИЯ ==========

    /**
     * Валидирует аргументы запроса.
     */
    public ValidationResult validateArguments(Request request) {
        List<String> errors = new ArrayList<>();

        if (request instanceof AddRequest addRequest) {
            Flat flat = addRequest.getFlat();
            if (flat == null) {
                errors.add("Квартира не может быть null");
            } else {
                validateFlat(flat, errors);
            }

        } else if (request instanceof UpdateRequest updateRequest) {
            if (updateRequest.getId() <= 0) {
                errors.add("ID должен быть больше 0");
            }
            Flat flat = updateRequest.getFlat();
            if (flat == null) {
                errors.add("Квартира не может быть null");
            } else {
                validateFlat(flat, errors);
            }

        } else if (request instanceof RemoveRequest removeRequest) {
            if (removeRequest.getId() <= 0) {
                errors.add("ID должен быть больше 0");
            }

        } else if (request instanceof AddIfMinRequest addIfMinRequest) {
            Flat flat = addIfMinRequest.getFlat();
            if (flat == null) {
                errors.add("Квартира не может быть null");
            } else {
                validateFlat(flat, errors);
            }

        } else if (request instanceof FilterGreaterThanHouseRequest filterRequest) {
            House house = filterRequest.getHouse();
            if (house == null) {
                errors.add("Дом не может быть null");
            } else {
                validateHouse(house, errors);
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

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private int generateNextId() {
        return repository.findAll().stream()
                .mapToInt(Flat::getId)
                .max()
                .orElse(0) + 1;
    }

    private void validateFlat(Flat flat) {
        List<String> errors = new ArrayList<>();
        validateFlat(flat, errors);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }
}