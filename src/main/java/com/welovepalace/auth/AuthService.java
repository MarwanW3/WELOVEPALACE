package com.welovepalace.auth;

import com.welovepalace.user.AppUser;
import com.welovepalace.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public void register(String username, String password) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        repo.save(new AppUser(username, encoder.encode(password)));
    }

    public boolean login(String username, String password) {
        return repo.findByUsername(username)
                .map(u -> encoder.matches(password, u.getPasswordHash()))
                .orElse(false);
    }
}
