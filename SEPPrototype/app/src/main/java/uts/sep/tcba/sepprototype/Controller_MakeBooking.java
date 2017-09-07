package uts.sep.tcba.sepprototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class Controller_MakeBooking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makebooking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button b = (Button) findViewById(R.id.save);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                String bookingDetails = getDetails();
                intent.putExtra("result", bookingDetails);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public String getDetails(){
        TextView date = (TextView) findViewById(R.id.date);
        TextView time = (TextView) findViewById(R.id.time);
        TextView subject = (TextView) findViewById(R.id.subject);
        TextView tutor = (TextView) findViewById(R.id.tutor);
        TextView location = (TextView) findViewById(R.id.location);
        String booking = date.getText().toString() + " " + time.getText().toString() + " - " + tutor.getText().toString() + " (" + subject.getText().toString() + ")\n" + location.getText().toString();
        return booking;
    }

}
