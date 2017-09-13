package uts.sep.tcba.sepprototype;

import java.util.LinkedList;
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


    public User() {

    }


    public User(int ID) {
        //database to populate/initialise
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
