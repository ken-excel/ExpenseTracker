package com.example.expensetracker;

public class Entry {
    public Integer date, month, year, money, repeat;
    public String tag, description;

    public Entry(){

    }

    public Entry(Integer date, Integer month, Integer year, String tag, Integer money, String description, Integer repeat){
        this.date = date;
        this.month = month;
        this.year = year;
        this.tag = tag;
        this.money = money;
        this.description = description;
        this.repeat =repeat;
    }
}
