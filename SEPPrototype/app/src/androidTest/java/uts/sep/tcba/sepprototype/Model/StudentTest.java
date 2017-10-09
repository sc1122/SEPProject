package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.LinkedList;

import uts.sep.tcba.sepprototype.Model.Student;

import static org.junit.Assert.*;

/**
 * Created by Nick Earley on 4/10/2017.
 */
public class StudentTest {

    // start of student construction

    int ID = 456;
    String type = "Student";
    String first = "Seven";
    String last = "Nine";


    public static Student testStudent = new Student();

    public StudentTest() throws InterruptedException {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID);
        Thread.sleep(5000);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Student testUser = new Student(dataSnapshot);
                set(testUser);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void set(Student thing) {
        this.testStudent = thing;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID + "/Subjects");
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("test","tes5");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    testStudent.tutors.add(ds.child("Tutor").getValue(Integer.class));
                    Log.d("asd",testStudent.getTutors().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });

        Log.d("Stuff", testStudent.tutors.toString());
    }

    // end of student construction
    // start testing


    @Test
    public void getTutors() throws Exception {

        assertEquals(ID , testStudent.getID());

    }


    @Test
    public void viewTutAvailability() throws Exception {
        assertEquals(false , testStudent.getTutors());
    }



    @Test
    public void getTutorsForIndex() throws Exception {
        assertEquals(false , testStudent.getTutors());
    }

}