package com.example.kgm13.requestfridge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kgm13 on 2017-05-14.
 */

public class info_ingredient_DBManager extends SQLiteOpenHelper {
    public info_ingredient_DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션); TEXT, INTEGER,
        db.execSQL("CREATE TABLE info_ingredient (recipe_code INTEGER, list_korean TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }
}
