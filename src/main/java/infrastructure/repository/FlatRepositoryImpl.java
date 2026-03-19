package infrastructure.repository;

import domain.model.Flat;
import domain.repository.FlatRepository;
import infrastructure.file.CsvParser;
import infrastructure.file.FileManager;

import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория для работы с CSV файлом
 * Хранит коллекцию в памяти и синхронизирует с файлом
 */
public class FlatRepositoryImpl implements FlatRepository {

    private final FileManager fileManager;
    private final CsvParser csvParser;
    private List<Flat> collection;

    /**
     * Конструктор
     * @param fileName имя файла для хранения данных
     */
    public FlatRepositoryImpl(String fileName) {
        this.fileManager = new FileManager(fileName);
        this.csvParser = new CsvParser();
        this.collection = loadFromFile();

    }

    @Override
    public List<Flat> findAll() {
        return collection;
        // Возвращаем весь список
    }

    @Override
    public Optional<Flat> findById(int id) {
        return collection.stream()
                .filter(flat -> flat.getId() == id)
                .findFirst();
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
     * Загрузить коллекцию из файла
     * @return список квартир
     */
    private List<Flat> loadFromFile() {
        List<String> lines = fileManager.readAllLines();
        return csvParser.fromCsvLines(lines);
    }

    /**
     * Сохранить коллекцию в файл
     */
    private void saveToFile() {
        List<String> lines = csvParser.toCsvLines(collection);
        fileManager.writeAllLines(lines);
    }
}
