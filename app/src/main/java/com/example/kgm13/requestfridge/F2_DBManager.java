package com.example.kgm13.requestfridge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kgm13 on 2017-04-20.
 * DB를 관리하는 클래스, table의 내용 및 insert, update, delete를 다룸
 *     table line    |    id     |  location |  favorite |   name    |    del    |
 *     data  type    |  INTEGER  |   TEXT    |  INTEGER  |   TEXT    |  INTEGER  |
 *     information   |    id     |wheretokeep|switch(0,1)|nameofthing|delete(0,1)|
 *    favorite는 0이면 off, 1이면 on상태, on상태일때는 바로 설정 spinner에서 listview를 끌어다 떙겨 쓸수 있음
 */

public class F2_DBManager extends SQLiteOpenHelper {
    public F2_DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션); TEXT, INTEGER,
        db.execSQL("CREATE TABLE LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, location TEXT, favorite INTEGER, name TEXT, del INTEGER);");
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

