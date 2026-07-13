package com.portal.web;

import com.portal.model.Result;
import com.portal.model.Student;
import com.portal.service.GradeScale;
import com.portal.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

/**
 * Serves the Academic Result page — a semester-wise result viewer modelled on a
 * typical university result portal. The student picks a semester from a
 * dropdown and searches; the page then shows their information and the grades,
 * marks and semester GPA for that semester.
 *
 * <p>All data comes from the student's own seeded results, so the page is fully
 * self-contained.</p>
 */
@Controller
public class AcademicResultController {

    private final StudentService studentService;

    public AcademicResultController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/academic-result")
    public String academicResult(@RequestParam(value = "semester", required = false) String semester,
                                 Principal principal,
                                 Model model) {
        Student student = studentService.getByUsername(principal.getName());
        List<Result> allResults = studentService.getResultsFor(student);

        // Distinct semesters the student has results for, newest-looking first.
        List<String> semesters = allResults.stream()
                .map(Result::getSemester)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("semesters", semesters);
        model.addAttribute("selectedSemester", semester);

        if (semester != null && !semester.isBlank()) {
            List<ResultRow> rows = allResults.stream()
                    .filter(r -> semester.equals(r.getSemester()))
                    .map(r -> new ResultRow(
                            r.getCourse().getCode(),
                            r.getCourse().getTitle(),
                            r.getCourse().getCredits(),
                            r.getMarks(),
                            r.getGrade(),
                            GradeScale.pointFor(r.getGrade())))
                    .toList();

            model.addAttribute("rows", rows);
            model.addAttribute("totalCredits", rows.stream().mapToInt(ResultRow::credits).sum());
            model.addAttribute("semesterGpa", semesterGpa(rows));
            model.addAttribute("searched", true);
        } else {
            model.addAttribute("searched", false);
        }

        return "academic-result";
    }

    /** Credit-weighted GPA for the selected semester's rows. */
    private double semesterGpa(List<ResultRow> rows) {
        int totalCredits = rows.stream().mapToInt(ResultRow::credits).sum();
        if (totalCredits == 0) {
            return 0.0;
        }
        double weighted = rows.stream()
                .mapToDouble(r -> r.gradePoint() * r.credits())
                .sum();
        return weighted / totalCredits;
    }
}
