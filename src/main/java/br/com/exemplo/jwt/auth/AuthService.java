package br.com.exemplo.jwt.auth;

import br.com.exemplo.jwt.auth.AuthDtos.AuthResponse;
import br.com.exemplo.jwt.auth.AuthDtos.LoginRequest;
import br.com.exemplo.jwt.auth.AuthDtos.RegisterRequest;
import br.com.exemplo.jwt.auth.AuthDtos.UserResponse;
import br.com.exemplo.jwt.security.JwtService;
import br.com.exemplo.jwt.user.AppUser;
import br.com.exemplo.jwt.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        AppUser user = users.save(new AppUser(
            request.name().trim(),
            email,
            passwordEncoder.encode(request.password())
        ));
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        AppUser user = users.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }
        return new AuthResponse("Bearer", jwtService.generateToken(user), jwtService.getExpirationMs() / 1000);
    }

    public UserResponse getUser(String email) {
        AppUser user = users.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return toResponse(user);
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
