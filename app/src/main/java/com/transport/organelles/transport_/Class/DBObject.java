package com.transport.organelles.transport_.Class;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Organelles on 6/14/2017.
 */

public class DBObject {
    private static DBAssets dbHelper;
    private SQLiteDatabase db;

    public DBObject(Context context) {
        dbHelper = new DBAssets(context);
        this.db = dbHelper.getReadableDatabase();
    }

    public SQLiteDatabase getDbConnection(){
        return this.db;
    }

    public void closeDbConnection(){
        if(this.db != null){
            this.db.close();
        }
    }

}
