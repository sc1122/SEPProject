package uts.sep.tcba.sepprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TimePicker;

import org.w3c.dom.Text;

public class Controller_MakeAvailability extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_availability);

        TimePicker startTime = (TimePicker) findViewById(R.id.startTime);
        startTime.setIs24HourView(true);

        TimePicker endTime = (TimePicker) findViewById(R.id.endTime);
        endTime.setIs24HourView(true);;
    }
}
