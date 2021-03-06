package com.wmstein.tourcount.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/***********************************
 * Created by wmstein on 2016-05-15,
 * last edited on 2019-01-27
 */
public class TempDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private final DbHelper dbHandler;
    private final String[] allColumns = {
        DbHelper.T_ID,
        DbHelper.T_TEMP_LOC,
        DbHelper.T_TEMP_CNT,
    };

    public TempDataSource(Context context)
    {
        dbHandler = new DbHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHandler.getWritableDatabase();
    }

    public void close()
    {
        dbHandler.close();
    }

    public void saveTempLoc(Temp temp)
    {
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(DbHelper.T_ID, temp.id);
        dataToInsert.put(DbHelper.T_TEMP_LOC, temp.temp_loc);
        database.update(DbHelper.TEMP_TABLE, dataToInsert, null, null);
    }

    /* 
    //for future use
    public void saveTempCnt(int tmpcnt)
    {
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(DbHelper.T_ID, 1);
        dataToInsert.put(DbHelper.T_TEMP_CNT, tmpcnt);
        database.update(DbHelper.TEMP_TABLE, dataToInsert, null, null);
    }

    // for future use
    public int getTempCnt()
    {
        Temp temp;
        Cursor cursor = database.query(DbHelper.TEMP_TABLE, allColumns, String.valueOf(1), null, null, null, null);
        cursor.moveToFirst();
        temp = cursorToTemp(cursor);
        cursor.close();
        return temp.temp_cnt;
    }
    */

    public Temp getTemp()
    {
        Temp temp;
        Cursor cursor = database.query(DbHelper.TEMP_TABLE, allColumns, String.valueOf(1), null, null, null, null);
        cursor.moveToFirst();
        temp = cursorToTemp(cursor);
        cursor.close();
        return temp;
    }

    private Temp cursorToTemp(Cursor cursor)
    {
        Temp temp = new Temp();
        temp.id = cursor.getInt(cursor.getColumnIndex(DbHelper.T_ID));
        temp.temp_loc = cursor.getString(cursor.getColumnIndex(DbHelper.T_TEMP_LOC));
        temp.temp_cnt = cursor.getInt(cursor.getColumnIndex(DbHelper.T_TEMP_CNT));
        return temp;
    }

}
