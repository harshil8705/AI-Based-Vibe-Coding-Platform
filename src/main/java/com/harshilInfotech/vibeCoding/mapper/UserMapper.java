package com.harshilInfotech.vibeCoding.mapper;

import com.harshilInfotech.vibeCoding.dto.auth.SignupRequest;
import com.harshilInfotech.vibeCoding.dto.auth.UserProfileResponse;
import com.harshilInfotech.vibeCoding.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

}