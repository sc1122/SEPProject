package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import uts.sep.tcba.sepprototype.Model.Student;

import static org.junit.Assert.*;

/**
 * Created by Nick Earley on 4/10/2017.
 */
public class StudentTest {

    // start of student construction

    private int ID = 456;
    private static LinkedList<Integer> tutors = new LinkedList<Integer>();


    private static Student testStudent = new Student();

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


    }

    // end of student construction
    // start testing


    @Test
    public void getTutors() throws Exception {
        tutors.clear();
        tutors.add(123);
        tutors.add(123);

        assertEquals(tutors , testStudent.getTutors());

    }

    public int getTutors(int i) {
       return tutors.get(i);
    }

    @Before
    @Test
    public void getTutorsForIndex() throws Exception {
        tutors.clear();
        Thread.sleep(5000);
        tutors.add(123);
        tutors.add(123);
        assertEquals(getTutors(0), testStudent.getTutorsForIndex(0));
    }

}