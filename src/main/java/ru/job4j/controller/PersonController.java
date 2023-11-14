package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ru.job4j.util.NonNullFieldValueReplacer.updateAllNonNullFields;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    
    private final PersonService personService;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;
    private static final String PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}";

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> list = personService.findAll();
        MultiValueMap<String, String> header =
                new MultiValueMapAdapter<>(Map.of("Content-Type", List.of("application/json")));
        return new ResponseEntity<>(list, header, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Optional<Person> optPerson = personService.findById(id);
        if (optPerson.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found");
        }
        return new ResponseEntity<Person>(
                optPerson.get(),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        usernameAndPasswordValidation(person);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Optional<Person> optPerson = personService.save(person);
        return new ResponseEntity<>(
                optPerson.orElse(new Person()),
                optPerson.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        usernameAndPasswordValidation(person);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        boolean isUpdated = personService.update(person);
        if (isUpdated) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {

        boolean isDeleted = personService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not deleted");
    }

    @PatchMapping("/")
    public ResponseEntity<Person> patch(@RequestBody Person person) throws InvocationTargetException, IllegalAccessException {
        Optional<Person> opt = personService.findById(person.getId());
        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found and not patched");
        }
        Person current = opt.get();
        updateAllNonNullFields(current, person);
        boolean isUpdated = personService.update(current);
        return new ResponseEntity<>(current, HttpStatus.OK);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
                new HashMap<>() {{
                    put("message", e.getMessage());
                    put("type", e.getClass());
                }}
        ));
    }

    private void usernameAndPasswordValidation(Person person) {
        if (!person.getPassword().matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "Invalid password. Password must be at least 8 characters long "
                            + "with 1 uppercase and 1 lowercase character");
        }
        if (person.getUsername().length() < 6) {
            throw new IllegalArgumentException(
                    "Invalid username. Username must be at least 6 characters long"
            );
        }
    }
}
