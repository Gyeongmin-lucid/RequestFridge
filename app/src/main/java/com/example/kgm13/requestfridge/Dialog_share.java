package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

import static com.example.kgm13.requestfridge.LoginActivity.login_id;

public class Dialog_share extends Dialog {

    @Nullable @Bind(R.id.share_cancel) Button share_cancel;
    @Nullable @Bind(R.id.share_cancel_top) Button share_cancel_top;
    @Nullable @Bind(R.id.share_confirm) Button share_confirm;
    @Nullable @Bind(R.id.share_id) EditText share_id;

    String share_ID;
    View share_cancel_button;

    public Dialog_share(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_share);
        ButterKnife.bind(this);

        share_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        share_cancel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        share_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_cancel_button = view;
                share_ID = share_id.getText().toString();
                if(login_id.equals(share_ID)){
                    Snackbar.make(share_cancel_button, "공유하고 싶은 아이디를 입력해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    Share_confirmID();
                }

            }
        });

    }

    void Share_confirmID() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String param = "&u_shareid=" + share_ID + "&u_id="+ login_id;
                try {
                    URL url = new URL("http://13.124.64.178/share_id.php");

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

                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if(result.equals("error")){
                    Snackbar.make(share_cancel_button, "찾으시는 아이디가 없습니다.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder
                            .setMessage(share_ID + "님에게 공유요청을 하였습니다. \n"+ share_ID +"님이 수락하시면 회원님의 데이터와 공유가 됩니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

}
