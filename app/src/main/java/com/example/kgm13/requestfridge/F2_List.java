package com.example.kgm13.requestfridge;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.kgm13.requestfridge.F2_Dialog.db2_check;
public class F2_List extends Fragment {
    static View f2_view;

    /*****************************listview variable*******************************/
    static ArrayList<Shopping> arrayList_freeze = new ArrayList<Shopping>(); // list들의 집합
    static F2_ListViewAdapter adapter_freeze;                                // list들의 집합들의 adapter
    static ArrayList<Shopping> arrayList_cold = new ArrayList<Shopping>();
    static F2_ListViewAdapter adapter_cold;
    static ArrayList<Shopping> arrayList_out = new ArrayList<Shopping>();
    static F2_ListViewAdapter adapter_out;

    static ListView listView_freeze;
    static ListView listView_cold;
    static ListView listView_out;

    /////////////////DB 변수///////////////////////
    int del, favorite;
    String location, name;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        f2_view = inflater.inflate(R.layout.activity_f2_list, container, false);

        listView_freeze = (ListView) f2_view.findViewById(R.id.f2_list_listview_freeze);
        listView_cold = (ListView) f2_view.findViewById(R.id.f2_list_listview_cold);
        listView_out = (ListView) f2_view.findViewById(R.id.f2_list_listview_out);

        adapter_freeze = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_freeze);
        adapter_cold = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_cold);
        adapter_out = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_out);


        LoadDBdata();

        return f2_view;
    }

    void LoadDBdata(){
        if (db2_check) {
            try {
                // 데이터베이스 객체를 얻어오는 다른 간단한 방법
                //location을 저장을 하고 들고오지 않음 아직!!! 이거 수정해야함!!!!
                F2_DBManager dbHelper = new F2_DBManager(getActivity(), "List.db", null, 1);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor c = db.query("LIST", null, null, null, null, null, null, null);
                arrayList_cold.clear();
                arrayList_freeze.clear();
                arrayList_out.clear();
                while (c.moveToNext()) { //db의 id를 하나씩 이동하면서 list를 추가합니다
                    del = c.getInt(c.getColumnIndex("del"));
                    favorite = c.getInt(c.getColumnIndex("favorite"));
                    name = c.getString(c.getColumnIndex("name"));
                    location = c.getString(c.getColumnIndex("location"));

                    if (del == 0) {
                        if (location.equals("freeze")) {
                            arrayList_freeze.add(new Shopping(name));
                            adapter_freeze.notifyDataSetChanged();
                        }
                        else if (location.equals("cold")) {
                            arrayList_cold.add(new Shopping(name));
                            adapter_cold.notifyDataSetInvalidated();
                        }
                        else if (location.equals("out")) {
                            arrayList_out.add(new Shopping(name));
                            adapter_out.notifyDataSetChanged();
                        }

                    }

                }
                listView_freeze.setAdapter(adapter_freeze);
                listView_cold.setAdapter(adapter_cold);
                listView_out.setAdapter(adapter_out);
                db.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

}