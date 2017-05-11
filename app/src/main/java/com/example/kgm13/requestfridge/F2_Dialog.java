package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
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

import static com.example.kgm13.requestfridge.F2_List.adapter_cold;
import static com.example.kgm13.requestfridge.F2_List.adapter_freeze;
import static com.example.kgm13.requestfridge.F2_List.adapter_out;
import static com.example.kgm13.requestfridge.F2_List.arrayList_cold;
import static com.example.kgm13.requestfridge.F2_List.arrayList_freeze;
import static com.example.kgm13.requestfridge.F2_List.arrayList_out;
import static com.example.kgm13.requestfridge.F2_List.listView_cold;
import static com.example.kgm13.requestfridge.F2_List.listView_freeze;
import static com.example.kgm13.requestfridge.F2_List.listView_out;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;

/**
 * Created by kgm13 on 2017-04-09.
 */

public class F2_Dialog  extends Dialog {

    ////////////////////////////DB 변수////////////////////////////
    F2_DBManager dbManager;
    static boolean db2_check = false;

    ////////////////////////////dialog 변수////////////////////////////
    //library를 이용해서 xml의 id를 불러옴
    //gradle에 compile 'com.jakewharton:butterknife:7.0.1'추가
    //onCreate부의 setContentView밑에 ButteerKnife.bing(this); 추가
    private String location, listname;    // 물품위치, 물품 이름
    Boolean freeze = false, cold = false, out = false;

    @Nullable @Bind(R.id.f2_cancel) Button f2_cancel;
    @Nullable @Bind(R.id.f2_freeze) Button f2_freeze;
    @Nullable @Bind(R.id.f2_cold) Button f2_cold;
    @Nullable @Bind(R.id.f2_out) Button f2_out;
    @Nullable @Bind(R.id.f2_listname) EditText f2_listname;
    @Nullable @Bind(R.id.f2_leftbtn) Button f2_leftbtn;
    @Nullable @Bind(R.id.f2_rightbtn) Button f2_rightbtn;


    public F2_Dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f2_dialog);
        ButterKnife.bind(this);

        dbManager = new F2_DBManager(getContext().getApplicationContext(), "List.db", null, 1);

        f2_freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!freeze){
                    freeze = true;
                    f2_freeze.setBackgroundColor(getContext().getResources().getColor(R.color.tabcolor));
                    f2_freeze.setTextColor(getContext().getResources().getColor(R.color.white));
                }
                else{
                    freeze = false;
                    f2_freeze.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    f2_freeze.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                }
            }
        });
        f2_cold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cold){
                    cold = true;
                    f2_cold.setBackgroundColor(getContext().getResources().getColor(R.color.tabcolor));
                    f2_cold.setTextColor(getContext().getResources().getColor(R.color.white));
                }
                else{
                    cold = false;
                    f2_cold.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    f2_cold.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                }
            }
        });
        f2_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!out){
                    out = true;
                    f2_out.setBackgroundColor(getContext().getResources().getColor(R.color.tabcolor));
                    f2_out.setTextColor(getContext().getResources().getColor(R.color.white));
                }
                else{
                    out = false;
                    f2_out.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                    f2_out.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                }
            }
        });



        ////////////////////////////////구현해야 할 사항////////////////////////////////////
        //'계속 추가하기'에 대한 event
        f2_leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();

                if (buttoncheck()) {
                    if (freeze) {
                        arrayList_freeze.add(new Shopping(listname));
                        adapter_freeze.notifyDataSetChanged();
                        listView_freeze.setAdapter(adapter_freeze);
                        dbManager.insert("insert into LIST values(null, " + "'freeze', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    if (cold) {
                        arrayList_cold.add(new Shopping(listname));
                        adapter_cold.notifyDataSetChanged();
                        listView_cold.setAdapter(adapter_cold);
                        dbManager.insert("insert into LIST values(null, " + "'cold', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    if (out) {
                        arrayList_out.add(new Shopping(listname));
                        adapter_out.notifyDataSetChanged();
                        listView_out.setAdapter(adapter_out);
                        dbManager.insert("insert into LIST values(null, " + "'out', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    setData();
                    db2_check = true;
                    Snackbar.make(v, listname + "이 추가되었습니다!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    f2_listname.setText("");

                } else {
                    Snackbar.make(v, "버튼 설정을 다시 확인해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });


        //'등록'에 대한 event
        f2_rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();
                if (buttoncheck()) {
                    if (freeze) {
                        arrayList_freeze.add(new Shopping(listname));
                        adapter_freeze.notifyDataSetChanged();
                        listView_freeze.setAdapter(adapter_freeze);
                        dbManager.insert("insert into LIST values(null, " + "'freeze', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    if (cold) {
                        arrayList_cold.add(new Shopping(listname));
                        adapter_cold.notifyDataSetChanged();
                        listView_cold.setAdapter(adapter_cold);
                        dbManager.insert("insert into LIST values(null, " + "'cold', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    if (out) {
                        arrayList_out.add(new Shopping(listname));
                        adapter_out.notifyDataSetChanged();
                        listView_out.setAdapter(adapter_out);
                        dbManager.insert("insert into LIST values(null, " + "'out', " + 0 + ", '" + listname + "', " + 0 + ");");
                    }
                    setData();
                    db2_check = true;
                    dismiss();

                } else {
                    Snackbar.make(v, "버튼 설정을 다시 확인해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        f2_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }
    //////////////////dialog-editext에 대해 각각string 변환//////////////////

    public String getlistname() {
        return listname;
    }

    public String getlocation() {
        return location;
    }



    public void setlistname(String listname) {
        this.listname = listname;
        f2_listname.setText(listname);
        f2_listname.setFocusable(true);
    }

    public void setlocation(String location) {
        this.location = location;
        if(freeze){
            f2_freeze.setText(location);
            f2_freeze.setFocusable(true);
        }
        else if(cold){
            f2_cold.setText(location);
            f2_cold.setFocusable(true);
        }
        else{
            f2_out.setText(location);
            f2_out.setFocusable(true);
        }
    }

    //////////////////dialog-editext에 대해 string 변환//////////////////
    public void EditToString(){
        listname = f2_listname.getText().toString();
        if(freeze) location = "freeze";
        if(cold)   location = "cold";
        if(out)    location = "out";
    }

    //버튼 1개 이상 혹은 체크를 안했을때를 체킹 하는 함수
    boolean buttoncheck() {
        int count = 0;
        if (freeze)             count++;
        if (cold)               count++;
        if (out)                count++;
        if(count == 1){
            return true;
        }
        else{
            return false;
        }
    }
    ////////////////////////// android  ->  MYsql  연동////////////////////////////////////////
    void setData() {

        class set_list extends AsyncTask<Void, Integer, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                String param = "&u_id=" + login_id + "&u_location=" + location + "&u_favorite=" + "0" + "&u_name=" + listname + "&u_del=" + "0";
                try {
                    URL url = new URL("http://13.124.64.178/set_list.php");

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

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        set_list g = new set_list();
        g.execute();
    }
}
