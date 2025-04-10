package com.technical_test_Cuscatlan_adga.technical_test_adga.services;

import com.technical_test_Cuscatlan_adga.technical_test_adga.models.repositories.UserRepository;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.User;
import com.technical_test_Cuscatlan_adga.technical_test_adga.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(user);
    }

}
