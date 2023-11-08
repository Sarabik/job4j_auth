package ru.job4j.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.domain.Person;
import ru.job4j.repository.UserStore;
import ru.job4j.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserStore userStore;
    private PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        userStore.save(person);
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return userStore.findAll();
    }

}
