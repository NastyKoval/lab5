package application.service;

import application.request.Request;
import application.response.Response;
import domain.model.Flat;
import domain.model.House;
import domain.repository.FlatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с квартирами.
 *
 * <p>Содержит бизнес-логику для операций с коллекцией квартир.</p>
 */
public class FlatService {

    private final FlatRepository repository;
    private final BusinessValidator validator;
    private int nextId;
    private final LocalDateTime initializationDate;

    /**
     * Конструктор FlatService.
     *
     * @param repository репозиторий для работы с данными
     * @param validator валидатор для проверки данных
     */
    public FlatService(FlatRepository repository, BusinessValidator validator) {
        this.repository = repository;
        this.validator = validator;
        this.nextId = generateNextId();
        this.initializationDate = LocalDateTime.now();
    }

    /**
     * Добавляет квартиру в коллекцию.
     *
     * @param flat квартира для добавления
     * @throws IllegalArgumentException если данные невалидны
     */
    public void addFlat(Flat flat) {
        // Валидация квартиры
        validator.validateFlat(flat);

        // Устанавливаем ID
        flat.setId(nextId++);

        // Добавляем в репозиторий
        repository.save(flat);
    }

    /**
     * Обновляет квартиру по ID.
     *
     * @param id идентификатор квартиры
     * @param newFlat новые данные квартиры
     * @return true если обновлено, false если не найдено
     */
    public boolean updateFlat(int id, Flat newFlat) {
        Optional<Flat> existing = repository.findById(id);

        if (existing.isEmpty()) {
            return false;
        }

        // Валидация
        validator.validateFlat(newFlat);

        // Сохраняем ID
        newFlat.setId(id);

        // Удаляем старую и добавляем новую
        repository.deleteById(id);
        repository.save(newFlat);

        return true;
    }

    /**
     * Удаляет квартиру по ID.
     *
     * @param id идентификатор квартиры
     * @return true если удалено, false если не найдено
     */
    public boolean removeById(int id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    /**
     * Получает все квартиры.
     *
     * @return список всех квартир
     */
    public List<Flat> getAllFlats() {
        return repository.findAll();
    }

    /**
     * Получает квартиру по ID.
     *
     * @param id идентификатор квартиры
     * @return Optional содержащий квартиру или пустой
     */
    public Optional<Flat> getById(int id) {
        return repository.findById(id);
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        repository.clear();
        nextId = 1;
    }

    /**
     * Получает информацию о коллекции.
     *
     * @return текстовая информация
     */
    public String getInfo() {
        StringBuilder info = new StringBuilder();

        info.append("Тип коллекции: LinkedList<Flat>\n");
        info.append("Количество элементов: ").append(repository.size()).append("\n");
        info.append("Дата инициализации: ").append(initializationDate).append("\n");
        info.append("Объём коллекции: ").append(repository.size()).append("\n");

        return info.toString();
    }

    /**
     * Считает количество квартир с numberOfBathrooms меньше заданного.
     *
     * @param numberOfBathrooms пороговое значение
     * @return количество квартир
     */
    public int countLessThanNumberOfBathrooms(long numberOfBathrooms) {
        return (int) repository.findAll().stream()
                .filter(flat -> flat.getNumberOfBathrooms() < numberOfBathrooms)
                .count();
    }

    /**
     * Фильтрует квартиры по house (больше заданного).
     *
     * @param house объект house для сравнения
     * @return отфильтрованный список
     */
    public List<Flat> filterGreaterThanHouse(House house) {
        return repository.findAll().stream()
                .filter(flat -> flat.getHouse().compareTo(house) > 0)
                .toList();
    }

    /**
     * Добавляет квартиру если она меньше минимальной.
     *
     * @param flat квартира для добавления
     * @return true если добавлена, false если нет
     */
    public boolean addIfMin(Flat flat) {
        Optional<Flat> min = repository.findAll().stream()
                .min(Flat::compareTo);

        if (min.isEmpty() || flat.compareTo(min.get()) < 0) {
            addFlat(flat);
            return true;
        }

        return false;
    }

    /**
     * Удаляет первый элемент из коллекции.
     *
     * @return true если удалено, false если коллекция пуста
     */
    public boolean removeFirst() {
        List<Flat> flats = repository.findAll();
        if (flats.isEmpty()) {
            return false;
        }

        repository.deleteById(flats.get(0).getId());
        return true;
    }

    /**
     * Выводит и удаляет первый элемент.
     *
     * @return Optional содержащий удалённую квартиру
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
     * Генерирует следующий ID.
     *
     * @return следующий доступный ID
     */
    private int generateNextId() {
        return repository.findAll().stream()
                .mapToInt(Flat::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Сохраняет коллекцию в файл.
     */
    public void save() {
        // Реализуется в FlatRepositoryImpl автоматически
    }

    /**
     * Получает следующий доступный ID.
     *
     * @return следующий ID
     */
    public int getNextId() {
        return nextId;
    }
}