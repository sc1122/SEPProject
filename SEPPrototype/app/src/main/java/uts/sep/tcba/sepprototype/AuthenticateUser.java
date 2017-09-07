package uts.sep.tcba.sepprototype;


import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticateUser {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public AuthenticateUser(int ID, String password){
        mAuth = FirebaseAuth.getInstance(); // Database connection to Firebase

    }

    public void login() {
        String email = "12545455@student.uts.edu.au";
        String password = "heyitsnick";
    }

    public void logout(){

    }
}
