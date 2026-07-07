package com.portal.repository;

import com.portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data-access repository for {@link Student} entities.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
