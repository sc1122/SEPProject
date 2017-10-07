package uts.sep.tcba.sepprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class Controller_ViewBooking extends AppCompatActivity {

    public Booking currentBooking;
    private String userID;
    private String userType;
    private Button cancelButton, editButton;
    private TextView subject , tutor, date, time, location, capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);


        Bundle bundle = this.getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");
        userID = bundle.getString("id");
        userType = bundle.getString("userType");

        //Toolbar setting, disable edit button for student
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(userType.equals("Student")) {
            editButton = (Button) findViewById(R.id.edit);
            editButton.setVisibility(View.GONE);
        }

        //Log.d("DETAIL", currentBooking.toString());
        setContent(bundle.getString("subject"));

        //Cancel Button Code
        cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference().child("Bookings/" + currentBooking.getBookingID());
                if (userType.equals("Student")) {
                    if (currentBooking.getStudents().size() > 1) {
                        ref.child("students").child(userID).setValue(null);
                    } else {
                        ref.setValue(null);
                    }
                } else if (userType.equals("Tutor")) {
                    ref.setValue(null);
                }
                finish();
            }
        });

        //Edit Button on Tool bar
        editButton = (Button) findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Controller_EditBooking.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void setContent(String subjectString) {
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.location = (TextView) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);

        subject.setText(subjectString);

        tutor.setText(currentBooking.getTutorName() + " (" + currentBooking.getTutor() + ")");
        date.setText(currentBooking.getDate());
        time.setText(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
        location.setText(currentBooking.getLocation());
        capacity.setText(  currentBooking.getStudents().size() + " / "+ currentBooking.getCapacity());
    }



}
