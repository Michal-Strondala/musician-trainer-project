package com.musiciantrainer.musiciantrainerproject.validation;

import com.musiciantrainer.musiciantrainerproject.user.PasswordDto;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        if (obj instanceof PasswordDto passwordDto) {
            return passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword());
        }
        return false;
    }
//    @Override
//    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
//        final WebUser theUser = (WebUser) obj;
//        return theUser.getPassword().equals(theUser.getMatchingPassword());
//    }
}
