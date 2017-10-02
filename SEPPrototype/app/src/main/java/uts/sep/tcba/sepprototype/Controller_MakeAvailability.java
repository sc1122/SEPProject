package uts.sep.tcba.sepprototype;

import android.content.res.Resources;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.NumberPicker;
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
    private NumberPicker minutePicker;

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
                b.putSerializable("availability", getDetails());
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
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
        startTP = (TimePicker) findViewById(R.id.startTime);
        endTP = (TimePicker) findViewById(R.id.endTime);
        locText = (EditText) findViewById(R.id.locationText);
        capText = (TextView) findViewById(R.id.capacityText);
        startTP.setIs24HourView(true);
        endTP.setIs24HourView(true);
        startTP.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        endTP.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        setMinutePicker();
    }

    /*
    Method that sets the parameters in preparation for the creation of the availability object
     */
    private void setParameters(CalendarView cal, TimePicker start, TimePicker end) {
        date = getFormattedDate(cal);
        startTime = getTime(start);
        endTime = getTime(end);
        location = locText.getText().toString();
        capacity = Integer.parseInt(capText.getText().toString());
    }


    /*
    Returns the time from the time picker as a double, formats as HH.MM
     */
    private double getTime(TimePicker time){
        return ((double)time.getHour() + ((double)time.getMinute() * (0.01)));
    }

    private String getFormattedDate(CalendarView cal){
        Long l = cal.getDate();
        Date d = new Date(l);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        return formatter.format(d);
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
            minutePicker = (NumberPicker) minute;
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(numValues - 1);
            minutePicker.setDisplayedValues(displayedValues);
        }
    }


    public int getMinute() {
        if (minutePicker != null) {
            return (minutePicker.getValue() * INTERVAL);
        } else {
            return startTP.getCurrentMinute();
        }
    }

}
