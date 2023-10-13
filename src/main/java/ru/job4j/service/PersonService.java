package ru.job4j.service;

import ru.job4j.domain.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> findAll();
    Optional<Person> findById(int id);
    Optional<Person> save(Person person);
    boolean deleteById(int id);
    boolean update(Person person);
}
