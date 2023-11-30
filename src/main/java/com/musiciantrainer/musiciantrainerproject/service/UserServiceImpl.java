package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.RoleDao;
import com.musiciantrainer.musiciantrainerproject.dao.UserDao;
import com.musiciantrainer.musiciantrainerproject.entity.Role;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private UserDao userDao;

    private RoleDao roleDao;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findUserByEmail(String email) {
        // check the database if the user already exists
        return userDao.findUserByEmail(email);
    }

    @Override
    public User findById(Long theId) {
        Optional<User> result = userDao.findById(theId);

        User theUser = null;

        if (result.isPresent()) {
            theUser = result.get();
        }
        else {
            // we didn't find the user
            throw new RuntimeException("Did not find user id - " + theId);
        }

        return theUser;
    }

    @Override
    @Transactional
    public void updateUser(User updatedUser) {
        // Retrieve the existing user from the database
        User existingUser = userDao.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if a new password has been provided
        if (!updatedUser.getPassword().startsWith("$2a$")) {
            // If the password does not start with the bcrypt identifier, it's a plain text password
            // Hash the password before saving it to the database
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        } else {
            // If password is already hashed, use the existing password
            updatedUser.setPassword(existingUser.getPassword());
        }

        // Set the roles from the existing user to the updated user
        updatedUser.setRoles(existingUser.getRoles());

        // Update user details using Hibernate
        userDao.save(updatedUser);
    }


    @Override
    @Transactional
    public void save(WebUser webUser) {
        User user = new User();

        // assign user details to the user object
        user.setName(webUser.getName());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setEmail(webUser.getEmail());

        // give user default role of "USER"
        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_USER")));

        // save user in the database
        userDao.save(user);
    }




    // This is inherited method from UserDetails interface
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userDao.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                authorities);
    }



    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }

        return authorities;
    }

}
