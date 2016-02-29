package com.appworks.justintime;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayClassActivity extends AppCompatActivity {


    ClassDetailsOpenHelper classDetailsDb;

    List<String> mClassList;
    private ArrayAdapter<String> mClassListAdapter;
    private AdapterView.OnItemClickListener mClassClickedHandler;

    protected static final String TAG = "DisplayClassActivity";

    /**
     *request code for Add new class intent activity
     */
    private static final int ADD_NEW_CLASS_REQUEST_CODE = 1;

    protected String mClassName;
    protected String dayString;
    protected String timeString;
    protected String mAddressString;
    protected int mHour;
    protected int mMinute;
    protected String mAMPM;
    protected double mLatitude;
    protected double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewClassActivity.class);
                startActivityForResult(intent, ADD_NEW_CLASS_REQUEST_CODE);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        classDetailsDb = new ClassDetailsOpenHelper(this);

        /*
        // Fake Weather data
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        */

        Cursor cursor = classDetailsDb.getAllData();

        if (cursor.getCount() > 0) {
            StringBuffer buffer = new StringBuffer();
            mClassList = new ArrayList<>();
            while (cursor.moveToNext()) {
                buffer.append(cursor.getString(1) + ", ");
                buffer.append(cursor.getString(6) + " at ");
                buffer.append(cursor.getString(7));

                mClassList.add(buffer.toString());
                buffer.delete(0, buffer.length());
            }
        }


        mClassListAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_item_class, R.id.list_item_class_textview, mClassList);

        // Get the list view by its id
        ListView listView = (ListView) findViewById(R.id.listview_classes);

        // Set empty text if list view is empty
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        // Set weekForecast adapter on listView
        listView.setAdapter(mClassListAdapter);

        // Create a message handling object as an anonymous class.
        mClassClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Launch Edit View Activity
                //Intent intent = new Intent(getApplicationContext(), EditViewActivity.class);
                //startActivity(intent);
            }
        };

        listView.setOnItemClickListener(mClassClickedHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NEW_CLASS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mClassName = data.getStringExtra("ClassName");
                dayString = data.getStringExtra("day");
                timeString = data.getStringExtra("time");
                mHour = data.getIntExtra("hour", 12);
                mMinute = data.getIntExtra("minute", 0);
                mAMPM = data.getStringExtra("am_pm");
                mAddressString = data.getStringExtra("address");
                mLatitude = data.getDoubleExtra("lat", 0.0);
                mLongitude = data.getDoubleExtra("long", 0.0);

                boolean isInserted = classDetailsDb.insertData(mClassName, dayString, mHour, mMinute, mAMPM,
                        timeString, mAddressString, mLatitude, mLongitude);

                if(isInserted == true){
                    mClassListAdapter.add(mClassName + ", " + timeString + " at " + mAddressString);
                    Toast.makeText(this, "Class Added Successfully!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Class Add Failed", Toast.LENGTH_SHORT).show();
                }

            } else  {
                Log.i(TAG, "Result from ");

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
