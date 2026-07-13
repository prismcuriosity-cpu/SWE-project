package com.portal.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves the Student Access Center — a hub page of quick links to the
 * university's external portals and services, grouped into sections.
 *
 * <p>The links are defined here as static data and rendered as cards by the
 * {@code access} template.</p>
 */
@Controller
public class AccessCenterController {

    @GetMapping("/access")
    public String accessCenter(Model model) {
        // A LinkedHashMap preserves the section order shown on the page.
        Map<String, List<PortalLink>> sections = new LinkedHashMap<>();

        sections.put("Academic Essentials", List.of(
                new PortalLink("Student Portal", "https://studentportal.diu.edu.bd/",
                        "💻", "Grades, registration, payments and results"),
                new PortalLink("Notice Board", "https://daffodilvarsity.edu.bd/noticeboard",
                        "🔔", "Official university announcements and notices"),
                new PortalLink("Class Routine", "https://routine.zohirrayhan.me/",
                        "🗓️", "Your weekly class schedule and room allocation")));

        sections.put("Learning Resources", List.of(
                new PortalLink("Blended Learning Center", "https://elearn.daffodilvarsity.edu.bd/",
                        "💻", "Online courses, materials and assessments (e-learn)"),
                new PortalLink("Question Bank", "https://diuqbank.com/",
                        "📚", "Previous exam questions and study resources"),
                new PortalLink("Project Management (CSE PMS)", "https://csepms.diu.edu.bd/",
                        "📁", "CSE project & thesis management system")));

        sections.put("Campus Life", List.of(
                new PortalLink("Hall Management", "https://hall.diu.edu.bd/web/login",
                        "🏠", "Residential hall services and login")));

        model.addAttribute("sections", sections);
        return "access";
    }
}
