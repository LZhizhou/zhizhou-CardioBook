/**
 *MainActivity
 *
 * 2018-2-2
 */
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
    private String TAG = "TAG";
    private int oldColor;

    private EditText systolicEdit;
    private EditText diastolicEdit;
    private EditText heartRateEdit;
    private EditText measureDateEdit;
    private EditText measureTimeEdit;
    private EditText commentEdit;

    /**
     * run when creating
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set content
        setContentView(R.layout.activity_main);
        // prepatration, init attrabutions
        initEvent();
        Button button = findViewById(R.id.Add);
        // set "add" button to start a new activity to let user to input all info
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, AddMeasurements.class);
                startActivityForResult(intent, 0x07);
                // start the new activity
            }
        });
        // when user click a group of data, pop up details and edit
        measureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                intent = new Intent(MainActivity.this, AddMeasurements.class);
                // view1 is an AlertDialog, that display all the details and sak user if to change
                View view1 = View.inflate(MainActivity.this, R.layout.edit_measurements, null);
                // get the old info
                systolicEdit= (EditText)view1.findViewById(R.id.systolicEdit);
                diastolicEdit= (EditText) view1.findViewById(R.id.diastolicEdit);
                heartRateEdit= (EditText) view1.findViewById(R.id.heartRateEdit);
                measureDateEdit=(EditText)  view1.findViewById(R.id.measureDateEdit);
                measureTimeEdit= (EditText) view1.findViewById(R.id.measureTimeEdit);
                commentEdit= (EditText) view1.findViewById(R.id.commentEdit);

                //show the old info into the pop up
                systolicEdit.setText(((TextView) view.findViewById(R.id.systolicSQL)).getText());
                diastolicEdit.setText(((TextView) view.findViewById(R.id.diastolicSQL)).getText());
                heartRateEdit.setText(((TextView) view.findViewById(R.id.heartRateSQL)).getText());
                measureDateEdit.setText(((TextView) view.findViewById(R.id.dateSQL)).getText());
                measureTimeEdit.setText(((TextView) view.findViewById(R.id.timeSQL)).getText());
                commentEdit.setText(((TextView) view.findViewById(R.id.commentSQL)).getText());

                // let user to choose the new date, when they touch date box
                measureDateEdit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                            change_date(measureDateEdit);
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                });
                // let user to choose the new time, when they touch time box
                measureTimeEdit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                            change_time(measureTimeEdit);
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                });
                // create AlertDialog based on view1
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Do you want to change anything?")
                        .setView(view1)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateData(position);
                                // if uesr choose save
                            }
                        }).setNegativeButton("Cancel", null).show();

            }
        });
        // longtouch is aske user if delete this message
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
        // when return from the AddMeasures activity
        if (requestCode == 0x07 && resultCode == 0x07) {

            Bundle bundle = data.getExtras();
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            String systolic = bundle.getString("systolic");
            String diastolic = bundle.getString("diastolic");
            String heartRate = bundle.getString("heartRate");
            String comment = bundle.getString("comment");
            // unpack bundle
            Toast.makeText(MainActivity.this, "saved",Toast.LENGTH_LONG).show();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put("date", date);
            mContentValues.put("time", time);
            mContentValues.put("systolic", systolic);
            mContentValues.put("diastolic", diastolic);
            mContentValues.put("heartRate", heartRate);
            mContentValues.put("comment", comment);
            // write user input into sql file
            mDbWriter.insert("record", null, mContentValues);
            refreshListView();

        }
    }

    private void initEvent() {
        measureList = (ListView)findViewById(R.id.measureList);


        mMySQLite = new MySQLite(this);
        mDbWriter = mMySQLite.getWritableDatabase();

        //get date for the sql file
        mSimpleCursorAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.data_list, null,
                new String[]{"systolic", "diastolic", "heartRate", "date", "time", "comment"}, new int[]{R.id.systolicSQL, R.id.diastolicSQL, R.id.heartRateSQL, R.id.dateSQL,R.id.timeSQL, R.id.commentSQL}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                String systolicStr = (((TextView) view.findViewById(R.id.systolicSQL)).getText().toString());
                //get value of systolic
                int systolicNo = Integer.valueOf(systolicStr);
                // get the dafault text color
                oldColor = ((TextView) view.findViewById(R.id.timeSQL)).getCurrentTextColor();
                // if systolic is out of the range, set the color of text into red
                if (systolicNo>140 || systolicNo<90){
                    ((TextView) view.findViewById(R.id.systolicSQL)).setTextColor(Color.RED);
                }
                else{
                    // systolic is in of the range, set the color back to the default color
                    ((TextView) view.findViewById(R.id.systolicSQL)).setTextColor(oldColor);
                }
                String diastolicStr = (((TextView) view.findViewById(R.id.diastolicSQL)).getText().toString());
                //get value of diastolic
                int diastolicNo = Integer.valueOf(diastolicStr);
                // if diastolic is out of the range, set the color of text into red
                if (diastolicNo>90 || diastolicNo<60){
                    ((TextView) view.findViewById(R.id.diastolicSQL)).setTextColor(Color.RED);
                }
                else{
                    // diastolic is in of the range, set the color back to the default color
                    ((TextView) view.findViewById(R.id.diastolicSQL)).setTextColor(oldColor);
                }
                String commentStr = (((TextView) view.findViewById(R.id.commentSQL)).getText().toString());
                //get comment
                if ("".equals(commentStr)){
                    // if this is no comment, remove the textbox
                    ((TextView) view.findViewById(R.id.commentSQL)).setVisibility(View.GONE);
                }
                else{
                    // if this is no comment, show it
                    ((TextView) view.findViewById(R.id.commentSQL)).setVisibility(View.VISIBLE);
                }
                return view;
            }
        };

        measureList.setAdapter(mSimpleCursorAdapter);
        refreshListView();

    }

    /**
     * refreshListView bases on sql file
     */
    public void refreshListView() {
        Cursor mCursor = mDbWriter.query("record", null, null, null, null, null, null);
        mSimpleCursorAdapter.changeCursor(mCursor);

    }
    /**
     * updateData
     * @param: the index of the message
     */
    public void deleteDate(int pos){
        Cursor cursor = mSimpleCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        // goto pos
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        // delete that sql
        mDbWriter.delete("record", "_id=?", new String[]{itemId + ""});
        refreshListView();

    }

    /**
     * updateData
     * @param: the index of the message
     */
    public void updateData(int pos){
        Cursor cursor = mSimpleCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        // goto pos in sql file
        int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
        ContentValues contentValues = new ContentValues();
        // get the new info
        contentValues.put("date", measureDateEdit.getText().toString());
        contentValues.put("time", measureTimeEdit.getText().toString());
        contentValues.put("systolic", systolicEdit.getText().toString());
        contentValues.put("diastolic", diastolicEdit.getText().toString());
        contentValues.put("heartRate", heartRateEdit.getText().toString());
        contentValues.put("comment", commentEdit.getText().toString());
        // write them into sql file
        mDbWriter.update("record", contentValues, "_id=?", new String[]{itemId + ""});
        refreshListView();
    }
    private void change_date(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        // android date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //called when date has been chose
                //change all date and month into 2-digital number
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
                editText.setText(String.valueOf(i) + '-' + monthString+'-'+dayString);
                // set the date chosen by user to the text box

            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void change_time(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        // android time picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //call when time has been chose
                //format time into hh:mm , all 2 digital
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

                editText.setText(hourString + ':' + minString);
                //set the time chosen to time textbox
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

}



