package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String tag, description;
    Integer date, month,year, money;
    DatabaseHelper db;
    Button add_button;
    EditText money_edit, description_edit;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnTagSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tag_food:
                    tag = getString(R.string.tag_food);
                    return true;
                case R.id.tag_shop:
                    tag = getString(R.string.tag_shop);
                    return true;
                case R.id.tag_tran:
                    tag = getString(R.string.tag_tran);
                    return true;
                case R.id.tag_pay:
                    tag = getString(R.string.tag_pay);
                    return true;
                case R.id.tag_other:
                    tag = getString(R.string.tag_other);
                    return true;
            }
            return false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_day:
                    return true;
                case R.id.navigation_month:
                    Intent ActivityChange_tomonth = new Intent(MainActivity.this, MonthActivity.class);
                    startActivity(ActivityChange_tomonth);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    return true;
                case R.id.navigation_history:
                    Intent ActivityChange_tohistory = new Intent(MainActivity.this, HistoryActivity.class);
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
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        //Bottom Menu
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_day);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Tag Menu
        BottomNavigationView tagView = findViewById(R.id.tag_view);
        tagView.setOnNavigationItemSelectedListener(mOnTagSelectedListener);
        tagView.getMenu().getItem(0).setChecked(true);
        tag = "Food";

        //Date Panel
        TextView date_text = findViewById(R.id.date_textview);
        String showdate = new SimpleDateFormat("MMM dd yyyy").format(Calendar.getInstance().getTime());
        String showdayofweek = new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime());
        date = Integer.parseInt(new SimpleDateFormat("d").format(Calendar.getInstance().getTime()));
        month = Integer.parseInt(new SimpleDateFormat("M").format(Calendar.getInstance().getTime()));
        year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        date_text.setText(showdayofweek+" "+showdate);

        //Edit Text
        money_edit = findViewById(R.id.money_edittext);
        description_edit = findViewById(R.id.description_edittext);

        //Add Button
        add_button = findViewById(R.id.add_button);
    }

    public void addEntry(View view){
        description = description_edit.getText().toString();
        money = Integer.parseInt(money_edit.getText().toString());
        if(money_edit.getText().toString().isEmpty())
        {
            Toast.makeText(MainActivity.this, "Please enter amount of money", Toast.LENGTH_SHORT).show();
        }
        else{
            Entry entry = new Entry(date,month,year,tag,money,description,0);
            boolean inserted = db.addEntry(entry);
            if(inserted){
                Toast.makeText(MainActivity.this, "Entry added", Toast.LENGTH_SHORT).show();
                description_edit.setText("");
                money_edit.setText("");
            }
        }
    }
}
