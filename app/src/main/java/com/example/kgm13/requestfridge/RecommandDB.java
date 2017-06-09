package com.example.kgm13.requestfridge;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by kgm13 on 2017-05-18.
 */

public class RecommandDB {
    //내부 db생성 변수들
    public static Cursor cursor_i,cursor_c, cursor_s;
    public static DatabaseRead info_i, info_c, info_s;
    public static SQLiteDatabase DB_i, DB_c, DB_s;
    //추천 db부분!
    public static void setDBoption(){
        info_c = new DatabaseRead(GlobalApplication.getAppContext(), "info_cuisine.db");
        info_i = new DatabaseRead(GlobalApplication.getAppContext(), "info_ingredient.db");
        info_s = new DatabaseRead(GlobalApplication.getAppContext(), "info_stage.db");

        DB_c = info_c.getReadableDatabase();
        DB_i = info_i.getReadableDatabase();
        DB_s = info_s.getReadableDatabase();

    }
    public static void closeDBoption(){
        DB_c.close();
        DB_i.close();
        DB_s.close();
    }

    //사용법 : 레시피 번호와, 해당 출력하고싶은 col 번호를 넣으면, 그에 맞는 string 출력
    //출력하고자 하는 내용이 요리명 : 1, 설명 : 2, image주소 : 3으로 col_num에 입력
    public static String get_cuisine(int recipeCode_num, int col_num) {
        setDBoption();
        String result = "";
        cursor_c = DB_c.rawQuery("select * from info_cuisine where recipe_code = " + recipeCode_num + "", null);
        if (cursor_c.getCount() != 0 && cursor_c != null) {
            if (cursor_c.moveToFirst()) {
                result = cursor_c.getString(col_num);
                Log.v("cuisine success", result);
            }
        } else {
            Log.w("cuisine error", "code_num with matching col_string not found.");
        }
        cursor_c.close();
        closeDBoption();
        return result;
    }

    //사용법 : list에 해당하는 이름을 parameter로 넣으면, 레시피 번호가 array형태로 리턴. 번호를 차례대로 쓰면됨!!, 초기화 0으로 했으니 마지막은 0으로 체킹
    public static int[] get_ingredient(String listname){
        setDBoption();
        int[] result = new int[100];
        Arrays.fill(result, 0);
        int i = 0;
        cursor_i = DB_i.rawQuery("select * from info_ingredient where list_korean = '"+ listname +"'",null);
        if (cursor_i.getCount() != 0 && cursor_i!=null) {
            if (cursor_i.moveToFirst()) {
                do {
                    result[i++] = cursor_i.getInt(0);
                    Log.v("ingredient success", String.valueOf(result[i-1]));
                } while (cursor_i.moveToNext() && i <100);
            }
        }
        else{
            Log.w("ingredient error", "listname with matching numbers not found.");
        }
        cursor_i.close();
        closeDBoption();
        return result;
    }
    // 사용법 : 레시피 번호를 넣으면 그에 맞는 요리 순서대로 string[]형태가 return
    public static String[] get_stage(int recipeCode_num){
        setDBoption();
        String[] result = new String[10];
        Arrays.fill(result, "");
        cursor_s = DB_s.rawQuery("select * from info_stage where recipe_code = " + recipeCode_num + "", null);
        if (cursor_s.getCount() != 0 && cursor_s != null) {
            if (cursor_s.moveToFirst()) {
                do{
                    result[cursor_s.getInt(1)-1] =  String.valueOf(cursor_s.getInt(1));
                    result[cursor_s.getInt(1)-1] +=  ". ";
                    result[cursor_s.getInt(1)-1] += cursor_s.getString(2);
                    Log.v("stage success", result[cursor_s.getInt(1)-1]);
                } while (cursor_s.moveToNext());
            }
        } else {
            Log.w("stage error", "code_num with matching recipe not found.");
        }
        cursor_s.close();
        closeDBoption();
        return result;
    }
}
