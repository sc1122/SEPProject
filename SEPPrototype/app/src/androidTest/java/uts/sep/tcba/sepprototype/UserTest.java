package uts.sep.tcba.sepprototype;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

import uts.sep.tcba.sepprototype.Controller_Authentication;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;


/**
 * Created by Nick Earley on 28/09/2017.
 */
public class UserTest {


    int ID = 456;
    String type = "Tutor";
    String first = "Test";
    String last = "User";

    User testUser = new User(ID);
    private FirebaseAuth mAuth;


    public void userTest() {
        mAuth = FirebaseAuth.getInstance(); // Database connection to Firebase
        mAuth.signInWithEmailAndPassword("456", "password");
    }


    @Test
    public void getID() throws Exception {

        assertEquals(testUser.getID(), ID);

    }

    @Test
    public void getEmail() throws Exception {
        userTest();
        User testUser = new User(ID);
        assertEquals(testUser.getEmail(), ID+"@student.uts.edu.au");

    }

    @Test
    public void getType() throws Exception {

        assertEquals(testUser.getType(), type);
    }

    @Test
    public void getFirstName() throws Exception {
        assertEquals(testUser.getFirstName(), first);
    }

    @Test
    public void getLastName() throws Exception {
        assertEquals(testUser.getLastName(), last);
    }

    @Test
    public void getSubjects() throws Exception {
        LinkedList<String> sbj = new LinkedList<String>();
        sbj.add("stuff");
        assertEquals(testUser.getSubjects(), sbj);
    }

    @Test
    public void cancelConsultation() throws Exception {

    }

}