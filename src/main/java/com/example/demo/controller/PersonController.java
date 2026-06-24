package com.example.demo.controller;

import com.example.demo.model.dto.PersonDto;
import com.example.demo.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("person")
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @PostMapping
  public PersonDto createPerson(@RequestBody @Valid PersonDto person) {
    return personService.createPerson(person);
  }

}
