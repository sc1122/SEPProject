package uts.sep.tcba.sepprototype.Model;

import android.util.Log;

import java.io.Serializable;
import java.util.LinkedList;
import com.google.firebase.database.*;

public class Student extends User implements Serializable {

    public LinkedList<Integer> tutors = new LinkedList<Integer>();

    public Student() { }

    public Student(int ID) {
        super(ID);
        fetchTutorsForSubject();
    }

    public Student(DataSnapshot data){
        super(data);
        fetchTutorsForSubject();
    }

    public Student(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.ID = user.getID();
        this.subjects = user.getSubjects();
        this.type = user.getType();
        this.email = user.getEmail();
        fetchTutorsForSubject();
    }

    public void fetchTutorsForSubject() {
        tutors.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + getID() + "/Subjects");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    tutors.add(ds.child("Tutor").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public LinkedList<Integer> getTutors() {
        return tutors;
    }

    public int getTutorsForIndex(int i) {
        return tutors.get(i);
    }
}
