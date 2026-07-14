package com.bulletinboard.BulletinBoard.user.impl.services;

import com.bulletinboard.BulletinBoard.common.config.JwtTokenProvider;
import com.bulletinboard.BulletinBoard.user.api.dto.JwtResponse;
import com.bulletinboard.BulletinBoard.user.api.dto.LoginRequest;
import com.bulletinboard.BulletinBoard.user.api.dto.users.RegisterRequest;
import com.bulletinboard.BulletinBoard.user.db.entity.User;
import com.bulletinboard.BulletinBoard.user.db.enums.UserStatus;
import com.bulletinboard.BulletinBoard.user.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bulletinboard.BulletinBoard.user.db.enums.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Lazy
    @Autowired
    private AuthService self;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional(rollbackFor = Exception.class)
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("This login already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(USER);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        return self.login(new LoginRequest(request.getLogin(), request.getPassword()));
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        String token = jwtTokenProvider.createToken(
                request.getLogin(),
                authentication.getAuthorities()
        );

        return new JwtResponse(token, request.getLogin());
    }
}
