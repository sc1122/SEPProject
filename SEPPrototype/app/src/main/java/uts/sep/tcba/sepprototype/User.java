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
                firstName = dataSnapshot.child("FirstName").getValue(String.class);
                lastName = dataSnapshot.child("LastName").getValue(String.class);
                email = dataSnapshot.child("Email").getValue(String.class);
                extractAndAddSubjects(dataSnapshot.child("Subjects"));
                type = dataSnapshot.child("Type").getValue(String.class);
                Log.d("USER OBJECT", "CREATED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public User (DataSnapshot data) {
        ID = Integer.parseInt(data.getKey());
        firstName = data.child("FirstName").getValue(String.class);
        lastName = data.child("LastName").getValue(String.class);
        email = data.child("Email").getValue(String.class);
        extractAndAddSubjects(data.child("Subjects"));
        type = data.child("Type").getValue(String.class);
        Log.d("USER OBJECT", "CREATED");
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

    public String getFullName() {
        return this.firstName + " " + this.getLastName();
    }

    public LinkedList<String> getSubjects() {
        return this.subjects;
    }

    public void extractAndAddSubjects(DataSnapshot data) {
        subjects.clear();
        for (DataSnapshot d : data.getChildren()) {
            String subject = d.child("Name").getValue(String.class) + " (" + d.getKey() + ")";
            subjects.add(subject);
        }
    }

    public String getSubjectFromSubjects(int subjectID) {
        for (String subject : subjects) {
            if (subject.contains(subjectID+"")) {
                return subject;
            }
        }
        return null;
    }

    public void cancelConsultation() {

    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " (" + this.ID + ")";
    }
}
