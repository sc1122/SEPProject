package uts.sep.tcba.sepprototype;

import android.util.Log;
import com.google.firebase.database.*;
import java.io.Serializable;
import java.util.LinkedList;

public class User implements Serializable {
    public int ID;
    public String firstName;
    public String lastName;
    public String email;
    public LinkedList<String> subjects = new LinkedList<String>();
    public String type;

    public User() { }

    public User(int ID) {
        this.ID = ID;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + String.valueOf(ID));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("FirstName").getValue().toString();
                lastName = dataSnapshot.child("LastName").getValue().toString();
                email = dataSnapshot.child("Email").getValue().toString();
                extractAndAddSubjects(dataSnapshot.child("Subjects"));
                for (String s: subjects) {
                    Log.d("SUBJECTS1", s.toString());
                }
                type = dataSnapshot.child("Type").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public int getID() { return this.ID; }

    public String getEmail() { return this.email; }

    public String getType() { return this.type; }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public LinkedList<String> getSubjects() {
        return this.subjects;
    }

    public void extractAndAddSubjects(DataSnapshot data) {
        subjects.clear();
        for (DataSnapshot d : data.getChildren()) {
            String subject = d.getKey().toString() + " - " + d.child("Name").getValue().toString();
            subjects.add(subject);
        }
    }

    public void cancelConsultation() {

    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " (" + this.ID + ")";
    }
}
