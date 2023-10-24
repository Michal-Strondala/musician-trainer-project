package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.Role;

public interface RoleDao {

    public Role findRoleByName(String theRoleName);
}
