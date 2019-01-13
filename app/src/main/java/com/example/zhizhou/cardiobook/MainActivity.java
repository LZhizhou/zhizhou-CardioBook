package com.example.zhizhou.cardiobook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LauncherActivity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    private ListView measureList;
    private MySQLite mMySQLite;
    private SQLiteDatabase mDbWriter;
    private SQLiteDatabase mDbReader;
    private String TAG = "TAG";
    private int oldColor;

    private EditText systolicEdit;
    private EditText diastolicEdit;
    private EditText heartRateEdit;
    private EditText measureDateEdit;
    private EditText measureTimeEdit;
    private EditText commentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initEvent();
        Button button = findViewById(R.id.Add);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, AddMeasurements.class);
                startActivityForResult(intent, 0x07);
            }
        });
        measureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                intent = new Intent(MainActivity.this, AddMeasurements.class);
                View view1 = View.inflate(MainActivity.this, R.layout.edit_measurements, null);
                systolicEdit= (EditText)view1.findViewById(R.id.systolicEdit);

                diastolicEdit= (EditText) view1.findViewById(R.id.diastolicEdit);
                heartRateEdit= (EditText) view1.findViewById(R.id.heartRateEdit);
                measureDateEdit=(EditText)  view1.findViewById(R.id.measureDateEdit);
                measureTimeEdit= (EditText) view1.findViewById(R.id.measureTimeEdit);
                commentEdit= (EditText) view1.findViewById(R.id.commentEdit);

                systolicEdit.setText(((TextView) view.findViewById(R.id.systolicSQL)).getText());

                diastolicEdit.setText(((TextView) view.findViewById(R.id.diastolicSQL)).getText());

                heartRateEdit.setText(((TextView) view.findViewById(R.id.heartRateSQL)).getText());

                measureDateEdit.setText(((TextView) view.findViewById(R.id.dateSQL)).getText());

                measureTimeEdit.setText(((TextView) view.findViewById(R.id.timeSQL)).getText());

                commentEdit.setText(((TextView) view.findViewById(R.id.commentSQL)).getText());
                measureDateEdit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                            change_date();
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                });
                measureTimeEdit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                            change_time();
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                });

                new AlertDialog.Builder(MainActivity.this).setTitle("Warning")
                        .setMessage("Do you want to change anything?").setView(view1)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateData(position);
                            }
                        }).setNegativeButton("Cancel", null).show();

            }
        });

        measureList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Warning")
                        .setMessage("Do you want to delete it?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDate(position);
                            }
                        })
                        .setNegativeButton("Cancel",null).show();
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x07 && resultCode == 0x07) {

            Bundle bundle = data.getExtras();
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            String systolic = bundle.getString("systolic");
            String diastolic = bundle.getString("diastolic");
            String heartRate = bundle.getString("heartRate");
            String comment = bundle.getString("comment");
            Toast.makeText(MainActivity.this, "saved",Toast.LENGTH_LONG).show();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("date", date);
            mContentValues.put("time", time);
            mContentValues.put("systolic", systolic);
            mContentValues.put("diastolic", diastolic);
            mContentValues.put("heartRate", heartRate);
            mContentValues.put("comment", comment);
            mDbWriter.insert("record", null, mContentValues);
            refreshListView();

        }
    }

    private void initEvent() {
        measureList = (ListView)findViewById(R.id.measureList);


        mMySQLite = new MySQLite(this);
        mDbReader = mMySQLite.getReadableDatabase();
        mDbWriter = mMySQLite.getWritableDatabase();

        mSimpleCursorAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.data_list, null,
                new String[]{"systolic", "diastolic", "heartRate", "date", "time", "comment"}, new int[]{R.id.systolicSQL, R.id.diastolicSQL, R.id.heartRateSQL, R.id.dateSQL,R.id.timeSQL, R.id.commentSQL}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                String systolicStr = (((TextView) view.findViewById(R.id.systolicSQL)).getText().toString());
                int systolicNo = Integer.valueOf(systolicStr);
                oldColor = ((TextView) view.findViewById(R.id.timeSQL)).getCurrentTextColor();
                if (systolicNo>140 || systolicNo<90){
                    ((TextView) view.findViewById(R.id.systolicSQL)).setTextColor(Color.RED);

                }
                else{
                    ((TextView) view.findViewById(R.id.systolicSQL)).setTextColor(oldColor);
                }
                String diastolicStr = (((TextView) view.findViewById(R.id.diastolicSQL)).getText().toString());
                int diastolicNo = Integer.valueOf(diastolicStr);
                if (diastolicNo>90 || diastolicNo<60){
                    ((TextView) view.findViewById(R.id.diastolicSQL)).setTextColor(Color.RED);

                }
                else{
                    ((TextView) view.findViewById(R.id.diastolicSQL)).setTextColor(oldColor);
                }
                String commentStr = (((TextView) view.findViewById(R.id.commentSQL)).getText().toString());
                if ("".equals(commentStr)){
                    ((TextView) view.findViewById(R.id.commentSQL)).setVisibility(View.GONE);
                }
                else{
                    ((TextView) view.findViewById(R.id.commentSQL)).setVisibility(View.VISIBLE);
                }
                return view;
            }
        };

        measureList.setAdapter(mSimpleCursorAdapter);
        refreshListView();

    }

    public void refreshListView() {
        Cursor mCursor = mDbWriter.query("record", null, null, null, null, null, null);
        mSimpleCursorAdapter.changeCursor(mCursor);




    }
    public void deleteDate(int pos){
        Cursor cursor = mSimpleCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        mDbWriter.delete("record", "_id=?", new String[]{itemId + ""});
        refreshListView();

    }
    public void updateData(int pos){
        Cursor cursor = mSimpleCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", measureDateEdit.getText().toString());
        contentValues.put("time", measureTimeEdit.getText().toString());
        contentValues.put("systolic", systolicEdit.getText().toString());
        contentValues.put("diastolic", diastolicEdit.getText().toString());
        contentValues.put("heartRate", heartRateEdit.getText().toString());
        contentValues.put("comment", commentEdit.getText().toString());
        mDbWriter.update("record", contentValues, "_id=?", new String[]{itemId + ""});
        refreshListView();
    }
    private void change_date(){
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String dayString, monthString;
                if (i2 < 10){
                    dayString = "0"+String.valueOf(i2);
                }else{
                    dayString = String.valueOf(i2);
                }
                if ((i1+1)<10){
                    monthString = "0"+String.valueOf(i1+1);
                }else{
                    monthString = "0"+String.valueOf(i1+1);
                }
                measureDateEdit.setText(String.valueOf(i) + '-' + monthString+'-'+dayString);

            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void change_time(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String hourString, minString;

                if (i<10){
                    hourString = "0"+String.valueOf(i);
                } else{
                    hourString = String.valueOf(i1);
                }

                if (i1<10){
                    minString = 0+String.valueOf(i1);
                } else{
                    minString = String.valueOf(i1);
                }

                measureTimeEdit.setText(hourString + ':' + minString);
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

}



