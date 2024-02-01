package com.musiciantrainer.musiciantrainerproject.user;

import com.musiciantrainer.musiciantrainerproject.validation.PasswordMatches;
import com.musiciantrainer.musiciantrainerproject.validation.ValidEmail;
import com.musiciantrainer.musiciantrainerproject.validation.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@PasswordMatches(field = "password", fieldMatch = "matchingPassword", message = "Passwords do not match!")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WebUser {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String name;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    @ValidPassword
    private String matchingPassword;

}
