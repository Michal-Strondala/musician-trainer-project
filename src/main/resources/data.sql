--
-- Dumping initial data for table `user`
--
-- NOTE: The passwords are encrypted using BCrypt
--
-- A generation tool is avail at: http://www.luv2code.com/generate-bcrypt-password
-- https://www.bcryptcalculator.com/
--
-- Default passwords here are: fun123
--
INSERT IGNORE INTO user (email, password) VALUES ('john@john.com','$2a$10$t90CmbfCiJ.Fxm5ckIFiDepXrVp/Q9Onvp4sU9Aro6xnEP1WAD7fi');
INSERT IGNORE INTO user (email, password) VALUES ('mary@mary.com','$2a$10$t90CmbfCiJ.Fxm5ckIFiDepXrVp/Q9Onvp4sU9Aro6xnEP1WAD7fi');
--
-- Dumping initial data for table `role`
--
INSERT IGNORE INTO role (role_name) VALUES ('ROLE_USER');
INSERT IGNORE INTO role (role_name) VALUES ('ROLE_ADMIN');
--
-- Dumping initial data for table `users_roles`
--
INSERT IGNORE INTO `users_roles` (user_id, role_id)
SELECT 1, 1
FROM dual
WHERE NOT EXISTS (
    SELECT * FROM `users_roles` WHERE user_id = 1 AND role_id = 1
);

INSERT IGNORE INTO `users_roles` (user_id, role_id)
SELECT 2, 1
FROM dual
WHERE NOT EXISTS (
    SELECT * FROM `users_roles` WHERE user_id = 2 AND role_id = 1
);

INSERT IGNORE INTO `users_roles` (user_id, role_id)
SELECT 2, 2
FROM dual
WHERE NOT EXISTS (
    SELECT * FROM `users_roles` WHERE user_id = 2 AND role_id = 2
);
