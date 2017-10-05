package uts.sep.tcba.sepprototype;

import android.util.Log;
import android.widget.TextView;

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


    User testUser = new User();

    public UserTest() throws InterruptedException {

        testUser.firstName = first;
        testUser.lastName = last;
        testUser.type = type;
        testUser.ID = ID;
        testUser.email = ID + "@student.uts.edu.au";

        Log.d("Valid", "hello");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID);
        Log.d("HI", ref.toString());
        testUser.subjects.add("stuff");

        Thread.sleep(100);
        /*try
        {
            Thread.sleep(1000);
        }catch(InterruptedException ie) {
        }*/

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("Test2", "Test2");
                User testUser = new User(dataSnapshot);
                Log.d("ToString",testUser.toString() );
//                asb(testUser);
                if(testUser.getID() == (ID)){
                    Log.d("Currently","Test4");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });

       // Log.d("THIRD", testUser.toString());


    }

//    public User asb(User thing) {
//        Log.d("ANOTHER", thing.toString());
//        this.testUser = thing;
//        Log.d("ONE", testUser.toString());
//        return thing;
//
//    }


    @Test
    public void getID() throws Exception {
//        Log.d("NewString",testUser.toString());
//        if(testUser == null){
//            Log.d("Null","Null");
//        };
//        if(testUser == null) {
//            Log.d("LUL", "LUL");
//        }
        assertEquals(testUser.getID(), ID);

    }

    @Test
    public void getEmail() throws Exception {

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