package uts.sep.tcba.sepprototype;

import java.util.LinkedList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firebase_core.*;
import com.google.firebase.auth.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.*;
import com.google.firebase.iid.*;
import com.google.firebase.provider.*;
import com.google.firebase.*;


public class User {
    public int ID;
    public String firstName;
    public String lastName;
    public LinkedList<Integer> subjects;
    public boolean isStudent;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userReference;

    public User() {
    }


    public User(String userID) {
        //database to populate/initialise
        userDatabase = FirebaseDatabase.getInstance();
        userReference = userDatabase.getReference(userID);
    }

    public int getID() {
        return ID;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LinkedList<Integer> getSubjects() {
        return subjects;
    }

    public void cancelConsultation() {

    }

}
