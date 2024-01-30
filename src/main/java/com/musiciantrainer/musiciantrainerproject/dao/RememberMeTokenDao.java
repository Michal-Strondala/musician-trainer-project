package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RememberMeTokenDao extends JpaRepository<RememberMeToken, String> {
}
