package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.PasswordResetToken;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.dto.WebUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    PasswordResetToken getPasswordResetToken(String token);

    void deletePasswordResetToken(PasswordResetToken passwordResetToken);

    void save(WebUser webUser);

    public User findUserByEmail(String email);

    User findById(Long theId);

    void updateUser(User updatedUser);

    void createPasswordResetTokenForUser(User theUser, String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changeUserPassword(User theUser, String password);

    boolean checkIfUserIsNotNull(Long id);
}
