package br.com.exemplo.jwt.auth;

import br.com.exemplo.jwt.auth.AuthDtos.AuthResponse;
import br.com.exemplo.jwt.auth.AuthDtos.LoginRequest;
import br.com.exemplo.jwt.auth.AuthDtos.RegisterRequest;
import br.com.exemplo.jwt.auth.AuthDtos.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Cadastrar usuário")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Autenticar e gerar JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Consultar usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public UserResponse me(java.security.Principal principal) {
        return authService.getUser(principal.getName());
    }
}
