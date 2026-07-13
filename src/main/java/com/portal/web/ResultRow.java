package com.portal.web;

/**
 * A single row on the Academic Result page: one course's outcome for the
 * selected semester, including the grade point derived from the letter grade.
 */
public record ResultRow(String courseCode, String courseTitle, int credits,
                        double marks, String grade, double gradePoint) {
}
