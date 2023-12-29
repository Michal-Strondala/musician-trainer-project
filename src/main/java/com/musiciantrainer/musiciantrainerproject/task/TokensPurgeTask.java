package com.musiciantrainer.musiciantrainerproject.task;

import com.musiciantrainer.musiciantrainerproject.dao.PasswordResetTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
@Service
@Transactional
public class TokensPurgeTask {

    @Autowired
    PasswordResetTokenDao passwordResetTokenDao;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {

        Date now = Date.from(Instant.now());

        passwordResetTokenDao.deleteAllExpiredSince(now);
    }
}
