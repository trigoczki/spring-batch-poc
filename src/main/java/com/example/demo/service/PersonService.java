package com.example.demo.service;

import com.example.demo.model.dto.PersonDto;
import com.example.demo.model.entity.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Transactional
  public PersonDto createPerson(PersonDto dto) {
    Person person = new Person();
    person.setName(dto.name());
    person.setAddress(dto.address());
    person.setOccupation(dto.occupation());

    Person savedPerson = save(person);
    return new PersonDto(savedPerson.getId(), savedPerson.getName(), savedPerson.getAddress(),
        savedPerson.getOccupation());
  }

  @Transactional(readOnly = true)
  public Person getById(Long id) {
    return personRepository.getReferenceById(id);
  }


  @Transactional
  public Person save(Person person) {
    return personRepository.save(person);
  }
}
