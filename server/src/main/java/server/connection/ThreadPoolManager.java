package server.connection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Менеджер пулов потоков для обработки запросов.
 */
public class ThreadPoolManager {

    // Фиксированный пул для чтения (I/O операции)
    private final ExecutorService receivePool;
    // Кэширующий пул для обработки (CPU операции, разная длительность)
    private final ExecutorService processPool;
    // Фиксированный пул для отправки (I/O операции)
    private final ExecutorService sendPool;

    public ThreadPoolManager() {

        this.receivePool = Executors.newFixedThreadPool(4);
        this.processPool = Executors.newCachedThreadPool();
        this.sendPool = Executors.newFixedThreadPool(4);
    }

    public ExecutorService getReceivePool() { return receivePool; }
    public ExecutorService getProcessPool() { return processPool; }
    public ExecutorService getSendPool() { return sendPool; }

    /** Корректное завершение всех потоков при выключении сервера */
    public void shutdown() {
        receivePool.shutdown();
        processPool.shutdown();
        sendPool.shutdown();
    }
}