package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

/**
 * Created by kgm13 on 2017-04-09.
 */

public class F2_Dialog  extends Dialog {

    ////////////////////////////dialog 변수////////////////////////////
    private String location;    // 물품위치
    private String listname;    // 물품이름
    Boolean freeze = false;
    Boolean cold = false;
    Boolean out = false;

    @Nullable @Bind(R.id.f2_cancel)
    Button f2_cancel;

    @Nullable @Bind(R.id.f2_freeze)
    Button f2_freeze;

    @Nullable @Bind(R.id.f2_cold)
    Button f2_cold;

    @Nullable @Bind(R.id.f2_out)
    Button f2_out;

    @Nullable @Bind(R.id.f2_listname)
    EditText f2_listname;


    @Nullable @Bind(R.id.f2_leftbtn)
    Button f2_leftbtn;

    @Nullable @Bind(R.id.f2_rightbtn)
    Button f2_rightbtn;


    public F2_Dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f2_dialog);
        ButterKnife.bind(this);

        f2_freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!freeze){
                   freeze = true;
                    f2_freeze.setBackgroundColor(getContext().getResources().getColor(R.color.tabcolor));
                    f2_freeze.setTextColor(getContext().getResources().getColor(R.color.white));
                    if(cold) {
                        cold = false;
                        f2_cold.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_cold.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
                    if(out) {
                        out = false;
                        f2_out.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_out.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
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
                    if(freeze) {
                        freeze = false;
                        f2_freeze.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_freeze.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
                    if(out) {
                        out = false;
                        f2_out.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_out.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
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
                    if(freeze) {
                        freeze = false;
                        f2_freeze.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_freeze.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
                    if(cold) {
                        cold = false;
                        f2_cold.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        f2_cold.setTextColor(getContext().getResources().getColor(R.color.tabcolor));
                    }
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
                    }
                    if (cold) {
                        arrayList_cold.add(new Shopping(listname));
                        adapter_cold.notifyDataSetChanged();
                        listView_cold.setAdapter(adapter_cold);
                    }
                    if (out) {
                        arrayList_out.add(new Shopping(listname));
                        adapter_out.notifyDataSetChanged();
                        listView_out.setAdapter(adapter_out);
                    }
                    Snackbar.make(v, listname + "이 추가되었습니다!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

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
                    }
                    if (cold) {
                        arrayList_cold.add(new Shopping(listname));
                        adapter_cold.notifyDataSetChanged();
                        listView_cold.setAdapter(adapter_cold);
                    }
                    if (out) {
                        arrayList_out.add(new Shopping(listname));
                        adapter_out.notifyDataSetChanged();
                        listView_out.setAdapter(adapter_out);
                    }
                    Snackbar.make(v, listname + "이 추가되었습니다!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
}