package uts.sep.tcba.sepprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Controller_StudentMenu extends AppCompatActivity{

private TextView mTextMessage;
private ListView listView;
private FloatingActionButton newBookingButton;
private ArrayList<String> bookings = new ArrayList<String>();
private ArrayList<String> subjects = new ArrayList<String>();
private ArrayList<String> pageList = new ArrayList<String>();
private ArrayAdapter adapter;
private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    pageList.clear();
                    mTextMessage.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            newBookingButton.setVisibility(View.VISIBLE);
                            pageList.addAll(bookings);
                            adapter.notifyDataSetChanged();
                            if (bookings.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            return true;
                        case R.id.navigation_dashboard:
                            newBookingButton.setVisibility(View.GONE);
                            pageList.addAll(subjects);
                            adapter.notifyDataSetChanged();
                            if (subjects.size() > 0) {
                                mTextMessage.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                            return true;
                        case R.id.navigation_notifications:
                            newBookingButton.setVisibility(View.GONE);
                            mTextMessage.setText(R.string.notifications_placeholder);
                            return true;
                    }
                    return false;
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        populateData();
        pageList = (ArrayList<String>) bookings.clone();

        listView = (ListView) findViewById(R.id.list);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object o = listView.getItemAtPosition(position);
            }
        });

        // Define a new Adapter to bind data
        // First parameter - Context, Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written, Fourth - the Array of data
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, pageList);
        // Assign adapter to ListView

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pageList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = ((TextView) view.findViewById(android.R.id.text1));
                textView.setMinHeight(0); // Min Height
                textView.setMinimumHeight(0); // Min Height
                textView.setHeight(200); // Height
                textView.setTextSize(16);
                return view;
            }
        };


        listView.setAdapter(adapter);

        newBookingButton = (FloatingActionButton) findViewById(R.id.newBooking);
        newBookingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_StudentMenu.this, Controller_MakeBooking.class);
                intent.putExtra("caller", "Controller_StudentMenu");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void populateData() {
        bookings.clear();
        bookings.add("12/09/2017 14:00 - Dr. Qureshi (48440)\nFEIT Learning Precinct, Building 11 Level 5");
        bookings.add("14/09/2017 11:00 - Cam Reeves (41900)\nBuilding 11 Level B1, Room 401");
        bookings.add("01/10/2017 13:30 - Dr. McBride (31281)\nFEIT Learning Precinct, Building 11 Level 5");
        bookings.add("21/10/2017 11:00 - Cam Reeves (41900)\nBuilding 11 Level B1, Room 401");
        subjects.clear();
        subjects.add("Fundamentals of Security (41900)");
        subjects.add("Systems Development Project (31281)");
        subjects.add("Software Engineering Practice (48440)");
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder(Controller_StudentMenu.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Would you like to log out?");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                String booking = data.getStringExtra("result");
                bookings.add(booking);
                pageList.add(booking);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
