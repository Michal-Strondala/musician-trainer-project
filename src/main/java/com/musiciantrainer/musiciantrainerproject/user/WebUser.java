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

@Getter
@Setter
@NoArgsConstructor
@ToString
@PasswordMatches
public class WebUser {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String name;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;
}
