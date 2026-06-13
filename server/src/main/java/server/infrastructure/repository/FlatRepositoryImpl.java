package server.infrastructure.repository;

import common.domain.model.Flat;
import server.infrastructure.file.CsvParser;
import server.infrastructure.file.FileManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для работы с CSV файлом.
 * Хранит коллекцию в памяти и синхронизирует с файлом.
 * Использует LinkedList для хранения данных в соответствии с требованием.
 */
public class FlatRepositoryImpl implements FlatRepository {

    private final FileManager fileManager;
    private final CsvParser csvParser;
    private final List<Flat> collection;

    /**
     * Конструктор репозитория.
     *
     * @param fileName имя файла для хранения данных
     */
    public FlatRepositoryImpl(String fileName) {
        this.fileManager = new FileManager(fileName);
        this.csvParser = new CsvParser();
        // создаём LinkedList и загружаем данные
        this.collection = new LinkedList<>();
        this.collection.addAll(loadFromFile());
    }

    @Override
    public List<Flat> findAll() {
        return collection;
    }

    @Override
    public Optional<Flat> findById(int id) {
        return collection.stream()
                .filter(flat -> flat.getId() == id)
                .findFirst();
    }

    @Override
    public boolean existsById(int id) {
        return collection.stream()
                .anyMatch(flat -> flat.getId() == id);
    }

    @Override
    public void save(Flat flat) {
        collection.add(flat);
        saveToFile();
    }

    @Override
    public void deleteById(int id) {
        collection.removeIf(flat -> flat.getId() == id);
        saveToFile();
    }

    @Override
    public void clear() {
        collection.clear();
        saveToFile();
    }

    @Override
    public int size() {
        return collection.size();
    }


    /**
     * Загружает коллекцию квартир из файла.
     *
     * @return список квартир из файла
     */
    private List<Flat> loadFromFile() {
        List<String> lines = fileManager.readAllLines();
        return csvParser.fromCsvLines(lines);
    }

    /**
     * Сохраняет коллекцию квартир в файл.
     */
    public void saveToFile() {
        List<String> lines = csvParser.toCsvLines(collection);
        fileManager.writeAllLines(lines);
    }
    @Override
    public void update(int id, Flat newFlat) {
        // Находим элемент по ID и заменяем его
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.set(i, newFlat);
                saveToFile();  // Сохраняем изменения в файл
                return;
            }
        }
        // Если не нашли то выбрасываем ошибку
        throw new IllegalArgumentException("Элемент с ID=" + id + " не найден");
    }

    @Override
    public void saveAll(List<Flat> flats) {
        // Очищаем коллекцию
        collection.clear();

        // Добавляем все элементы
        collection.addAll(flats);

        // Сохраняем в файл одним разом
        saveToFile();
    }
}