package com.example.kgm13.requestfridge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kgm13 on 2017-04-20.
 * DB를 관리하는 클래스, table의 내용 및 insert, update, delete를 다룸
 *     table line    |    id     |  location |   image   |imagebitmap|   name    |    year   |   month   |    day    |    del    |
 *     data  type    |  INTEGER  |   TEXT    |  INTEGER  |  INTEGER  |   TEXT    |  INTEGER  |  INTEGER  |  INTEGER  |  INTEGER  |
 *     information   |    id     |wheretokeep|  svgimage |  bitmap   |nameofthing|year expire|monthexpire|day  expire|delete(0,1)|
 *     imagebitimap은 image가 0일때 작동하도록 설계, image가 0이 아닐시, 그대로 image가 들어가게됨.
 */

public class F1_DBManager extends SQLiteOpenHelper {
    public F1_DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션); TEXT, INTEGER,
                db.execSQL("CREATE TABLE FRIDGE( _id INTEGER PRIMARY KEY AUTOINCREMENT, location TEXT, image INTEGER, imagebitmap INTEGER, name TEXT," +
                        "    year INTEGER, month INTEGER, day INTEGER, del INTEGER);");
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

