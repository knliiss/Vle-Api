package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.repo.UserRepo;
import dev.knalis.vleapi.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findFirstByUsernameIgnoreCase(username)
                .map(UserDetailsImpl::new)
                .orElseGet(() -> userRepo.findByUsernameIgnoreCase(username).stream().findFirst()
                        .map(UserDetailsImpl::new)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)));
    }

    public boolean userExists(String username) {
        return userRepo.existsByUsernameIgnoreCase(username);
    }
}
