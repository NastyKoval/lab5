package server.infrastructure.repository;

import common.domain.model.Flat;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с коллекцией квартир
 * Определяет контракт хранилища
 */
public interface FlatRepository {

    /**
     * Получить все квартиры
     * @return список всех квартир
     */
    List<Flat> findAll();

    /**
     * Найти квартиру по ID
     * @param id идентификатор квартиры
     * @return квартиру если найдена, или пустой Optional
     */
    Optional<Flat> findById(int id);

    /**
     * Сохранить квартиру
     * @param flat квартира для сохранения
     */
    void save(Flat flat);

    /**
     * Удалить квартиру по ID
     * @param id идентификатор для удаления
     */
    void deleteById(int id);

    /**
     * Очистить всю коллекцию
     */
    void clear();

    /**
     * Сохраняет все элементы из списка.
     */
    void saveAll(List<Flat> flats);

    /**
     * Получить размер коллекции
     * @return количество квартир
     */
    int size();

    boolean existsById(int id);

    void update(int id, Flat flat);
    }

