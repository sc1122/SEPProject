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

    public User(String ID) {
        this.ID = Integer.parseInt(ID);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("FirstName").getValue().toString();
                lastName = dataSnapshot.child("LastName").getValue().toString();
                email = dataSnapshot.child("Email").getValue().toString();
                extractAndAddSubjects(dataSnapshot.child("Subjects").getValue().toString());
                for (String s: subjects) {
                    Log.d("SUBJECTS", s.toString());
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

    public void extractAndAddSubjects(String subjectJSON) {
        subjects.clear();
        subjectJSON = subjectJSON.substring(1, subjectJSON.length()-1);
        String[] subjectsString = subjectJSON.split("=|,");
        int i = 0;
        String subjectString = "";
        for (String s: subjectsString) {
            if (i % 2 == 0) {
                subjectString = s.trim();
            } else {
                subjects.add(subjectString + " - " + s.trim());
                subjectString = "";
            }
            i = i + 1;
        }
    }

    public void cancelConsultation() {

    }

    @Override
    public String toString() {
        return this.ID + "|" + this.firstName + "|" + this.lastName + "|" + this.type + "|" + this.subjects;
    }
}
