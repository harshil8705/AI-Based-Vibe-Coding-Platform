package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.dto.auth.AuthResponse;
import com.harshilInfotech.vibeCoding.dto.auth.LoginRequest;
import com.harshilInfotech.vibeCoding.dto.auth.SignupRequest;
import com.harshilInfotech.vibeCoding.entity.User;
import com.harshilInfotech.vibeCoding.error.BadRequestException;
import com.harshilInfotech.vibeCoding.mapper.UserMapper;
import com.harshilInfotech.vibeCoding.repository.UserRepository;
import com.harshilInfotech.vibeCoding.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest request) {

        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username: " + request.username());
        });

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        return new AuthResponse("dummy", userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}