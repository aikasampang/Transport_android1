package com.transport.organelles.transport_.Class;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Organelles on 6/8/2017.
 */

public class DBAssets extends SQLiteAssetHelper {

    private static String DBName = GlobalVariable.DB_Name;
    private static int DBVersion = GlobalVariable.DB_Version;

    public DBAssets(Context context) {
        super (context, DBName, null, DBVersion);

    }

}