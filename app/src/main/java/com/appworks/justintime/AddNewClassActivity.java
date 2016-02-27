package com.appworks.justintime;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Calendar;


public class AddNewClassActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static TextView mDayView;
    private static TextView mTimeView;
    private static String timeString;
    private static String dayString;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDayView = (TextView) findViewById(R.id.day);
        mTimeView = (TextView) findViewById(R.id.time);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // set default day and time of a class
        setDefaultDayTime();

        // set day picker as on click listener on day text view
        mDayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog();
            }
        });

        // set time picker as on click listener on day text view
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* Test Code for Google Maps
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    // Set default day and time
    private void setDefaultDayTime() {
        // Set Default day
        dayString = "Sunday";
        mDayView.setText(dayString);

        // Set default time
        setTimeString(12, 0, 0, DateFormat.is24HourFormat(getApplicationContext()));
        mTimeView.setText(timeString);
    }

    // DialogFragment to pick a day for a class
    public static class DayPickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pick_day)
                    .setItems(R.array.days_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which){
                                case 0:
                                    dayString = "Sunday";
                                    break;
                                case 1:
                                    dayString = "Monday";
                                    break;
                                case 2:
                                    dayString = "Tuesday";
                                    break;
                                case 3:
                                    dayString = "Wednesday";
                                    break;
                                case 4:
                                    dayString = "Thursday";
                                    break;
                                case 5:
                                    dayString = "Friday";
                                    break;
                                case 6:
                                    dayString = "Saturday";
                                    break;
                            }

                            mDayView.setText(dayString);
                        }
                    });
            return builder.create();
        }
    }

    private static void setTimeString(int hourOfDay, int minute, int am_pm, boolean is24HourFormat) {
        String hour = "" + hourOfDay;
        String min = "" + minute;
        String append = "";

        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        }
        if (minute < 10)
            min = "0" + minute;
        if (am_pm == Calendar.PM)
            append = " PM";
        else if(am_pm == Calendar.AM)
            append = " AM";

        if(is24HourFormat)
            append = "";


        timeString = hour + ":" + min + append;
    }

    // DialogFragment to pick a time for a class
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

            setTimeString(hourOfDay, minute, c.get(Calendar.AM_PM), DateFormat.is24HourFormat(getActivity()));

            mTimeView.setText(timeString);
        }
    }

    // Show day picker dialog
    private void showDayPickerDialog() {
        DialogFragment newFragment = new DayPickerFragment();
        newFragment.show(getSupportFragmentManager(), "dayPicker");
    }

    // Show time picker dialog
    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
