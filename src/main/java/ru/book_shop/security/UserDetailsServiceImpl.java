package ru.book_shop.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.book_shop.entities.user.User;
import ru.book_shop.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository
                .findById(Integer.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("api.error.login.user-not-found"));

        return UserDetailsImpl.builder()
                .userName(String.valueOf(user.getId()))
                .password(user.getPassword())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
