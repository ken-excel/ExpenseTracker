package com.example.expensetracker;

import android.content.Context;
import android.graphics.Color;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthAdapter extends BaseAdapter implements ListAdapter {
    public List<Map.Entry<Integer, Entry>> list;
    private Context context;
    public Boolean editmode;
    public Boolean update_require;
    private DatabaseHelper db;
    public Integer year, month;

    public MonthAdapter(HashMap<Integer,Entry> list, Context context, Boolean editmode, Integer year, Integer month) {
        this.list = new ArrayList(list.entrySet());
        this.context = context;
        this.editmode = editmode;
        this.db = new DatabaseHelper(context);
        this.year = year;
        this.month = month;
        this.update_require = false;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        //Handle element
        TextView tag_text= (TextView)view.findViewById(R.id.tag);
        EditText money_text= (EditText)view.findViewById(R.id.money);
        ImageButton repeat_button= (ImageButton)view.findViewById(R.id.repeat_button);
        ImageButton delete_button= (ImageButton)view.findViewById(R.id.delete_button);

        final Integer id = list.get(position).getKey();

        if(update_require){
            list.get(position).getValue().money = Integer.parseInt(money_text.getText().toString());
            db.updateEntry(id,list.get(position).getValue());
            update_require = false;
        }
        final Entry entry = list.get(position).getValue();
        tag_text.setText(entry.tag);
        money_text.setText(entry.money.toString());
        if(!editmode){
            disableEditText(money_text);
            delete_button.setVisibility(View.GONE);
        }
        else{
            enableEditText(money_text);
            delete_button.setVisibility(View.VISIBLE);
        }
        if(entry.repeat == 1){
            repeat_button.setBackgroundColor(Color.YELLOW);
        }
        else{
            repeat_button.setBackgroundColor(Color.WHITE);
        }

        repeat_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(entry.repeat == 1){
                    entry.repeat = 0;
                    boolean delete = db.updateEntry(id,entry);
                    list.get(position).getValue().repeat = 0;
                    notifyDataSetChanged();
                }
                else{
                    entry.repeat = 1;
                    boolean delete = db.updateEntry(id,entry);
                    list.get(position).getValue().repeat = 1;
                    notifyDataSetChanged();
                }
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean delete = db.deleteEntry(id);
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
    }
}