package com.example.kgm13.requestfridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by KYS on 2017-04-06.
 */

public class LoginActivity extends AppCompatActivity {
    @Nullable @Bind(R.id.button_nonmem) Button btnnonmem;
    @Nullable @Bind(R.id.button_signup) Button btnsignup;
    @Nullable @Bind(R.id.button_signin) Button butsignin;
    @Nullable @Bind(R.id.login_id)      EditText editlogid;
    @Nullable @Bind(R.id.login_pw)      EditText editlogpw;

    public static String login_id, login_pw;
    SessionCallback callback;
    public static boolean login_check = false;

    BackPressCloseHandler backPressCloseHandler;    //cancel를 두번 눌렸을때 취소가 되게 하기 위한 변수

    //xls -> db과정에서 쓰이는 변수(추천디비에서 쓰임!)
    info_cuisine_DBManager cuisine_dbManager;
    info_ingredient_DBManager ingredient_dbManager;
    info_stage_DBManager stage_dbManager;
    Sheet sheet;
    Cell cell;
    int colnum;
    int it = 1;
    String[] rowdata;
    String db_string;
    dbstart makeDB;

    //내부 db생성 변수들
    //DataBaseHelper mDbHelper;
    //TestAdapter mDbHelper;
    Cursor cursor_i,cursor_c, cursor_s;
    DatabaseRead info_i, info_c, info_s;
    SQLiteDatabase DB_i, DB_c, DB_s;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        cuisine_dbManager = new info_cuisine_DBManager(getApplicationContext(), "databases/info_cuisine.db", null, 1);
        ingredient_dbManager = new info_ingredient_DBManager(getApplicationContext(), "databases/info_ingredient.db", null, 1);
        stage_dbManager = new info_stage_DBManager(getApplicationContext(), "databases/info_stage.db", null, 1);

        //사용예제 : return 값을 이용해서 사용하세욤!!
        get_ingredient("계란");
        get_cuisine(17,1);
        get_stage(17);


        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);


        btnnonmem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Login_Sign_up.class);
                startActivity(intent);
                finish();
            }
        });
        butsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_id = editlogid.getText().toString();
                login_pw = editlogpw.getText().toString();
                getsignin();

            }
        });

        editlogid.setBackgroundColor(Color.argb(80, 0, 0, 0));
        editlogpw.setBackgroundColor(Color.argb(80, 0, 0, 0));
        butsignin.setBackgroundColor(Color.argb(95, 0, 0, 0));

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    String kakaoID = String.valueOf(userProfile.getId());
                    Log.e("kakaoID", kakaoID);
                    Log.e("UserProfile", userProfile.toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }

    }

    public void getsignin() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id + "&u_pw=" + login_pw;
                try {
                    URL url = new URL("http://13.124.64.178/sign_in.php");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param.getBytes("UTF-8"));
                    outs.flush();
                    outs.close();

/* 서버 -> 안드로이드 파라메터값 전달 */
                    InputStream is = null;
                    BufferedReader in = null;
                    String data = "";

                    is = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                    String json;
                    StringBuilder sb = new StringBuilder();
                    while ((json = in.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                String myJSON = result;
                showlogResult(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void showlogResult(String myJSON) {
        if (!login_id.equals("") && myJSON.equals(login_id)) {
            SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
            SharedPreferences.Editor editor = term.edit();
            editor.putString("ID", login_id); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.putBoolean("login_check", true);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast toast = Toast.makeText(this, "아이디나 비밀번호를 확인해 주세요!.", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    void setDBoption(){
        info_c = new DatabaseRead(this, "info_cuisine.db");
        info_i = new DatabaseRead(this, "info_ingredient.db");
        info_s = new DatabaseRead(this, "info_stage.db");

        DB_c = info_c.getReadableDatabase();
        DB_i = info_i.getReadableDatabase();
        DB_s = info_s.getReadableDatabase();

    }
    void closeDBoption(){
        DB_c.close();
        DB_i.close();
        DB_s.close();
    }

    //사용법 : 레시피 번호와, 해당 출력하고싶은 col 번호를 넣으면, 그에 맞는 string 출력
    //출력하고자 하는 내용이 요리명 : 1, 설명 : 2, image주소 : 3으로 col_num에 입력
    public String get_cuisine(int recipeCode_num, int col_num) {
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
    public int[] get_ingredient(String listname){
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
                } while (cursor_i.moveToNext());
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
    public String[] get_stage(int recipeCode_num){
        setDBoption();
        String[] result = new String[10];
        Arrays.fill(result, "");
        cursor_s = DB_s.rawQuery("select * from info_stage where recipe_code = " + recipeCode_num + "", null);
        if (cursor_s.getCount() != 0 && cursor_s != null) {
            if (cursor_s.moveToFirst()) {
                do{
                    result[cursor_s.getInt(1)-1] = cursor_s.getString(2);
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

    ///////////////////////////////내부 디비 작업
    class dbstart extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            info_recipe_sqlite();
            return null;
        }
    }


    void info_recipe_sqlite() {

        try {
            AssetManager am = getAssets();
            InputStream is = am.open("info_recipe.xls");
            Workbook wb = Workbook.getWorkbook(is);
            for (int sheet_num = 0; sheet_num < 3; sheet_num++, it = 1) {
                sheet = wb.getSheet(sheet_num);
                initList(sheet_num);
            }

        } catch (Exception e) {
        }

    }

    void initList(int sheet_num) {

        switch(sheet_num){
            //insertDB(num) : num -> db의 열의 개수, ex) 4 : 'code, cuisine, introduce, image_URL'
            //                                          2 : 'code, list_korean'
            case 0:
                colnum = 4;
                insertDB(colnum, "info_cuisine");
                return;
            case 1:
                colnum = 2;
                insertDB(colnum, "info_ingredient");
                return;
            case 2:
                colnum  = 3;
                insertDB(colnum, "info_stage");
            default:

        }

    }

    void insertDB(int colnum, String info){
        do{
            rowdata = getrowdata(it++);
            if(!rowdata[0].equals(" ")){
                db_string = "INSERT INTO " + info + " VALUES ('";
                for(int i = 0; i < colnum-1; i++) {
                    db_string += rowdata[i] + "', '";
                }
                db_string += rowdata[colnum-1] + "')";

                if(colnum == 2) {
                    ingredient_dbManager.insert(db_string);
                }
                if(colnum == 3){
                    stage_dbManager.insert(db_string);
                }
                if(colnum == 4) {
                    cuisine_dbManager.insert(db_string);
                }
            }
            else{
                return;
            }
        }while(!rowdata[0].equals(" "));
    }
    String[] getrowdata(int row) {
        String[] data = new String[colnum];
        for (int i = 0; i < colnum; i++) {
            cell = sheet.getCell(i, row);
            data[i] = String.valueOf(cell.getContents());
        }
        return data;
    }


}
