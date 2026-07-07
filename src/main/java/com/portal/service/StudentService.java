package com.portal.service;

import com.portal.model.Result;
import com.portal.model.Student;
import com.portal.repository.ResultRepository;
import com.portal.repository.StudentRepository;
import com.portal.web.RegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for student accounts: registration and profile/result
 * lookups used by the web controllers.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          ResultRepository resultRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.resultRepository = resultRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new student, hashing the password before it is persisted.
     *
     * @throws IllegalArgumentException if the username or email is already taken
     */
    @Transactional
    public Student register(RegistrationForm form) {
        if (studentRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (studentRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Student student = new Student(
                form.getUsername(),
                passwordEncoder.encode(form.getPassword()),
                form.getFullName(),
                form.getEmail(),
                form.getProgram(),
                form.getEnrollmentYear());

        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Student getByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Unknown student: " + username));
    }

    @Transactional(readOnly = true)
    public List<Result> getResultsFor(Student student) {
        return resultRepository.findByStudentId(student.getId());
    }
}
