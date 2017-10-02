package uts.sep.tcba.sepprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Controller_ViewBooking extends AppCompatActivity {

    public Booking currentBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        Bundle bundle = this.getIntent().getExtras();
        currentBooking = (Booking) bundle.getSerializable("booking");



    }
}
