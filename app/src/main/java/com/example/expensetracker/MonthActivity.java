package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MonthActivity extends AppCompatActivity {

    Integer month, year;
    HashMap<Integer, Entry> entry;
    MonthAdapter customAdapter;
    ListView listView;
    DatabaseHelper db;
    Button edit_button;
    ImageButton add_button;
    EditText tag_edit,money_edit, income_edit;
    Integer income;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_day:
                    Intent ActivityChange_today = new Intent(MonthActivity.this, MainActivity.class);
                    startActivity(ActivityChange_today);
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    return true;
                case R.id.navigation_month:
                    return true;
                case R.id.navigation_history:
                    Intent ActivityChange_tohistory = new Intent(MonthActivity.this, HistoryActivity.class);
                    startActivity(ActivityChange_tohistory);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_month);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        db = new DatabaseHelper(this);
        String showdate = new SimpleDateFormat("MMM yyyy").format(Calendar.getInstance().getTime());
        month = Integer.parseInt(new SimpleDateFormat("M").format(Calendar.getInstance().getTime()));
        year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        TextView date_text = findViewById(R.id.date_textview);
        date_text.setText(showdate);

        //acquire data
        entry = db.getMonthEntry_checkNew(year,month);
        income = db.getIncome(year,month);

        //Edit Text
        money_edit = findViewById(R.id.money);
        tag_edit = findViewById(R.id.tag);
        income_edit = findViewById(R.id.income);
        income_edit.setText(income.toString());

        //Button
        add_button = findViewById(R.id.add_button);
        edit_button = findViewById(R.id.edit_button);

        //listView
        listView = findViewById(R.id.listview);
        customAdapter = new MonthAdapter(entry, this, false, year, month);
        listView.setAdapter(customAdapter);
    }


    public void editButton(View view){
        if(edit_button.getText().toString() == "EDIT"){
            customAdapter.editmode = true;
            edit_button.setText("DONE");
            income_edit.setFocusable(true);
            income_edit.setFocusableInTouchMode(true);
            income_edit.setClickable(true);
        }
        else{
            customAdapter.editmode = false;
            edit_button.setText("EDIT");
            customAdapter.update_require = true;
            income_edit.setFocusable(false);
            income_edit.setFocusableInTouchMode(false);
            income_edit.setClickable(false);
            db.updateIncome(year,month,Integer.parseInt(income_edit.getText().toString()));
        }
        customAdapter.notifyDataSetChanged();
    }
    public void update(){
        entry = db.getMonthEntry(year,month);
        customAdapter.list = new ArrayList(entry.entrySet());
        customAdapter.notifyDataSetChanged();
    }

    public void addEntry(View view){
        String tag = tag_edit.getText().toString();
        Integer money = Integer.parseInt(money_edit.getText().toString());
        if(money_edit.getText().toString().isEmpty())
        {
            Toast.makeText(MonthActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
        }
        else{
            Entry cur_entry = new Entry(0,month,year,tag,money,"",1);
            boolean inserted = db.addEntry(cur_entry);
            this.update();
            if(inserted){
                Toast.makeText(MonthActivity.this, "Entry added", Toast.LENGTH_SHORT).show();
                tag_edit.setText("");
                money_edit.setText("");
            }
        }
    }

}
