package com.efe.apidemo.mapper;

import com.efe.apidemo.dto.StudentRequest;
import com.efe.apidemo.dto.StudentResponse;
import com.efe.apidemo.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component // Spring'e “Bu sınıfı yönet, gerektiğinde bana ver.” der.
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        return student;

    }

    // TEK Student -> StudentResponse
    public StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getEmail());
    }

    // LIST Student -> LIST StudentResponse
    public List<StudentResponse> toResponseList(List<Student> students) {
        return students.stream()
                .map(this::toResponse)
                 .collect(Collectors.toList());
    }

}
