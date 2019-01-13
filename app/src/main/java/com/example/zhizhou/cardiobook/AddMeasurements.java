package com.example.zhizhou.cardiobook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import javax.xml.validation.Validator;

import static android.content.ContentValues.TAG;

public class AddMeasurements extends AppCompatActivity {

    private int month, day, year,hour, minute;
    EditText dateString, timeString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurements);
        dateString = (EditText)findViewById(R.id.measureDate);
        dateString.setOnTouchListener(new View.OnTouchListener() {
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


        timeString = (EditText)findViewById(R.id.measureTime);
        timeString.setOnTouchListener(new View.OnTouchListener() {
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
        Button submitButton = findViewById(R.id.submit_data);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = ((EditText) findViewById(R.id.measureDate)).getText().toString();
                String time = ((EditText) findViewById(R.id.measureTime)).getText().toString();
                String systolic = ((EditText) findViewById(R.id.systolic)).getText().toString();
                String diastolic = ((EditText)findViewById(R.id.diastolic)).getText().toString();
                String heartRate = ((EditText)findViewById(R.id.heartRate)).getText().toString();
                String comment = ((EditText)findViewById(R.id.comment)).getText().toString();

                if ("".equals(date) || "".equals(time)|| "".equals(systolic)|| "".equals(diastolic)|| "".equals(heartRate)){
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
                    }
                    intent.putExtras(bundle);
                    setResult(0x07, intent);
                    finish();
                }

            }
        });


    }
    private void initDateTime(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONDAY) +1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }

    public void change_date(){
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddMeasurements.this, new DatePickerDialog.OnDateSetListener() {
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
                AddMeasurements.this.dateString.setText(String.valueOf(i) + '-' + monthString+'-'+dayString);

            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    public void change_time(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeasurements.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String hourString, minString;

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

                AddMeasurements.this.timeString.setText(hourString + ':' + minString);
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}
