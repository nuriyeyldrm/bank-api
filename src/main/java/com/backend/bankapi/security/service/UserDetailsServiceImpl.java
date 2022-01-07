package com.backend.bankapi.security.service;

import com.backend.bankapi.domain.User;
import com.backend.bankapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String ssn) throws UsernameNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with SSN: " + ssn));

        return UserDetailsImpl.build(user);
    }
}
