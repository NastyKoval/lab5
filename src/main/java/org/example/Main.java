package org.example;
import domain.repository.FlatRepository;
import infrastructure.repository.FlatRepositoryImpl;
import domain.model.Flat;
import java.util.List;
import java.util.Optional;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        FlatRepository repo = new FlatRepositoryImpl("data/test.csv");


        System.out.println("Размер: " + repo.size());


        List<Flat> flats = repo.findAll();
        System.out.println("Квартир: " + flats.size());


        Optional<Flat> flat = repo.findById(1);
        flat.ifPresent(f -> System.out.println("Нашёл: " + f.getName()));

        System.out.println("Готово!");
    }
}