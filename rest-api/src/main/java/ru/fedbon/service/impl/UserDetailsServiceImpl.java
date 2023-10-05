package ru.fedbon.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userMobileNumber) throws UsernameNotFoundException {

        var user = userRepository.findByUserMobileNumber(userMobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND + userMobileNumber));

        return new org.springframework.security.core.userdetails.User(
                user.getUserMobileNumber(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                true,
                getAuthorities()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("USER"));
    }
}
