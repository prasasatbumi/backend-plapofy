package com.finprov.loan.security;

import com.finprov.loan.entity.Role;
import com.finprov.loan.entity.User;
import com.finprov.loan.repository.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    Set<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(Role::getName)
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .collect(Collectors.toSet());
    boolean enabled = user.getIsActive() != null ? user.getIsActive() : true;
    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(authorities)
        .disabled(!enabled)
        .build();
  }
}
