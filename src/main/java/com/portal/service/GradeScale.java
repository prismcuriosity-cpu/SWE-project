package com.portal.service;

import java.util.Map;

/**
 * The UGC uniform grading scale: maps a letter grade to its grade point.
 * Used to compute a semester GPA from a student's results.
 */
public final class GradeScale {

    private static final Map<String, Double> POINTS = Map.ofEntries(
            Map.entry("A+", 4.00),
            Map.entry("A", 3.75),
            Map.entry("A-", 3.50),
            Map.entry("B+", 3.25),
            Map.entry("B", 3.00),
            Map.entry("B-", 2.75),
            Map.entry("C+", 2.50),
            Map.entry("C", 2.25),
            Map.entry("D", 2.00),
            Map.entry("F", 0.00));

    private GradeScale() {
    }

    /** Returns the grade point for a letter grade, or 0.0 if unknown. */
    public static double pointFor(String letterGrade) {
        if (letterGrade == null) {
            return 0.0;
        }
        return POINTS.getOrDefault(letterGrade.trim().toUpperCase(), 0.0);
    }
}
