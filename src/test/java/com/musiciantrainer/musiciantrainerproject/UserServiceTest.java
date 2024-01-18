//package com.musiciantrainer.musiciantrainerproject;
//
//import com.musiciantrainer.musiciantrainerproject.dao.UserDao;
//import com.musiciantrainer.musiciantrainerproject.entity.User;
//import com.musiciantrainer.musiciantrainerproject.service.UserService;
//import com.musiciantrainer.musiciantrainerproject.user.WebUser;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//@TestPropertySource("/application-test.properties")
//@SpringBootTest
//public class UserServiceTest {
//    @Autowired
//    private JdbcTemplate jdbc;
//    @Autowired
//    private UserDao userDao;
//    @Autowired
//    private UserService userService;
//
//    @Value("${sql.script.save.user}")
//    private String sqlAddUser;
//    @Value("${sql.script.delete.user}")
//    private String sqlDeleteUser;
//    @Value("${sql.script.save.usersroles}")
//    private String sqlAddUsersRoles;
//    @Value("${sql.script.delete.usersroles}")
//    private String sqlDeleteUsersRoles;
//
//
//    @BeforeEach
//    public void setupDatabase() {
//        jdbc.execute(sqlAddUser);
//        jdbc.execute(sqlAddUsersRoles);
//    }
//
//
//    @Test
//    public void isUserNullCheck() {
//
//        assertTrue(userService.checkIfUserIsNotNull(1L), "User is created in sql script : return true");
//
//        assertFalse(userService.checkIfUserIsNotNull(0L), "No user should have 0 id : return false");
//    }
//
//    @Test
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//        // This ensures that the test method runs in a separate transaction that will be rolled back after the test.
//        // This way, any changes made during the test (including the save operation) won't be committed to the actual database.
//    @Rollback
//        // explicitly indicates that the transaction should be rolled back after the test completes
//    public void saveUserService() {
//
//        // Create a WebUser object with some sample data
//        WebUser testWebUser = new WebUser();
//        testWebUser.setName("Bolek Lolek");
//        testWebUser.setEmail("bolek.lolek@example.com");
//        testWebUser.setPassword("abcd1234!");
//
//        // Save the user using the service method
//        userService.save(testWebUser);
//
//        // Retrieve the user from the database based on the email
//        User verifyUser = userDao.findUserByEmail("bolek.lolek@example.com");
//
//        // Check if the user was saved successfully
//        assertNotNull(verifyUser, "User should not be null");
//        assertEquals("Bolek Lolek", verifyUser.getName(), "User name should match");
//        assertEquals("bolek.lolek@example.com", verifyUser.getEmail(), "User email should match");
//        assertTrue(verifyUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")),
//                "User should have the ROLE_USER role");
//    }
//
//
//
//
//    @Test
//    public void updateUserService() {
//        // Check if the user with id = 4 exists
//        assertTrue(userService.checkIfUserIsNotNull(4L));
//
//        // Retrieve the user with id = 4
//        User existingUser = userDao.findById(4L).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Modify the existing user's details
//        existingUser.setName("Chad Darby");
//        existingUser.setPassword("Ostrava123!");
//        existingUser.setEmail("chad.darby@example.com");
//
//        // Call the update method in the service
//        userService.updateUser(existingUser);
//
//        // Retrieve the updated user from the database
//        User updatedUser = userDao.findById(4L).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Check if the user was updated successfully
//        assertNotNull(updatedUser, "User should not be null");
//        assertEquals("Chad Darby", updatedUser.getName(), "User name should match");
//        assertEquals("chad.darby@example.com", updatedUser.getEmail(), "User email should match");
//        assertTrue(updatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")),
//                "User should have the ROLE_USER role");
//
//
//    }
//
//
//    @AfterEach
//    public void setupAfterTransaction() {
//        jdbc.execute(sqlDeleteUsersRoles); // sqlDeleteUsersRoles must come before sqlDeleteUser, because this way it
//                                            // will not delete the role, only the record in users_roles table
//        jdbc.execute(sqlDeleteUser);
//    }
//}
