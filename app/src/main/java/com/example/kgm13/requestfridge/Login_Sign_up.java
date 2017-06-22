package com.example.kgm13.requestfridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

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

import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.LoginActivity.login_token;

public class Login_Sign_up extends AppCompatActivity {
    @Nullable @Bind(R.id.signup_id) EditText signup_id;
    @Nullable @Bind(R.id.signup_pw) EditText signup_pw;
    @Nullable @Bind(R.id.signup_pwcheck) EditText signup_pwcheck;
    @Nullable @Bind(R.id.bt_Join) Button signup_join;

    String login_pw, login_pw_chk;
    boolean signup = false, idcheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        ButterKnife.bind(this);


        signup_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditToStirng();
                if (login_pw.equals(login_pw_chk)) {
                    login_token = FirebaseInstanceId.getInstance().getToken();

                    login_regist();
                    while(!signup) {try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }}
                    if(idcheck) {
                        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
                        SharedPreferences.Editor editor = term.edit();
                        editor.putString("ID", login_id); //First라는 key값으로 infoFirst 데이터를 저장한다.
                        editor.putBoolean("login_check", true);
                        editor.putString("Token", login_token);
                        editor.commit();
                        login_check = true;

                        Intent intent = new Intent(Login_Sign_up.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplication(), "아이디를 확인해주세요", Toast.LENGTH_LONG).show();
                        //Snackbar.make(view, "아이디를 확인해주세요", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        signup = false;
                    }
                } else {
                    Toast.makeText(getApplication(), "비밀번호를 확인해주세요", Toast.LENGTH_LONG).show();
                    //Snackbar.make(view, "비밀번호를 확인해주세요", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

    }



    public void EditToStirng()
    {
        /* 버튼을 눌렀을 때 동작하는 소스 */
        login_id = signup_id.getText().toString();
        login_pw = signup_pw.getText().toString();
        login_pw_chk = signup_pwcheck.getText().toString();

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Login_Sign_up.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        idcheck = true;
                    }
                    signup = true;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
        Login_registDB rdb = new Login_registDB(login_id, login_pw, login_token);
        rdb.execute();
    }
}
