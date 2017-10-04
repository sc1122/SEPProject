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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class Controller_ViewBooking extends AppCompatActivity {

    public Booking currentBooking;
    private Button cancelButton, editButton;
    private TextView subject , tutor, date, time, location, capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);


        Bundle bundle = this.getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");

        //Toolbar setting, disable edit button for student
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent().getStringExtra("userType").equals("Student")) {
            cancelButton = (Button) findViewById(R.id.edit);
            cancelButton.setVisibility(View.GONE);
        }

        //Log.d("DETAIL", currentBooking.toString());
        setContent(currentBooking);

        //Cancel Button Code
        cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference().child("Bookings");

                Log.d("BOOKINGS", ref.child("Bookings").toString());
            }
        });

        //Edit Button on Tool bar
        editButton = (Button) findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Controller_EditBooking.class);
                Bundle bundle =  getIntent().getExtras();
                intent.putExtras(bundle);
                intent.putExtra("userType", getIntent().getStringExtra("userType"));
                startActivityForResult(intent, 2);
            }
        });
    }

    private void setContent(Booking currentBooking) {
        this.subject = (TextView) findViewById(R.id.subject);
        this.tutor = (TextView) findViewById(R.id.tutor);
        this.date = (TextView) findViewById(R.id.date);
        this.time = (TextView) findViewById(R.id.time);
        this.location = (TextView) findViewById(R.id.location);
        this.capacity = (TextView) findViewById(R.id.capacity);

        subject.setText(String.valueOf(currentBooking.getSubject()) );


        tutor.setText(currentBooking.getTutor() + currentBooking.getTutorName());
        date.setText(currentBooking.getDate());
        time.setText(currentBooking.getStartTime() + " - " + currentBooking.getEndTime());
        location.setText(currentBooking.getLocation());
        capacity.setText(  currentBooking.getStudents().size() + " / "+ currentBooking.getCapacity());
    }

    //not sure how to split start time & end time (whether we need two separate methods for each idk)
    public void handleSTime() {
        double sTime = Double.parseDouble(time.getText().toString());
        currentBooking.setStartTime(sTime);
    }

//    public void handleETime() {
//        double eTime = Double.parseDouble(time.getText().toString());
//        currentBooking.setEndTime(eTime);
//    }

}
