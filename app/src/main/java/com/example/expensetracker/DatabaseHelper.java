package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Exercise.db";

    //Main DB
    public static final String Main_db = "main_database";
    public static final String main_id = "id";
    public static final String main_c1 = "date";
    public static final String main_c2 = "month";
    public static final String main_c3 = "year";
    public static final String main_c4 = "tag";
    public static final String main_c5 = "money";
    public static final String main_c6 = "description";
    public static final String main_c7 = "repeat";

    //Main DB
    public static final String Income_db = "income_database";
    public static final String income_c1 = "date";
    public static final String income_c2 = "money";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_main = "CREATE TABLE " + Main_db + "("
                + main_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + main_c1 + " INTEGER, "
                + main_c2 + " INTEGER, "
                + main_c3 + " INTEGER, "
                + main_c4 + " TEXT, "
                + main_c5 + " INTEGER, "
                + main_c6 + " TEXT,"
                + main_c7 + " INTEGER"
                + ")";
        String create_income = "CREATE TABLE " + Income_db + "("
                + income_c1 + " STRING PRIMARY KEY, "
                + income_c2 + " INTEGER"
                + ")";
        try {
            db.execSQL(create_main);
        } catch (Exception e) {
            Log.d("ErrorMainDB",e.toString());
        }
        try {
            db.execSQL(create_income);
        } catch (Exception e) {
            Log.d("ErrorIncomeDB",e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_main = "DROP TABLE IF EXISTS "+Main_db;
        String drop_income = "DROP TABLE IF EXISTS "+Income_db;
        try {
            Log.d("MainDB","OnUpgrade");
            db.execSQL(drop_main);
            onCreate(db);
        }catch (Exception e) {
            Log.d("ErrorUpdatingMainDB",e.toString());
        }
        try {
            Log.d("IncomeDB","OnUpgrade");
            db.execSQL(drop_income);
            onCreate(db);
        }catch (Exception e) {
            Log.d("ErrorUpdatingIncomeDB",e.toString());
        }
        onCreate(db);
    }

    //Add Entry (from Day Page)
    public boolean addEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(main_c1, entry.date);
        contentValues.put(main_c2, entry.month);
        contentValues.put(main_c3, entry.year);
        contentValues.put(main_c4, entry.tag);
        contentValues.put(main_c5, entry.money);
        contentValues.put(main_c6, entry.description);
        contentValues.put(main_c7, entry.repeat);
        long result = db.insert(Main_db, null, contentValues);
        Log.d("addEntry",Long.toString(result));
        if(result <= 0)
            return false;
        else
            return true;
    }

    //Remove Entry (from History Page)
    public boolean deleteEntry(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = main_id+" =?";
        String[] whereArgs = new String[] { id.toString()};
        long result =  db.delete(Main_db, whereClause, whereArgs);
        Log.d("deleteEntry",Long.toString(result));
        if(result <= 0)
            return false;
        else
            return true;
    }

    //Get Entry except date = 0 (to Show in History Page)
    public HashMap<Integer, Entry> getEntry(Integer year, Integer month){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {main_id,main_c1,main_c2,main_c3,main_c4,main_c5,main_c6,main_c7};
        String selection = main_c3+" =?"+ " AND " +main_c2+" =?"+ " AND " + main_c1 + "!=?";
        String[] selectionArgs = {year.toString() ,month.toString(), "0"};
        Cursor cursor =db.query(Main_db,columns,selection,selectionArgs,null,null,main_c1);
        HashMap<Integer,Entry> temp = new HashMap<Integer,Entry>();
        while (cursor.moveToNext())
        {
            Entry cur_Entry = new Entry();
            Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(main_id)));
            cur_Entry.date=cursor.getInt(cursor.getColumnIndex(main_c1));
            cur_Entry.month=cursor.getInt(cursor.getColumnIndex(main_c2));
            cur_Entry.year=cursor.getInt(cursor.getColumnIndex(main_c3));
            cur_Entry.tag=cursor.getString(cursor.getColumnIndex(main_c4));
            cur_Entry.money=cursor.getInt(cursor.getColumnIndex(main_c5));
            cur_Entry.description=cursor.getString(cursor.getColumnIndex(main_c6));
            cur_Entry.repeat=cursor.getInt(cursor.getColumnIndex(main_c7));
            temp.put(id,cur_Entry);
        }
        cursor.close();
        return temp;
    }

    //Get Monthly Entry date = 0
    public HashMap<Integer, Entry> getMonthEntry_checkNew(Integer year, Integer month){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean newmonth;
        String[] columns = {main_id,main_c1,main_c2,main_c3,main_c4,main_c5,main_c6,main_c7};
        String selection = main_c3+" =?"+ " AND " +main_c2+" =?"+ " AND " + main_c1 + "=?";
        String[] selectionArgs = {year.toString(), month.toString(), "0"};
        Cursor cursor =db.query(Main_db,columns,selection,selectionArgs,null,null,null);

        if(cursor.moveToFirst())
        {
            newmonth = false;
        }
        else{
            newmonth = true;
        }

        if(newmonth){
            Integer prev_year, prev_month;
            if(month == 1){
                prev_year = year-1;
                prev_month = 12;
            }
            else{
                prev_year = year;
                prev_month = month-1;
            }
            selection = main_c3+" =?"+ " AND " +main_c2+" =?"+ " AND " + main_c1 + "=?"+ " AND " + main_c7 + "=?";
            selectionArgs = new String[]{prev_year.toString(), prev_month.toString(), "0","1"};
            cursor =db.query(Main_db,columns,selection,selectionArgs,null,null,null);
            while (cursor.moveToNext())
            {
                Entry cur_Entry = new Entry();
                Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(main_id)));
                cur_Entry.date=cursor.getInt(cursor.getColumnIndex(main_c1));
                cur_Entry.month=month;
                cur_Entry.year=year;
                cur_Entry.tag=cursor.getString(cursor.getColumnIndex(main_c4));
                cur_Entry.money=cursor.getInt(cursor.getColumnIndex(main_c5));
                cur_Entry.description=cursor.getString(cursor.getColumnIndex(main_c6));
                cur_Entry.repeat=cursor.getInt(cursor.getColumnIndex(main_c7));
                this.addEntry(cur_Entry);
            }
            //get data from last month
        }

        selection = main_c3+" =?"+ " AND " +main_c2+" =?"+ " AND " + main_c1 + "=?";
        selectionArgs = new String[]{year.toString(), month.toString(), "0"};
        HashMap<Integer,Entry> temp = new HashMap<Integer,Entry>();
        cursor =db.query(Main_db,columns,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext())
        {
            Entry cur_Entry = new Entry();
            Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(main_id)));
            cur_Entry.date=cursor.getInt(cursor.getColumnIndex(main_c1));
            cur_Entry.month=cursor.getInt(cursor.getColumnIndex(main_c2));
            cur_Entry.year=cursor.getInt(cursor.getColumnIndex(main_c3));
            cur_Entry.tag=cursor.getString(cursor.getColumnIndex(main_c4));
            cur_Entry.money=cursor.getInt(cursor.getColumnIndex(main_c5));
            cur_Entry.description=cursor.getString(cursor.getColumnIndex(main_c6));
            cur_Entry.repeat=cursor.getInt(cursor.getColumnIndex(main_c7));
            temp.put(id,cur_Entry);
        }
        cursor.close();
        return temp;
    }

    public HashMap<Integer, Entry> getMonthEntry(Integer year, Integer month){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {main_id,main_c1,main_c2,main_c3,main_c4,main_c5,main_c6,main_c7};
        String selection = main_c3+" =?"+ " AND " +main_c2+" =?"+ " AND " + main_c1 + "=?";
        String[] selectionArgs = new String[]{year.toString(), month.toString(), "0"};
        HashMap<Integer,Entry> temp = new HashMap<Integer,Entry>();
        Cursor cursor =db.query(Main_db,columns,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext())
        {
            Entry cur_Entry = new Entry();
            Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(main_id)));
            cur_Entry.date=cursor.getInt(cursor.getColumnIndex(main_c1));
            cur_Entry.month=cursor.getInt(cursor.getColumnIndex(main_c2));
            cur_Entry.year=cursor.getInt(cursor.getColumnIndex(main_c3));
            cur_Entry.tag=cursor.getString(cursor.getColumnIndex(main_c4));
            cur_Entry.money=cursor.getInt(cursor.getColumnIndex(main_c5));
            cur_Entry.description=cursor.getString(cursor.getColumnIndex(main_c6));
            cur_Entry.repeat=cursor.getInt(cursor.getColumnIndex(main_c7));
            temp.put(id,cur_Entry);
        }
        cursor.close();
        return temp;
    }

    //Update Entry
    public boolean updateEntry(Integer id, Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(main_c1, entry.date);
        contentValues.put(main_c2, entry.month);
        contentValues.put(main_c3, entry.year);
        contentValues.put(main_c4, entry.tag);
        contentValues.put(main_c5, entry.money);
        contentValues.put(main_c6, entry.description);
        contentValues.put(main_c7, entry.repeat);
        String selection = main_id+" =?";
        String[] selectionArgs = {id.toString()};
        long result = db.update(Main_db, contentValues, selection, selectionArgs);
        Log.d("updateEntry",Long.toString(result));
        if(result <= 0)
            return false;
        else
            return true;
    }

    public boolean addIncome(Integer year, Integer month, Integer money){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(income_c1, year.toString()+month.toString());
        contentValues.put(income_c2, money);
        long result = db.insert(Income_db, null, contentValues);
        Log.d("addIncome",Long.toString(result));
        if(result <= 0)
            return false;
        else
            return true;
    }

    public boolean updateIncome(Integer year, Integer month, Integer money){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(income_c1, year.toString()+month.toString());
        contentValues.put(income_c2, money);
        String selection = income_c1+" =?";
        String[] selectionArgs = {year.toString()+month.toString()};
        long result = db.update(Income_db, contentValues, selection, selectionArgs);
        Log.d("updateIncome",Long.toString(result));
        if(result <= 0)
            return false;
        else
            return true;
    }

    public Integer getIncome(Integer year, Integer month){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {income_c1,income_c2};
        String selection = income_c1+" =?";
        String[] selectionArgs = {year.toString()+month.toString()};
        Cursor cursor =db.query(Income_db,columns,selection,selectionArgs,null,null,null);
        Integer temp = 0;
        if (cursor.getCount() > 0){
            while (cursor.moveToNext())
            {
                temp=cursor.getInt(cursor.getColumnIndex(income_c2));
            }
        }
        else{
            this.addIncome(year, month, 0);
        }
        cursor.close();
        return temp;
    }
}
