package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.validation.PasswordMatches;
import com.musiciantrainer.musiciantrainerproject.validation.ValidEmail;
import com.musiciantrainer.musiciantrainerproject.validation.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches(field = "newPassword", fieldMatch = "confirmPassword", message = "Passwords do not match!")
@NoArgsConstructor
public class PasswordDto {
    private String oldPassword;

    private  String token;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;

    @ValidPassword
    private String newPassword;

    @ValidPassword
    private String confirmPassword;

    public PasswordDto(String token) {
        this.token = token;
        System.out.println("Token set in PasswordDto: " + token);
    }
}
