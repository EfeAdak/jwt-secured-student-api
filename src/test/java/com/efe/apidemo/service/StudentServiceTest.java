package com.efe.apidemo.service;


import com.efe.apidemo.exception.DuplicateEmailException;
import com.efe.apidemo.exception.StudentNotFoundException;
import com.efe.apidemo.mapper.StudentMapper;
import com.efe.apidemo.model.Student;
import com.efe.apidemo.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getByIdOrThrow_shouldReturnStudent_whenStudentExists() {

        Long studentId = 1L;

        Student student = new Student();
        student.setId(studentId);
        student.setName("efe");
        student.setEmail("efe@test.com");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Student result = studentService.getByIdOrThrow(studentId);

        assertEquals(studentId, result.getId());
        assertEquals("efe", result.getName());
        assertEquals("efe@test.com", result.getEmail());
    }

    @Test
    void getByIdOrThrow_shouldReturnException_whenStudentNotFound() {

        Long studentId = 99L;

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.getByIdOrThrow(studentId)
        );
    }

    @Test
    void add_shouldThrowDuplicateEmailException_whenEmailAlreadyExists() {

        Student student = new Student();
        student.setName("efe");
        student.setEmail("efe@test.com");

        when(studentRepository.existsByEmail("efe@test.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> studentService.add(student)
        );
    }

    @Test
    void add_shouldSaveStudent_whenEmailIsUnique() {

        Student student = new Student();
        student.setName("efe");
        student.setEmail("efe@test.com");

        when(studentRepository.existsByEmail("efe@test.com"))
                .thenReturn(false);

        when(studentRepository.save(student))
                .thenReturn(student);

        Student result = studentService.add(student);

        assertEquals("efe", result.getName());
        assertEquals("efe@test.com", result.getEmail());

        verify(studentRepository).save(student);
    }

    @Test
    void deleteOrThrow_shouldDeleteStudent_whenStudentExists() {

        Long studentId = 1L;

        when(studentRepository.existsById(studentId))
                .thenReturn(true);

        studentService.deleteOrThrow(studentId);

        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void deleteOrThrow_shouldThrowException_whenStudentDoesNotExist() {

        Long studentId = 99L;

        when(studentRepository.existsById(studentId))
                .thenReturn(false);

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.deleteOrThrow(studentId)
        );

        verify(studentRepository, never()).deleteById(studentId);
    }

    @Test
    void updateOrThrow_shouldUpdateStudent_whenStudentExists() {

        Long studentId = 1L;

        Student existing = new Student();
        existing.setId(studentId);
        existing.setName("oldName");
        existing.setEmail("old@test.com");

        Student updated = new Student();
        updated.setName("newName");
        updated.setEmail("new@test.com");

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existing));

        when(studentRepository.existsByEmail("new@test.com"))
                .thenReturn(false);

        Student result = studentService.updateOrThrow(studentId, updated);

        assertEquals("newName", result.getName());
        assertEquals("new@test.com", result.getEmail());
    }

    @Test
    void updateOrThrow_shouldThrowException_whenStudentDoesNotExist() {

        Long studentId = 99L;

        Student updated = new Student();
        updated.setName("newName");
        updated.setEmail("new@test.com");

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.updateOrThrow(studentId, updated)
        );
    }

    @Test
    void updateOrThrow_shouldThrowDuplicateEmailException_whenNewEmailAlreadyExists() {

        Long studentId = 1L;

        Student existing = new Student();
        existing.setId(studentId);
        existing.setName("oldName");
        existing.setEmail("old@test.com");

        Student updated = new Student();
        updated.setName("newName");
        updated.setEmail("taken@test.com");

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existing));

        when(studentRepository.existsByEmail("taken@test.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> studentService.updateOrThrow(studentId, updated)
        );
    }

    @Test
    void search_shouldReturnStudents_whenKeywordsGiven() {

        String keyword = "efe";
        Pageable pageable = PageRequest.of(0, 10);

        Student student = new Student();
        student.setId(1L);
        student.setName("efe");
        student.setEmail("efe@test.com");

        Page<Student> studentPage = new PageImpl<>(List.of(student));

        when(studentRepository.findByNameContainingIgnoreCase(keyword, pageable))
                .thenReturn(studentPage);

        Page<Student> result = studentService.search(keyword, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("efe", result.getContent().get(0).getName());
    }

    @Test
    void getAll_shouldReturnAllStudents() {

        Pageable pageable = PageRequest.of(0, 10);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("efe");
        student1.setEmail("efe@test.com");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("erdem");
        student2.setEmail("erdem@test.com");

        Page<Student> studentPage = new PageImpl<>(List.of(student1, student2));

        when(studentRepository.findAll(pageable))
                .thenReturn(studentPage);

        Page<Student> result = studentService.getAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("efe", result.getContent().get(0).getName());
        assertEquals("erdem", result.getContent().get(1).getName());
    }
}
