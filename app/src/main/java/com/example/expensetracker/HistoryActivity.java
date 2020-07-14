package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    Integer month, year, this_month, this_year;
    Date date;
    HashMap<Integer, Entry> monthEntry, dayEntry;
    MonthAdapter customAdapter_month;
    DayAdapter customAdapter_day;
    ListView monthView,dayView;
    DatabaseHelper db;
    Button edit_button;
    Integer income, remain;
    TextView date_text;
    TextView monthremain_text,dayremain_text;
    GestureDetectorCompat gestureDetectorCompat = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_day:
                    Intent ActivityChange_today = new Intent(HistoryActivity.this, MainActivity.class);
                    startActivity(ActivityChange_today);
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    return true;
                case R.id.navigation_month:
                    Intent ActivityChange_tomonth = new Intent(HistoryActivity.this, MonthActivity.class);
                    startActivity(ActivityChange_tomonth);
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    return true;
                case R.id.navigation_history:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_history);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        db = new DatabaseHelper(this);
        this_month = month = Integer.parseInt(new SimpleDateFormat("M").format(Calendar.getInstance().getTime()));
        this_year = year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        SimpleDateFormat date_format = new SimpleDateFormat("MMM yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("M-yyyy");
        date = Calendar.getInstance().getTime();
        try {
            date = sdf.parse(month+"-"+year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String showdate = date_format.format(date);
        date_text = findViewById(R.id.date_textview);
        date_text.setText(showdate);

        //acquire data from db
        income = db.getIncome(year,month);
        monthEntry = db.getMonthEntry(year,month);
        dayEntry = db.getEntry(year,month);

        //Set text
        TextView income_text = findViewById(R.id.income);
        monthremain_text = findViewById(R.id.monthremain_text);
        dayremain_text = findViewById(R.id.dayremain_text);
        income_text.setText(income.toString());

        //listView
        monthView = findViewById(R.id.monthlistview);
        customAdapter_month = new MonthAdapter(monthEntry, this, false, year, month);
        monthView.setAdapter(customAdapter_month);

        //listView
        dayView = findViewById(R.id.daylistview);
        customAdapter_day = new DayAdapter(dayEntry, this, false, year, month);
        dayView.setAdapter(customAdapter_day);

        //Button
        edit_button = findViewById(R.id.edit_button);

        //Gesture Listener
        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        gestureListener.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);

        calculateRemain();
    }

    public void editButton(View view){
        if(edit_button.getText().toString() == "EDIT"){
            customAdapter_day.editmode = true;
            edit_button.setText("DONE");
        }
        else{
            customAdapter_day.editmode = false;
            edit_button.setText("EDIT");
            customAdapter_day.update_require = true;
            customAdapter_day.notifyDataSetChanged();
            dayEntry = db.getEntry(year,month);
            calculateRemain();
        }
    }

    public void calculateRemain(){
        remain = income;
        for (Map.Entry mapElement : monthEntry.entrySet()) {
            Entry temp = (Entry) mapElement.getValue();
            remain -= temp.money;
        }
        monthremain_text.setText(remain.toString());
        for (Map.Entry mapElement : dayEntry.entrySet()) {
            Entry temp = (Entry) mapElement.getValue();
            remain -= temp.money;
        }
        dayremain_text.setText(remain.toString());
    }

    public void update(){
        //Update day
        SimpleDateFormat date_format = new SimpleDateFormat("MMM yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("M-yyyy");
        date = Calendar.getInstance().getTime();
        try {
            date = sdf.parse(month+"-"+year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String showdate = date_format.format(date);
        date_text.setText(showdate);
        dayEntry = db.getEntry(year,month);
        monthEntry = db.getMonthEntry(year,month);
        customAdapter_day.list = new ArrayList(dayEntry.entrySet());
        customAdapter_month.list = new ArrayList(monthEntry.entrySet());
        customAdapter_day.notifyDataSetChanged();
        customAdapter_month.notifyDataSetChanged();
        calculateRemain();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass activity on touch event to the gesture detector.
        gestureDetectorCompat.onTouchEvent(event);
        // Return true to tell android OS that event has been consumed, do not pass it to other event listeners.
        return true;
    }
}
