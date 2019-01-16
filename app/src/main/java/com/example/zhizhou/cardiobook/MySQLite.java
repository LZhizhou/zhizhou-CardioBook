package com.example.zhizhou.cardiobook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


class MySQLite extends SQLiteOpenHelper {
    private Context mContext;

    MySQLite(Context context) {
        super(context, "record.db", null, 1);

        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table record(_id integer primary key autoincrement," +
                "date varchar, time varchar, systolic integer, diastolic integer," +
                "heartRate integer, comment varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(mContext, "onUpgrade", Toast.LENGTH_SHORT).show();
    }
}
