package ru.fedbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userMobileNumber;

    private String password;

    private Instant createdDate;

    private Long userId;
}
