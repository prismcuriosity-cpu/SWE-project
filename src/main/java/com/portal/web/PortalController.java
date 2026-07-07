package com.portal.web;

import com.portal.model.Result;
import com.portal.model.Student;
import com.portal.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/**
 * Handles the authenticated area of the portal: the dashboard, the student
 * profile and the academic results pages. The signed-in user is resolved from
 * the security {@link Principal}, so each student only ever sees their own data.
 */
@Controller
public class PortalController {

    private final StudentService studentService;

    public PortalController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        Student student = studentService.getByUsername(principal.getName());
        List<Result> results = studentService.getResultsFor(student);

        model.addAttribute("student", student);
        model.addAttribute("courseCount", results.size());
        model.addAttribute("averageMarks", averageMarks(results));
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        Student student = studentService.getByUsername(principal.getName());
        model.addAttribute("student", student);
        return "profile";
    }

    @GetMapping("/results")
    public String results(Principal principal, Model model) {
        Student student = studentService.getByUsername(principal.getName());
        List<Result> results = studentService.getResultsFor(student);

        model.addAttribute("student", student);
        model.addAttribute("results", results);
        model.addAttribute("averageMarks", averageMarks(results));
        return "results";
    }

    private double averageMarks(List<Result> results) {
        return results.stream()
                .mapToDouble(Result::getMarks)
                .average()
                .orElse(0.0);
    }
}
