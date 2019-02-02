/**
 *MySQLite
 *
 * 2018-2-2
 *
 * create table at first run
 */
package com.example.zhizhou.cardiobook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


class MySQLite extends SQLiteOpenHelper {
    private Context mContext;

    /**
     * set context and open database
     */
    MySQLite(Context context) {
        super(context, "record.db", null, 1);

        this.mContext = context;
    }

    /**
     * create table at first run, and create a table called record, has column date, time
     * systolic, diastolic, heartRate, comment
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table record(_id integer primary key autoincrement," +
                "date varchar, time varchar, systolic integer, diastolic integer," +
                "heartRate integer, comment varchar)");
    }

    /**
     * not used in this porject
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(mContext, "onUpgrade", Toast.LENGTH_SHORT).show();
    }
}
