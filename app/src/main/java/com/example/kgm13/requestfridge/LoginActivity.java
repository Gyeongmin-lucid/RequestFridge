package com.example.kgm13.requestfridge;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by KYS on 2017-04-06.
 */

public class LoginActivity extends AppCompatActivity {
    //button_autosignin
    @Nullable @Bind(R.id.button_nonmem) Button btnnonmem;
    @Nullable @Bind(R.id.button_signup) Button btnsignup;
    @Nullable @Bind(R.id.button_autosignin) CheckBox btnauto;
    @Nullable @Bind(R.id.button_signin) Button butsignin;
    @Nullable @Bind(R.id.login_id)      EditText editlogid;
    @Nullable @Bind(R.id.login_pw)      EditText editlogpw;

    View view;

    public static String login_id, login_token;
    String login_pw;
    SessionCallback callback;
    public static boolean login_check = false, login_auto = false;
    boolean idcheck, signup;

    BackPressCloseHandler backPressCloseHandler;    //cancel를 두번 눌렸을때 취소가 되게 하기 위한 변수

    String kakaoID, kakao_pw = "ijfe12235jjFJIFJE235";

    //실시간 동기화 변수(Firebase)


    //xls -> db과정에서 쓰이는 변수(추천디비에서 쓰임!)
    info_cuisine_DBManager cuisine_dbManager;
    info_ingredient_DBManager ingredient_dbManager;
    info_stage_DBManager stage_dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
        SharedPreferences.Editor editor = term.edit();
        login_check = term.getBoolean("login_check", false);
        login_auto = term.getBoolean("login_auto", false);
        if (login_check) {
            login_id = term.getString("ID", "");
            if(login_auto){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }


        cuisine_dbManager = new info_cuisine_DBManager(getApplicationContext(), "databases/info_cuisine.db", null, 1);
        ingredient_dbManager = new info_ingredient_DBManager(getApplicationContext(), "databases/info_ingredient.db", null, 1);
        stage_dbManager = new info_stage_DBManager(getApplicationContext(), "databases/info_stage.db", null, 1);



        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        btnauto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnauto.isChecked())
                    login_auto = true;
                else
                    login_auto = false;
            }
        });

        btnnonmem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_auto = true;
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
                login_check = true;
                login_id = editlogid.getText().toString();
                login_pw = editlogpw.getText().toString();
                login_token = FirebaseInstanceId.getInstance().getToken();
                getsignin();

            }
        });

        editlogid.setBackgroundColor(Color.argb(80, 0, 0, 0));
        editlogpw.setBackgroundColor(Color.argb(80, 0, 0, 0));
        butsignin.setBackgroundColor(Color.argb(95, 0, 0, 0));


        FirebaseMessaging.getInstance().subscribeToTopic("sharelist");
        FirebaseInstanceId.getInstance().getToken();

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
                    login_check = true;
                    login_auto = true;
                    kakaoID = String.valueOf(userProfile.getId());
                    login_token = FirebaseInstanceId.getInstance().getToken();
                    login_regist();
                    while(!signup) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(idcheck) {
                        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
                        SharedPreferences.Editor editor = term.edit();
                        editor.putString("ID", kakaoID); //First라는 key값으로 infoFirst 데이터를 저장한다.
                        editor.putBoolean("login_check", true);
                        editor.putString("Token", login_token);
                        editor.commit();
                        login_check = true;
                    }

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
                String param = "&u_id=" + login_id + "&u_pw=" + login_pw + "&u_token=" + login_token;
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
            editor.putString("Token",login_token);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast toast = Toast.makeText(this, "아이디나 비밀번호를 확인해 주세요!.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    void login_regist() {
        class Login_registDB extends AsyncTask<Void, Integer, Void> {
            String sId;
            String sPw;
            String token;
            FirebaseInstanceIDService firebaseInstanceIDService;
            Login_registDB(String par_sId, String par_sPw, String par_token){
                sId = par_sId;
                sPw = par_sPw;
                token = par_token;
            }

            @Override
            protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
                String param = "&u_id=" + sId + "&u_pw=" + sPw + "&u_token=" + token;
                try {
/* 서버연결 */
                    URL url = new URL(
                            "http://13.124.64.178/sign_up.php");
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

                    //is = conn.getErrorStream();
                    is = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                    String line = null;
                    StringBuffer buff = new StringBuffer();
                    while ( ( line = in.readLine() ) != null )
                    {
                        buff.append(line + "\n");
                    }
                    data = buff.toString().trim();
                    Log.e("RECV DATA",data);
                    if(!data.equals("duplicatate")) {
                        idcheck = false;
                    }
                    idcheck = true;
                    signup = true;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
        Login_registDB rdb = new Login_registDB(kakaoID, kakao_pw, login_token);
        rdb.execute();
    }
}
