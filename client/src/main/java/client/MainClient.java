package client;

import client.presentation.ConsoleUI;
import client.presentation.application.command_cl.CommandRegistry;
import client.presentation.application.validator.InputValidator;

public class MainClient {
    public static void main(String[] args) {
        // Создаём зависимости
        InputValidator validator = new InputValidator();
        CommandRegistry registry = new CommandRegistry();



        // Создаём и запускаем интерфейс
        ConsoleUI consoleUI = new ConsoleUI(registry, validator);
        consoleUI.start();
    }
}