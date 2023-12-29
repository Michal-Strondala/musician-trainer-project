package com.musiciantrainer.musiciantrainerproject.security;

import com.musiciantrainer.musiciantrainerproject.dao.PasswordResetTokenDao;
import com.musiciantrainer.musiciantrainerproject.entity.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
@Transactional
public class SecurityUserServiceImpl implements SecurityUserService{
    @Autowired
    private PasswordResetTokenDao passwordResetTokenDao;

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenDao.findByToken(token);

        if (!isTokenFound(passToken)) {
            System.out.println("3. Token not found. Expected: " + token);
            return "invalidToken";
        }

        if (isTokenExpired(passToken)) {
            System.out.println("3. Token expired. Token: " + token);
            return "expired";
        }

        return null;
    }


    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
