package ru.fedbon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Username is required")
    @Column(name = "user_mobile_number")
    private String userMobileNumber;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @Column(name = "created")
    private Instant created;

    @Column(name = "enabled")
    private Boolean enabled;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userMobileNumber='" + userMobileNumber + '\'' +
                ", created=" + created +
                ", enabled=" + enabled +
                '}';
    }
}
