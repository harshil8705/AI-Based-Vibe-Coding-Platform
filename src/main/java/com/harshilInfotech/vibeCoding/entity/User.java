package com.harshilInfotech.vibeCoding.entity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Long id;

    String email;
    String passwordHash;
    String name;

    String avatarUrl;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;

}
