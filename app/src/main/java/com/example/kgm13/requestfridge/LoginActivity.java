package com.example.kgm13.requestfridge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by KYS on 2017-04-06.
 */

public class LoginActivity extends AppCompatActivity {
    @Nullable @Bind(R.id.button_nonmem) Button btnnonmem;
    @Nullable @Bind(R.id.button_signup) Button btnsignup;
    @Nullable @Bind(R.id.button_signin) Button butsignin;
    @Nullable @Bind(R.id.login_id) EditText editlogid;
    @Nullable @Bind(R.id.login_pw) EditText editlogpw;

    SessionCallback callback;

    BackPressCloseHandler backPressCloseHandler;    //cancel를 두번 눌렸을때 취소가 되게 하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

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
                    } else {

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
}
