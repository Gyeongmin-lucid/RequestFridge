package com.example.kgm13.requestfridge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Login_Sign_up extends AppCompatActivity {
    @Nullable @Bind(R.id.signup_id) EditText signup_id;
    @Nullable @Bind(R.id.signup_pw) EditText signup_pw;
    @Nullable @Bind(R.id.signup_pwcheck) EditText signup_pwcheck;
    @Nullable @Bind(R.id.bt_Join) Button signup_join;

    String sId, sPw, sPw_chk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        ButterKnife.bind(this);

        signup_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditToStirng();
                if(sPw.equals(sPw_chk)) {
                    Login_registDB rdb = new Login_registDB(sId, sPw);
                    rdb.execute();
                }
                else {
                    Snackbar.make(view, "비밀번호를 확인해주세요", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

    }

    public void EditToStirng()
    {
        /* 버튼을 눌렀을 때 동작하는 소스 */
        sId = signup_id.getText().toString();
        sPw = signup_pw.getText().toString();
        sPw_chk = signup_pwcheck.getText().toString();

    }


}
