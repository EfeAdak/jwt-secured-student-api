package com.efe.apidemo.controller;

import com.efe.apidemo.dto.StudentResponse;
import com.efe.apidemo.dto.StudentRequest;
import com.efe.apidemo.exception.DuplicateEmailException;
import com.efe.apidemo.exception.StudentNotFoundException;
import com.efe.apidemo.model.Student;
import com.efe.apidemo.mapper.StudentMapper;
import com.efe.apidemo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentMapper studentMapper;

    @Test
    void getStudentById_shouldReturnStudent() throws Exception {

        Student student = new Student();
        student.setId(1L);
        student.setName("efe");
        student.setEmail("efe@test.com");

        StudentResponse response = new StudentResponse(1L, "efe", "efe@test.com");

        when(studentService.getByIdOrThrow(1L)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(response);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("efe"))
                .andExpect(jsonPath("$.email").value("efe@test.com"));
    }

    @Test
    void getStudentById_shouldReturn404NotFound() throws Exception {

        when(studentService.getByIdOrThrow(999L))
                .thenThrow(new StudentNotFoundException(999L));

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Student not found with id 999"))
                .andExpect(jsonPath("$.path").value("/students/999"));
    }

    @Test
    void addStudent_shouldReturn400WhenValidationFails() throws Exception {

        String invalidRequest = """
                {
                    "name" : "",
                    "email" : "abc"
                }
                """;

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.path").value("/students"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }


    @Test
    void addStudent_shouldReturn201WhenStudentCreated() throws Exception {

        String request = """
                {
                    "name": "Efe",
                    "email": "efe@test.com"
                }
                """;

        Student student = new Student();
        student.setId(1L);
        student.setName("Efe");
        student.setEmail("efe@test.com");

        StudentResponse response = new StudentResponse(
                1L,
                "Efe",
                "efe@test.com"
        );

        when(studentMapper.toEntity(any(StudentRequest.class))).thenReturn(student);
        when(studentService.add(student)).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(response);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Efe"))
                .andExpect(jsonPath("$.email").value("efe@test.com"));
    }

    @Test
    void updateStudent_shouldReturn200WhenStudentUpdated() throws Exception {

        String request = """
                {
                    "name": "Efe Updated",
                    "email": "efe.updated@test.com"
                }
                """;

        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("Efe Updated");
        updatedStudent.setEmail("efe.updated@test.com");

        StudentResponse response = new StudentResponse(
                1L,
                "Efe Updated",
                "efe.updated@test.com"
        );

        when(studentMapper.toEntity(any(StudentRequest.class))).thenReturn(updatedStudent);
        when(studentService.updateOrThrow(eq(1L), any(Student.class))).thenReturn(updatedStudent);
        when(studentMapper.toResponse(updatedStudent)).thenReturn(response);

        mockMvc.perform(put("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Efe Updated"))
                .andExpect(jsonPath("$.email").value("efe.updated@test.com"));

    }

    @Test
    void deleteStudent_ShouldReturnNoContent() throws Exception {
        Long studentId = 1L;

        doNothing().when(studentService).deleteOrThrow(studentId);

        mockMvc.perform(delete("/students/{id}", studentId))
                .andExpect(status().isNoContent());

        verify(studentService).deleteOrThrow(studentId);

    }


    @Test
    void deleteStudent_whenStudentNotFound_shouldReturnNotFound() throws Exception {
        Long studentId = 999L;

        doThrow(new StudentNotFoundException(studentId))
                .when(studentService)
                .deleteOrThrow(studentId);

        mockMvc.perform(delete("/students/{id}", studentId))
                .andExpect(status().isNotFound());

        verify(studentService).deleteOrThrow(studentId);
    }

    @Test
    void addStudent_whenRequestisInvalid_shouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "name" : "",
                    "email" : "abc"
                }
                """;

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.path").value("/students"))
                .andExpect(jsonPath("$.fieldErrors.name").isArray())
                .andExpect(jsonPath("$.fieldErrors.name[0]").exists())
                .andExpect(jsonPath("$.fieldErrors.name[1]").exists())
                .andExpect(jsonPath("$.fieldErrors.email").isArray())
                .andExpect(jsonPath("$.fieldErrors.email[0]").exists());

    }

    @Test
    void addStudent_whenEmailAlreadyExist_shouldReturnConflict() throws Exception {
        String requestJson = """
                {
                    "name" : "Efe",
                    "email" : "efe@example.com"
                }
                """;
        
        Student student = new Student();
        student.setName("Efe");
        student.setEmail("efe@example.com");

        when(studentMapper.toEntity(any(StudentRequest.class))).thenReturn(student);
        doThrow(new DuplicateEmailException("efe@example.com"))
                .when(studentService)
                .add(any(Student.class));

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email already exists: efe@example.com"))
                .andExpect(jsonPath("$.path").value("/students"));

        verify(studentService).add(any(Student.class));

    }


}