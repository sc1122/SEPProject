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
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class Controller_TutorMenu extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView listView;
    private FloatingActionButton newAvailabilityButton;
    private ArrayList<Booking> bookings = new ArrayList<Booking>();
    private ArrayList<Availability> availabilities = new ArrayList<Availability>();
    private ArrayList<String> pageList = new ArrayList<String>();
    private ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    private Tutor currentTutor;
    private boolean bookingTab = true;
    private boolean availTab = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    pageList.clear();
                    mTextMessage.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            bookingTab = true;
                            availTab = false;
                            newAvailabilityButton.setVisibility(View.GONE);
                            refreshListView();
                            adapter.notifyDataSetChanged();
                            return true;
                        case R.id.navigation_dashboard:
                            bookingTab = false;
                            availTab = true;
                            newAvailabilityButton.setVisibility(View.VISIBLE);
                            mTextMessage.setText("No availabilities added.\n\nClick the button in the bottom right of the screen to add an availability.");
                            refreshListView();
                            return true;
                        case R.id.navigation_notifications:
                            bookingTab = false;
                            availTab = false;
                            newAvailabilityButton.setVisibility(View.GONE);
                            mTextMessage.setText(R.string.notifications_placeholder);
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Activity Initialisation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutormenu);
        mTextMessage = (TextView) findViewById(R.id.message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_tutor);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Fetch user from previous activity
        Intent intent = getIntent();
        int loggedInUserID = Integer.parseInt(intent.getStringExtra("user"));
        currentTutor = new Tutor(loggedInUserID);

        // Set a listener for when data is updated/loaded to refresh the view
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + loggedInUserID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!getTitle().toString().contains(currentTutor.getFirstName())) {
                    setTitle(getTitle() + " - " + currentTutor.getFirstName() + " " + currentTutor.getLastName() + " (T)");
                }
                // Populate user bookings
                getBookings(String.valueOf(currentTutor.getID()));
                sortBookings();
                // Populate user availabilities
                getAvailabilities(dataSnapshot);
                sortAvailabilities();
                refreshListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });

        // Set ListView UI element to display different lists based on tab selected
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter to bind data to UI list
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

        // Bind the adapted to the UI list
        listView.setAdapter(adapter);

        // Set a listener for clicking item in the ListView to trigger the view booking screen on the pressed list item
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (bookingTab) {
                    Intent intent = new Intent(Controller_TutorMenu.this, Controller_ViewBooking.class);
                    Booking selectedItem = bookings.get(position);
                    Log.d("BOOKING", selectedItem.toString());
                    intent.putExtra("booking", selectedItem);
                    intent.putExtra("subject", currentTutor.getSubjects());
                    intent.putExtra("userType", currentTutor.getType());
                    intent.putExtra("subject", currentTutor.getSubjectFromSubjects(selectedItem.getSubject()));
                    startActivityForResult(intent, 1);
                }
            }
        });

        // Button press now creates a new availability
        newAvailabilityButton = (FloatingActionButton) findViewById(R.id.newAvailability);
        newAvailabilityButton.setVisibility(View.GONE);
        newAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Controller_TutorMenu.this, Controller_MakeAvailability.class);
                Bundle b = new Bundle();
                b.putSerializable("user", currentTutor);
                intent.putExtras(b);
                startActivityForResult(intent, 2);
            }
        });
    }

    public void refreshListView() {
        pageList.clear();
        if (bookingTab) {
            for (Booking b : bookings) {
                pageList.add(b.toString());
            }
        } else if (availTab) {
            for (Availability a : availabilities) {
                pageList.add(a.toString());
            }
        }
        if (pageList.size() > 0) {

            mTextMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    public void getBookings(final String ID) {
        bookings.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookings.clear();
                for (DataSnapshot booking : dataSnapshot.child("Bookings").getChildren()) {
//                    Log.d("BOOKING", booking.toString());
                    String tutorName = currentTutor.getFirstName() + " " + currentTutor.getLastName();
                    if (booking.child("tutor").getValue().toString().equals(ID)) {
                        Booking b = new Booking(booking, tutorName);
                        bookings.add(b);
                    }
                }
                sortBookings();
                refreshListView();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RLdatabase", "Failed");
            }
        });
    }

    public void sortBookings() {
        Collections.sort(bookings, new Comparator<Booking>() {
            public int compare(Booking b1, Booking b2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(b1.getDate() + " " + b1.getStartTime());
                    Log.d("DATE1", date1.toString());
                    Date date2 = formatter.parse(b2.getDate() + " " + b2.getStartTime());
                    Log.d("DATE2", date2.toString());
                    Log.d("COMPARE", String.valueOf(date1.compareTo(date2)));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void getAvailabilities(DataSnapshot dataSnapshot) {
        availabilities.clear();
        for (DataSnapshot data : dataSnapshot.child("Availabilities").getChildren()) {
            availabilities.add(new Availability(data));
        }
    }

    public void sortAvailabilities() {
        Collections.sort(availabilities, new Comparator<Availability>() {
            @Override
            public int compare(Availability a1, Availability a2) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
                try {
                    Date date1 = formatter.parse(a1.getDate() + " " + a1.getStartTime());
                    Log.d("DATE1", date1.toString());
                    Date date2 = formatter.parse(a2.getDate() + " " + a2.getStartTime());
                    Log.d("DATE2", date2.toString());
                    Log.d("COMPARE", String.valueOf(date1.compareTo(date2)));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    @Override
    public void onBackPressed(){
        AlertDialog alertDialog = new AlertDialog.Builder(Controller_TutorMenu.this).create();
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

    // Return method from make availability and from view booking
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Availability availability = (Availability) bundle.getSerializable("availability");
                //Check if selected Time period makes sense to none-time traveller;
                if(selectedTimeIsCorrect(availability)) {
                    availabilityDoesNotExist(availability);
                }else{
                    showErrorDialog("Improper Time Selected", "Please select proper time slot");
                }
                //TODO: Add availability to availability list
            }
            BottomNavigationView view = (BottomNavigationView) findViewById(R.id.navigation_tutor);
            view.setSelectedItemId(R.id.navigation_dashboard);
        }
    }

    public void addAvailabilityToFirebase(Availability availability) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + currentTutor.getID() + "/Availabilities");
        DatabaseReference bookingStatus = ref.push();
        bookingStatus.setValue(availability);
    }

    private boolean selectedTimeIsCorrect(Availability availability){
        double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        double endTime = Double.parseDouble(availability.getEndTime().replace(':','.'));
        if(startTime >= endTime)
            return false;
        return true;
    }

    private void availabilityDoesNotExist(final Availability availability) {
        String tutorId = String.valueOf(currentTutor.getID());
        final String date = availability.getDate();
        final double startTime = Double.parseDouble(availability.getStartTime().replace(':','.'));
        final double endTime = Double.parseDouble(availability.getEndTime().replace(':','.'));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(tutorId).child("Availabilities");
        Log.d("REF", ref.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String ssStartTime = snapshot.child("startTime").getValue().toString().replace(':','.');
                    String ssEndTime = snapshot.child("endTime").getValue().toString().replace(':','.');
                    if(snapshot.child("date").getValue().equals(date)) {
                        if((startTime >= Double.parseDouble(ssStartTime) && startTime < Double.parseDouble(ssEndTime))){
                            showErrorDialog("Availability Error", "Input availability already existed");
                        }else {
                            addAvailabilityToFirebase(availability);
                        }
                    }else{
                        addAvailabilityToFirebase(availability);
                        break;
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
