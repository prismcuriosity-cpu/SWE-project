package com.portal.repository;

import com.portal.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access repository for {@link Result} entities.
 */
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByStudentId(Long studentId);
}
