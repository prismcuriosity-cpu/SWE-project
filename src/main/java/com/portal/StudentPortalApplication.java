package com.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Portal System.
 *
 * A self-contained Spring Boot web application that provides student
 * registration, login, dashboard, profile and academic results — backed by an
 * embedded H2 database. Selenium WebDriver tests (see {@code src/test}) drive
 * this application end to end.
 */
@SpringBootApplication
public class StudentPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentPortalApplication.class, args);
    }
}
