package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.connection.UdpServer;
import server.application.service.FlatService;
import server.infrastructure.repository.FlatRepositoryImpl;
import server.infrastructure.file.FileManager;
public class MainServer {

    private static final Logger logger = LogManager.getLogger(MainServer.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar server.jar <data_file.csv>");
            System.exit(1);
        }

        String fileName = args[0];
        logger.info("Server starting... File: {}", fileName);

        final FileManager fileManager = new FileManager(fileName);
        final FlatRepositoryImpl repository = new FlatRepositoryImpl(fileName);
        FlatService flatService = new FlatService(repository);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered, saving collection...");
            try {
                repository.saveToFile();
                logger.info("Collection saved successfully");
            } catch (Exception e) {
                logger.error("Ошибка при сохранении: {}", e.getMessage());
                e.printStackTrace();
            }
        }));

        UdpServer server = new UdpServer(8080, flatService);
        logger.info("Server started on port 8080");
        server.start();
    }
}
