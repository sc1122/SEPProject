package uts.sep.tcba.sepprototype;

import android.icu.text.SimpleDateFormat;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import org.w3c.dom.Text;

import java.io.Serializable;

public class Controller_MakeAvailability extends AppCompatActivity {

    public Tutor currentUser;
    public String date;
    public double startTime;
    public double endTime;
    public String location;

    private CalendarView cal;
    private TimePicker startTP;
    private TimePicker endTP;
    private EditText locText;


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
              b.putSerializable("availability", (Serializable) getDetails());
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    /*
    Method which gets the details of the availability as selected
    Then creates a new availability object with these parameters
    Then stores this object on firebase
    TODO: Properly initialise an availability object to create it in firebase
     */
    public Availability getDetails(){
        setParameters(cal, startTP, endTP);
        Availability availability = new Availability();
        return availability;
    }


    private void setContent(){
        cal = (CalendarView) findViewById(R.id.calendarView);
        startTP = (TimePicker) findViewById(R.id.startTime);
        endTP = (TimePicker) findViewById(R.id.endTime);
        locText = (EditText) findViewById(R.id.locationText);
        startTP.setIs24HourView(true);
        endTP.setIs24HourView(true);
    }

    /*
    Method that sets the parameters in preparation for the creation of the availability object
     */
    private void setParameters(CalendarView cal, TimePicker start, TimePicker end){
        Long d = cal.getDate();
        date = d.toString();
        startTime = getTime(start);
        endTime = getTime(end);
        location = locText.getText().toString();
        System.out.print(date +", " + startTime + ", " + endTime + ", " + location); //For Debug
    }


    /*
    Returns the time from the time picker as a double, formats as HH.MM
     */
    private double getTime(TimePicker time){
        return ((double)time.getHour() + ((double)time.getMinute() * (0.01)));
    }


}
