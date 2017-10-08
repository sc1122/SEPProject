package uts.sep.tcba.sepprototype;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import com.google.firebase.database.*;
import java.util.*;

import java.io.Serializable;

public class Controller_MakeAvailability extends AppCompatActivity {

    public Tutor currentUser;
    public String date;
    public double startTime;
    public double endTime;
    public String location;
    public int capacity;


    private CalendarView cal;
    private TimePicker startTP;
    private TimePicker endTP;
    private EditText locText;
    private TextView capText;
    private NumberPicker minutePicker , minutePickerEnd;

    private boolean existingAvailability = true;

    //Field for interval
    private final static int INTERVAL = 30;
    private static final DecimalFormat FORMATTER = new DecimalFormat("00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_availability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = this.getIntent().getExtras();
        currentUser = (Tutor) bundle.getSerializable("user");

        setContent();

        setSupportActionBar(toolbar);
        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = getIntent();
                Bundle b = new Bundle();
                Availability a = getDetails();
                validateData(a);
                if(!existingAvailability) {
                    b.putSerializable("availability", a);
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        Button addButton = (Button) findViewById(R.id.addCapacity);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                int c = Integer.parseInt(capText.getText().toString());
                ++c;
                capText.setText(String.valueOf(c));
            }
        });
        Button removeButton = (Button) findViewById(R.id.removeCapacity);
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                int c = Integer.parseInt(capText.getText().toString());
                if(Integer.parseInt(capText.getText().toString()) > 1) {
                    --c;
                    capText.setText(String.valueOf(c));
                }
            }
        });
    }


    /*
    Method which gets the details of the availability as selected
    Then creates a new availability object with these parameters
    Then stores this object on firebase
     */
    public Availability getDetails(){
        setParameters(cal, startTP, endTP);
        Availability availability = new Availability(date, startTime, endTime, location, capacity);
        return availability;
    }

    /*
    Method that initialises each of the view elements and sets the time pickers to their
    required parameters
     */
    private void setContent(){
        cal = (CalendarView) findViewById(R.id.calendarView);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String correctDay = String.valueOf(dayOfMonth);
                if (correctDay.length() == 1) {
                    correctDay = "0" + String.valueOf(dayOfMonth);
                }
                int correctMonth = month+1;
                String correctYear = String.valueOf(year).substring(2);
                date = String.valueOf(correctDay + "/" + correctMonth + "/") + correctYear;
            }
        });
        startTP = (TimePicker) findViewById(R.id.startTime);
        startTP.setIs24HourView(true);
        startTP.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        endTP = (TimePicker) findViewById(R.id.endTime);
        endTP.setIs24HourView(true);
        endTP.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        capText = (TextView) findViewById(R.id.capacityText);
        locText = (EditText) findViewById(R.id.locationText);
        setMinutePicker();
    }

    /*
    Method that sets the parameters in preparation for the creation of the availability object
     */
    private void setParameters(CalendarView cal, TimePicker start, TimePicker end) {
        startTime = getTime(start);
        endTime = getTime(end);
        capacity = Integer.parseInt(capText.getText().toString());
        location = locText.getText().toString();
    }

    /*
    Returns the time from the time picker as a double, formats as HH.MM
     */
    private double getTime(TimePicker time){
        return ((double)time.getHour() + ((double)time.getMinute() * (0.3)));
    }

    //Change the interval of minute to 30 minutes
    public void setMinutePicker() {
        int numValues = 60 / INTERVAL;
        String[] displayedValues = new String[numValues];
        for (int i = 0; i < numValues; i++) {
            displayedValues[i] = FORMATTER.format(i * INTERVAL);
        }

        View minute = startTP.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        if ((minute != null) && (minute instanceof NumberPicker)) {
            minutePicker = (NumberPicker) minute;
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(numValues - 1);
            minutePicker.setDisplayedValues(displayedValues);
        }
        minute = endTP.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        if ((minute != null) && (minute instanceof NumberPicker)) {
            minutePickerEnd = (NumberPicker) minute;
            minutePickerEnd.setMinValue(0);
            minutePickerEnd.setMaxValue(numValues - 1);
            minutePickerEnd.setDisplayedValues(displayedValues);
        }
    }


    public int getMinute() {
        if (minutePicker != null) {
            return (minutePicker.getValue() * INTERVAL);
        } else {
            return startTP.getCurrentMinute();
        }
    }

    private void validateData(Availability a) {
        //Check if time selected makes sense
        if(selectedTimeIsCorrect(a)) {
            //Check if the availability selected makes sense
            availabilityDoesNotExist(a);
        }else{
            showErrorDialog("Improper Time Selected", "Please select proper time slot");
            existingAvailability = true;
        }
    }

    private boolean selectedTimeIsCorrect(Availability availability){
        double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        double endTime = Double.parseDouble(availability.getEndTime().replace(':','.'));
        if(startTime >= endTime)
            return false;
        return true;
    }



    private void availabilityDoesNotExist(final Availability availability) {
        String tutorId = String.valueOf(currentUser.getID());
        final String date = availability.getDate();
        final double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(tutorId).child("Availabilities");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mainloop:
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String ssStartTime = snapshot.child("startTime").getValue().toString().replace(':','.');
                    String ssEndTime = snapshot.child("endTime").getValue().toString().replace(':','.');
                    //If availability with the same date is found
                    if(snapshot.child("date").getValue().equals(date)) {
                        //if the start time is within the range of existing availability with the same date
                        if((startTime >= Double.parseDouble(ssStartTime) && startTime < Double.parseDouble(ssEndTime))){
                            //show error if it is
                            showErrorDialog("Availability Error", "Input availability already existed");
                            existingAvailability = true;
                            //Break out of the main loop to prevent checking another availability
                            break;
                        }else {
                            //Keep the loop going, and check again if there is existing availability because there might be multiple availability period on one day
                            existingAvailability = false;
                        }
                    } else{
                        existingAvailability = false;
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showErrorDialog(String title, String errorMessage){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }




}
