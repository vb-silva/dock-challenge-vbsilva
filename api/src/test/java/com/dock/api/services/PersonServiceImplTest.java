package com.dock.api.services;

import com.dock.api.repositories.PersonRepository;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.AssertTrue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PersonServiceImplTest {
    private PersonRepository personRepository = mock(PersonRepository.class);
    private PersonServiceImpl personService = new PersonServiceImpl(personRepository);

    @Test
    void existsByIdShouldReturnTrue() {
        var existingId = 1L;

        when(personService.existsById(eq(existingId))).thenReturn(true);
        assertTrue(personService.existsById(existingId));

        verify(personRepository, times(1)).existsById(existingId);
    }

    @Test
    void existsByIdShouldReturnFalse() {
        var nonExistentId = 2L;

        when(personService.existsById(eq(nonExistentId))).thenReturn(false);
        assertFalse(personService.existsById(nonExistentId));

        verify(personRepository, times(1)).existsById(nonExistentId);
    }
}