package uts.sep.tcba.sepprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Controller_ViewBooking extends AppCompatActivity {

    public Booking currentBooking;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        Bundle bundle = this.getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent().getStringExtra("userType").equals("Student")) {
            editButton = (Button) findViewById(R.id.edit);
            editButton.setVisibility(View.GONE);
        }


    }
}
