package com.efe.apidemo.controller;

import com.efe.apidemo.dto.StudentResponse;
import com.efe.apidemo.exception.ApiError;
import com.efe.apidemo.mapper.StudentMapper;
import com.efe.apidemo.model.Student;
import com.efe.apidemo.service.StudentService;
import com.efe.apidemo.dto.StudentRequest;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;



import jakarta.validation.Valid;



@Tag(name = "Student API", description = "CRUD operations for managing students")
@RestController // RestController -> It tells Spring that this class will handle HTTP requests and return JSON or text
public class  StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    })
    @Operation(
            summary =  "Get all students",
            description = "Returns students with pagination, sorting and optional search"
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<Page<StudentResponse>> getAll(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        Page<Student> studentPage;

        if(search != null && !search.isBlank()) {
            studentPage = studentService.search(search, pageable);
        }
        else {
            studentPage = studentService.getAll(pageable);
        }

        Page<StudentResponse> responsePage = studentPage.map(studentMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Student created successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @Operation(
            summary = "Create student",
            description = "Creates a new student with name and email"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    public ResponseEntity<StudentResponse> addStudent(@Valid @RequestBody StudentRequest request) {

        Student student = studentMapper.toEntity(request);
        Student created = studentService.add(student);
        StudentResponse response = studentMapper.toResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Student deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @Operation(
            summary = "Delete student",
            description = "Deletes a student by id"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteOrThrow(id);
        return ResponseEntity.noContent().build(); // 204
    }




    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student updated successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))

            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))

            )
    })
    @Operation(
            summary = "Update student",
            description = "Updates an existing student by id"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/students/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {

       Student updated = studentMapper.toEntity(request);
       Student saved = studentService.updateOrThrow(id, updated);

       return ResponseEntity.ok(studentMapper.toResponse(saved));
    }




    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Student retrieved successfully",
                    content = @Content(schema = @Schema(implementation = StudentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @Operation(
            summary = "Get student by id",
            description = "Returns student by id"
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id)
    {
        Student student = studentService.getByIdOrThrow(id);
        return ResponseEntity.ok(studentMapper.toResponse(student));
    }

}
