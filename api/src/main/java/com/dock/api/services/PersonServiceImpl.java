package com.dock.api.services;

import com.dock.api.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService{

    private final PersonRepository personRepository;

    @Override
    public boolean existsById(Long personId) {
        return personRepository.existsById(personId);
    }
}