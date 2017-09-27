package com.wmstein.tourcount.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/*
 * Created by wmstein on 15.05.2016.
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

    public void saveTemp(Temp temp)
    {
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(DbHelper.T_ID, temp.id);
        dataToInsert.put(DbHelper.T_TEMP_LOC, temp.temp_loc);
        dataToInsert.put(DbHelper.T_TEMP_CNT, temp.temp_cnt);
        database.update(DbHelper.TEMP_TABLE, dataToInsert, null, null);
    }

    private Temp cursorToTemp(Cursor cursor)
    {
        Temp temp = new Temp();
        temp.id = cursor.getInt(cursor.getColumnIndex(DbHelper.T_ID));
        temp.temp_loc = cursor.getString(cursor.getColumnIndex(DbHelper.T_TEMP_LOC));
        temp.temp_cnt = cursor.getInt(cursor.getColumnIndex(DbHelper.T_TEMP_CNT));
        return temp;
    }

    public Temp getTemp()
    {
        Temp temp;
        Cursor cursor = database.query(DbHelper.TEMP_TABLE, allColumns, String.valueOf(1), null, null, null, null);
        cursor.moveToFirst();
        temp = cursorToTemp(cursor);
        // Make sure to close the cursor
        cursor.close();
        return temp;
    }

}
