package com.harshilInfotech.vibeCoding.service;

import com.harshilInfotech.vibeCoding.dto.auth.AuthResponse;
import com.harshilInfotech.vibeCoding.dto.auth.SignupRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    @Nullable AuthResponse login(LonginRequest request);

}
