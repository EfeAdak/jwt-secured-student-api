package com.efe.apidemo.service;

import com.efe.apidemo.exception.DuplicateEmailException;
import com.efe.apidemo.mapper.StudentMapper;
import com.efe.apidemo.model.Student;
import com.efe.apidemo.repository.StudentRepository;
import com.efe.apidemo.exception.StudentNotFoundException;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> search(String keyword, Pageable pageable) {
        return studentRepository
                .findByNameContainingIgnoreCase(keyword, pageable);
    }


    public Page<Student> getAll(Pageable pageable) {

        return studentRepository.findAll(pageable);
    }


    public Student add(Student student) {
        String email = student.getEmail();

        if (email != null && studentRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        return studentRepository.save(student);
    }

    public void deleteOrThrow(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }

        studentRepository.deleteById(id);

    }

    @Transactional
    public Student updateOrThrow(Long id, Student updatedStudent) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        String newEmail = updatedStudent.getEmail();
        if (newEmail != null
                && !newEmail.equals(existing.getEmail())
                && studentRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        existing.setName(updatedStudent.getName());
        existing.setEmail(newEmail);

        return existing;
    }

    public Student getByIdOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

}
