package com.harshilInfotech.vibeCoding.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionId implements Serializable {

    Long projectId;
    Long userId;

}
