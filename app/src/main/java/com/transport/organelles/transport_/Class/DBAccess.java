package com.transport.organelles.transport_.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Organelles on 6/8/2017.
 */

public class DBAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DBAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DBAccess(Context context) {

        this.openHelper = new DBAssets(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */

    public static DBAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DBAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void Open() {
        this.database = openHelper.getWritableDatabase();
    }

    //Database connection with return
    public boolean OpenDB() {
        this.database = openHelper.getWritableDatabase();

        return true;
    }

    /**
     * Close the database connection.
     */
    public void Close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */

    public List<String> getQuotes() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM employee", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public Cursor readEntry() {
        Open();
        //String[] allColumns = new String[] { "empID", "fname",  "lname" };
        Cursor c = database.rawQuery("SELECT empID, fname, lname from employee ORDER BY empid Desc", null);

        if (c != null) {
            c.moveToFirst();
        }
        Close();
        return c;
    }

    public boolean InsertEmployee(String fname, String mname, String lname) {
        boolean isValid = false;
        Open();
        try {
            database.execSQL("INSERT INTO employee" +
                    "(fname, mname, lname)" +
                    "VALUES('" + fname + "', '" + mname + "', '" + lname + "')");
        } finally {
            isValid = true;
        }
        Close();

        return isValid;
    }

    //Return cursor from SQL query.
    public Cursor selectQuery(String strQuery) {
        Cursor cu = null;
        try {
            Open();
            cu = database.rawQuery(strQuery,null);
            if (cu.getCount() > 0) {
                cu.moveToFirst();
            }
            Close();

        }
        catch (SQLException ex) {
            GlobalVariable.sqlException = String.valueOf(ex);
        }
        return cu;
    }

    //Execute SQL query
    public boolean executeQuery(String strQuery) {
        Open();
        try {
            database.beginTransactionNonExclusive();
            database.execSQL(strQuery);
            database.setTransactionSuccessful();
            //Close();
            return true;
        }
        catch (SQLiteException ex) {
            GlobalVariable.sqlException = String.valueOf(ex);
            Log.d("DBACCESS", ex + "");
            return false;
        }
        finally {
            database.endTransaction();
        }

    }

    public boolean insertDB(String tableName, ContentValues cv) {
        Open();
        try {
            database.beginTransactionNonExclusive();
            database.insert(tableName, "ID", cv);
            database.setTransactionSuccessful();
            //Close();
            return true;
        }
        catch (SQLiteException ex) {
            GlobalVariable.sqlException = String.valueOf(ex);
            return false;
        }
        finally {
            database.endTransaction();
        }
    }

//    public void deleteDataTable(String table){
//        database.delete(table,null,null);
//    }


}
