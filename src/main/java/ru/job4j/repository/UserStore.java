package ru.job4j.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserStore {
    private final ConcurrentHashMap<String, Person> users = new ConcurrentHashMap<>();

    public void save(Person person) {
        users.put(person.getUsername(), person);
    }

    public Optional<Person> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public List<Person> findAll() {
        return new ArrayList<>(users.values());
    }
}
