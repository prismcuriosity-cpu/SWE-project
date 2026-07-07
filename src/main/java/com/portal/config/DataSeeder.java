package com.portal.config;

import com.portal.model.Course;
import com.portal.model.Result;
import com.portal.model.Student;
import com.portal.repository.CourseRepository;
import com.portal.repository.ResultRepository;
import com.portal.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Seeds the database on startup with a set of courses and a demo student
 * (username {@code demo} / password {@code Passw0rd!}) together with academic
 * results. The demo account gives the Selenium automation a deterministic,
 * self-created data set to log in with and verify.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(StudentRepository students,
                               CourseRepository courses,
                               ResultRepository results,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (courses.count() == 0) {
                courses.saveAll(List.of(
                        new Course("CS101", "Introduction to Programming", 4),
                        new Course("CS102", "Data Structures", 4),
                        new Course("CS201", "Database Systems", 3),
                        new Course("CS202", "Web Application Development", 3),
                        new Course("CS301", "Software Testing & Automation", 3)
                ));
            }

            if (!students.existsByUsername("demo")) {
                Student demo = new Student(
                        "demo",
                        passwordEncoder.encode("Passw0rd!"),
                        "Demo Student",
                        "demo@student.portal",
                        "BSc Computer Science",
                        2023);
                students.save(demo);

                Course cs101 = courses.findByCode("CS101").orElseThrow();
                Course cs102 = courses.findByCode("CS102").orElseThrow();
                Course cs201 = courses.findByCode("CS201").orElseThrow();
                Course cs301 = courses.findByCode("CS301").orElseThrow();

                results.saveAll(List.of(
                        new Result(demo, cs101, "2023-Fall", 88.0, "A"),
                        new Result(demo, cs102, "2024-Spring", 79.5, "B+"),
                        new Result(demo, cs201, "2024-Fall", 91.0, "A"),
                        new Result(demo, cs301, "2025-Spring", 84.0, "A-")
                ));
            }
        };
    }
}
