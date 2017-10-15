package uts.sep.tcba.sepprototype.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class Controller_Authentication extends AppCompatActivity {

    // UI elements
    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;

    public String ID; // String containing the UTS user entered
    public String password; // String containing the password entered
    public String userType; // String containing the type of user logging in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Set up the login form.
        mIdView = (AutoCompleteTextView) findViewById(R.id.IDnumber);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) mPasswordView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mIdSignInButton = (Button) findViewById(R.id.sign_in_button);
        mIdSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mPasswordView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                attemptLogin();
            }
        });
    }

     // Performs pre-login validation of the login form before calling Firebase
    private void attemptLogin() {
        // Clear any input errors
        mIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        ID = mIdView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean preDatabaseValidation = true; // assume validation will pass
        View focusView = null;

        // Check if a UTS user ID has been entered
        if (TextUtils.isEmpty(ID)) {
            mIdView.setError(getString(R.string.error_field_required)); // set message saying field is mandatory
            focusView = mIdView; // switch focus to this field
            preDatabaseValidation = false; // validation failed
        }

        // Check password has been entered and is longer than the password minimum character limit (defined by Firebase Authentication)
        if (password.length() < 8) {
            if (TextUtils.isEmpty(password)) { // if empty
                mPasswordView.setError(getString(R.string.error_field_required)); // set message saying field is mandatory
            } else { // otherwise too short
                mPasswordView.setError("Password is too short - 8 charaters minimum"); // set message password is too short
            }
            focusView = mPasswordView; // switch focus to this field
            preDatabaseValidation = false; // validation failed
        }

        if (preDatabaseValidation) { // if validation has not been failed by checks
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true, false);
            getUserDetails();
        } else {
            // There was an error in validation, set focus to error and display message. No login attempt made to database
            focusView.requestFocus();
        }
    }

    // Ensure user is in the Firebase real-time database and get their email if they are
    public void getUserDetails() {
        String userPath = "Users/" + ID;
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference(userPath); // sets database reference to the user ID entered
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Email").exists()) { // if the reference to the user ID exists and has an email attribute
                    String email = dataSnapshot.child("Email").getValue(String.class); // extract user's email from Firebase
                    userType = dataSnapshot.child("Type").getValue(String.class); // get the user type from Firebase
                    login(email, userType); // Attempt to log in with the user's email and the password they provided
                } else {
                    showProgress(false, false); // stop the progress spinner
                    mIdView.setError("Invalid ID"); // set message saying ID not found
                    mIdView.requestFocus(); // switch focus to this field
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    // Use user's email and password to attempt authentication with Firebase authentication database
    public void login(String email, final String userType) {
        mAuth = FirebaseAuth.getInstance(); // Database connection to Firebase authentication database
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("IMPORTANT", "signInWithEmail:onComplete:" + task.isSuccessful()); // informational debug statement written to log as to whether login succeeded
                        if (task.isSuccessful()) { // if login was successful
                            Intent intent; // create new intent to parse data to next screen/activity
                            if (userType.equals("Tutor")) {
                                // if the user is a tutor, set destination to tutor menu
                                intent = new Intent(Controller_Authentication.this, Controller_TutorMenu.class);
                            } else {
                                // if the user is a student, set destination to student menu
                                intent = new Intent(Controller_Authentication.this, Controller_StudentMenu.class);
                            }
                            intent.putExtra("user", ID); // Pass the user's ID to the their menu
                            clearInputCredentials(); // Reset for next login on device
                            showProgress(false, true); // stop the spinner
                            startActivity(intent); // Go to main menu
                        } else { // if login failed
                            Log.e("AUTHFAIL", "signInWithEmail:failed", task.getException()); // error debug statement written to log
                            showProgress(false, false); // stop the spinner
                            mPasswordView.setError(getString(R.string.error_incorrect_password)); // set message saying password incorrect
                            mPasswordView.requestFocus(); // switch focus to this field
                        }
                    }
                });
    }

    public void clearInputCredentials() {
        // Clear the credential input fields
        TextView id = (TextView) findViewById(R.id.IDnumber);
        TextView pw = (TextView) findViewById(R.id.password);
        id.setText("");
        pw.setText("");
        // Reset ID and password
        ID = "";
        password = "";
    }

    //Shows the progress wheel UI and hides the login form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, boolean ending) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) { // if animation supported
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            // Hide the login form with animation depending on the show variable (false). Opposite, if true
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            // Show the progress wheel with animation depending on the show variable (false). Opposite, if true
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else { // otherwise animation not supported
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        if (ending) { // Keep the login form hidden if login successful
            mLoginFormView.setVisibility(View.GONE);
        }
    }
}

