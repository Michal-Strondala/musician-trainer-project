package com.musiciantrainer.musiciantrainerproject;

import com.musiciantrainer.musiciantrainerproject.dao.PieceDao;
import com.musiciantrainer.musiciantrainerproject.dao.UserDao;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class PieceServiceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PieceService pieceService;
    @Autowired
    private PieceDao pieceDao;
    @MockBean
    private User testUser;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;

    @Value("${sql.script.save.piece}")
    private String sqlAddPiece;
    @Value("${sql.script.delete.piece}")
    private String sqlDeletePiece;
    @Value("${sql.script.save.user}")
    private String sqlAddUser;
    @Value("${sql.script.delete.user}")
    private String sqlDeleteUser;
    @Value("${sql.script.save.usersroles}")
    private String sqlAddUsersRoles;
    @Value("${sql.script.delete.usersroles}")
    private String sqlDeleteUsersRoles;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddUser);
        jdbc.execute(sqlAddUsersRoles);
        jdbc.execute(sqlAddPiece);
    }

    @Test
    public void isPieceNullCheck() {

        assertTrue(pieceService.checkIfPieceIsNotNull(999L), "Piece is created in sql script : return true");

        assertFalse(pieceService.checkIfPieceIsNotNull(0L), "No piece should have 0 id : return false");
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback
    public void addPieceService() {

        // Create new piece
        Piece testPiece = new Piece();
        testPiece.setName("TestPiece");
        testPiece.setComposer("Test Composer");
        testPiece.setPriority((short) 2);

        // Save the user to the database
        entityManager.persist(testUser);
        entityManager.flush();

        // Set the user in the piece
        testPiece.setUser(testUser);

        // Add the new piece to the DB
        pieceService.addPiece(testPiece, testUser);

        // Retrieve the piece from the DB
        Piece verifyPiece = pieceDao.findByName("TestPiece");

        // Check if the piece was added successfully
        assertNotNull(verifyPiece, "Piece shout not be null");
        assertEquals("TestPiece", verifyPiece.getName(), "Piece name should match");
        assertEquals("Test Composer", verifyPiece.getComposer(), "Piece composer should match");
        assertEquals((short) 2, verifyPiece.getPriority(), "Piece priority should match");
    }


    @Test
    public void deletePieceService() {

        // find the test piece to be deleted
        Optional<Piece> deletedPiece = pieceDao.findById(999L);

        // check if the test piece exists
        assertTrue(deletedPiece.isPresent(), "Should be present : true");

        // delete the test piece
        pieceService.deletePiece(999L);

        // check if it was deleted successfully
        deletedPiece = pieceDao.findById(999L);

        assertFalse(deletedPiece.isPresent(), "The piece should NOT be present : false");
    }


    @Test
    public void editPieceService() {

        // find the test piece to be edited
        Piece pieceToBeEdited = pieceDao.findById(999L).orElseThrow(() -> new RuntimeException("Piece not found"));

        // check if the piece to be edited exists
        assertTrue(pieceService.checkIfPieceIsNotNull(999L));

        // edit the piece
        pieceToBeEdited.setName("Edited name");
        pieceToBeEdited.setComposer("Edited composer");
        pieceToBeEdited.setPriority((short) 3);

        // edit the piece in DB
        User theUser = userDao.findById(4L).orElseThrow(() -> new RuntimeException("User not found"));
        pieceService.editPiece(pieceToBeEdited,theUser);

        // retrieve the edited piece from DB
        Piece editedPiece = pieceDao.findById(999L).orElseThrow(() -> new RuntimeException("Piece not found"));

        // Check if the user was updated successfully
        assertNotNull(editedPiece, "Piece should not be null");
        assertEquals("Edited name", editedPiece.getName(), "Piece name should match");
        assertEquals("Edited composer", editedPiece.getComposer(), "Piece composer should match");
        assertEquals((short) 3, editedPiece.getPriority(), "Piece priority should match");
    }


    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeletePiece);
        jdbc.execute(sqlDeleteUsersRoles); // sqlDeleteUsersRoles must come before sqlDeleteUser, because this way it
        // will not delete the role, only the record in users_roles table
        jdbc.execute(sqlDeleteUser);
    }

}
