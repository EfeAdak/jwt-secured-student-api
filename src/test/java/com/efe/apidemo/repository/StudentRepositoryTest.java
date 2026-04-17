package com.efe.apidemo.repository;

import com.efe.apidemo.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;



@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should return true when email exists.")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {

        Student student = new Student();
        student.setName("efe");
        student.setEmail("efe@test.com");

        studentRepository.save(student);

        boolean exists = studentRepository.existsByEmail("efe@test.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when email does not exists.")
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExists() {

        boolean exists = studentRepository.existsByEmail("no@test.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return matching students when searching by name ignoring case.")
    void findByNameContainingIgnoreCase_shouldReturnMatchingStudents() {

        Student student1 = new Student();
        student1.setName("efe");
        student1.setEmail("efe@test.com");

        Student student2 = new Student();
        student2.setName("erdem");
        student2.setEmail("erdem@test.com");

        Student student3 = new Student();
        student3.setName("Ozan Efe ");
        student3.setEmail("ozanefe@test.com");

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Student> result = studentRepository.findByNameContainingIgnoreCase("efe", pageable);

        assertEquals(2, result.getTotalElements());
    }


}
