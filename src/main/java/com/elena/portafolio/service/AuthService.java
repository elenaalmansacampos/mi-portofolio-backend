package com.elena.portafolio.service;

import com.elena.portafolio.dto.LoginResponse;
import com.elena.portafolio.entity.User;
import com.elena.portafolio.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }


    @Transactional
    public AuthSession login(
            String username,
            String password
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadCredentialsException("Credenciales incorrectas"));

        String accessToken = jwtService.generateToken(user.getUsername());

        RefreshTokenService.IssuedRefreshToken refreshToken =
                refreshTokenService.create(user.getId());

        return new AuthSession(
                new LoginResponse(
                        accessToken,
                        user.getUsername(),
                        user.getRole()
                ),
                refreshToken.token(),
                refreshToken.expiresAt()
        );
    }


    @Transactional
    public AuthSession refresh(String rawRefreshToken) {

        RefreshTokenService.RotatedRefreshToken rotated =
                refreshTokenService.rotate(rawRefreshToken);

        User user = userRepository.findById(rotated.userId())
                .orElseThrow(() ->
                        new BadCredentialsException("Usuario no encontrado"));

        String accessToken = jwtService.generateToken(user.getUsername());

        return new AuthSession(
                new LoginResponse(
                        accessToken,
                        user.getUsername(),
                        user.getRole()
                ),
                rotated.token(),
                rotated.expiresAt()
        );
    }


    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }
}
