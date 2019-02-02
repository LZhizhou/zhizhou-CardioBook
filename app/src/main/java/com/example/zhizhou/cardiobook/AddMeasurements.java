/**
 * AddMeasurements
 *
 * 2018-2-2
 *
 * this activity is used for user to input infomations
 */
package com.example.zhizhou.cardiobook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddMeasurements extends AppCompatActivity {
    /**
     *
     */
    private int month, day, year,hour, minute;
    EditText dateString, timeString;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurements);
        dateString = (EditText)findViewById(R.id.measureDate);
        //set listener to get date, when user touch the date textbox
        dateString.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                    // when user touch the date box
                    change_date(dateString);
                    return true;
                }
                else {
                    return false;
                }
            }
        });


        timeString = (EditText)findViewById(R.id.measureTime);
        timeString.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                    change_time(timeString);
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        Button submitButton = findViewById(R.id.submit_data);
        //return all the user inputs
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get user inputs from the corrposonding edittext
                String date = ((EditText) findViewById(R.id.measureDate)).getText().toString();
                String time = ((EditText) findViewById(R.id.measureTime)).getText().toString();
                String systolic = ((EditText) findViewById(R.id.systolic)).getText().toString();
                String diastolic = ((EditText)findViewById(R.id.diastolic)).getText().toString();
                String heartRate = ((EditText)findViewById(R.id.heartRate)).getText().toString();
                String comment = ((EditText)findViewById(R.id.comment)).getText().toString();

                if ("".equals(date) || "".equals(time)|| "".equals(systolic)|| "".equals(diastolic)|| "".equals(heartRate)){
                    // make sure user fill out all the info.
                    Toast.makeText(AddMeasurements.this, "fill all informations",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();

                    bundle.putCharSequence("date", date);
                    bundle.putCharSequence("time", time);
                    bundle.putCharSequence("systolic", systolic);
                    bundle.putCharSequence("diastolic", diastolic);
                    bundle.putCharSequence("heartRate", heartRate);
                    if (!"".equals(comment)){
                        bundle.putCharSequence("comment",comment);
                        // if this is comment
                    }
                    // pack all the user inputs
                    intent.putExtras(bundle);
                    setResult(0x07, intent);
                    // return
                    finish();
                }

            }
        });


    }


    /**
     * use android date picker to let user to choose date and write it to the date textbox
     * @param editText: click this, and store result into it
     */
    private void change_date(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        // android date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddMeasurements.this, new DatePickerDialog.OnDateSetListener() {
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

    /**
     * similiar to change_date()
     * use android time picker to let user to choose date and write it to the time textbox
     * @param editText: click this, and store result into it
     */
    private void change_time(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        // android time picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeasurements.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String hourString, minString;
                //call when time has been chose
                //format time into hh:mm , all 2 digital
                if (i<10){
                    hourString = 0+String.valueOf(i);
                } else{
                    hourString = String.valueOf(i);
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
