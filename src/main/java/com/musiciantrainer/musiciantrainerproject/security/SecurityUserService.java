package com.musiciantrainer.musiciantrainerproject.security;

public interface SecurityUserService {
    String validatePasswordResetToken(String token);
}
