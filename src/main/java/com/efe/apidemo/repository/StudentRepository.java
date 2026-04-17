package com.efe.apidemo.repository;

import com.efe.apidemo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
