package com.portal.service;

import com.portal.model.Student;
import com.portal.repository.StudentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridges the portal's {@link Student} store into Spring Security by loading a
 * user's credentials and authorities during authentication.
 */
@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public PortalUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No student found with username: " + username));

        return new User(
                student.getUsername(),
                student.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }
}
