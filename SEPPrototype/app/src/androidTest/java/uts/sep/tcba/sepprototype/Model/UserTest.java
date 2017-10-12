package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

import com.google.firebase.database.*;

import uts.sep.tcba.sepprototype.Model.User;


/**
 * Created by Nick Earley on 28/09/2017.
 */
public class UserTest {

    // Create test user


    private int ID = 456;
    private String type = "Student";
    private String first = "Seven";
    private String last = "Nine";


    private static User testUser = new User();

    public UserTest() throws InterruptedException {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID);
        Thread.sleep(5000);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User testUser = new User(dataSnapshot);
                set(testUser);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void set(User thing) {
        this.testUser = thing;
    }


    // End of creation of test user

    // start testing


    @Test
    public void getID() throws Exception {

        assertEquals(testUser.getID(), ID);

    }

    @Test
    public void getEmail() throws Exception {

        assertEquals(testUser.getEmail(), ID + "@student.uts.edu.au");

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
        sbj.add("Test Subject One (13418)");
        sbj.add("Fundamentals of Security (41900)");
        assertEquals(testUser.getSubjects(), sbj);
    }

    @Test
    public void cancelConsultation() throws Exception {

    }

}