package com.example.kgm13.requestfridge;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by kgm13 on 2017-05-18.
 */

public class DatabaseRead extends SQLiteAssetHelper {

    private String DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    public DatabaseRead(Context context, String par_DATABASE_NAME) {
        super(context, par_DATABASE_NAME, null, DATABASE_VERSION);
        par_DATABASE_NAME = DATABASE_NAME;
    }
}
