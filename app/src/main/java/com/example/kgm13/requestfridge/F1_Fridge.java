package com.example.kgm13.requestfridge;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kgm13.requestfridge.F1_Dialog.createImage;
import static com.example.kgm13.requestfridge.MLRoundedImageView.border;
import static com.example.kgm13.requestfridge.MLRoundedImageView.getCroppedBitmap;


public class F1_Fridge extends Fragment {
    public static View f1_view;         //frdige에 대한 view를 공유 (dialog와 같이)

    ////////////////////////////DB 변수////////////////////////////
    SQLiteDatabase db;
    boolean FridgeDB_check;              // 끄고 다시 들어올때 db가 있는지 없는지 체크

    int image;
    String name;
    int year, month, day, dayleft;       // D-day의 year,month, day + dayleft : day기준 얼마나 남았는지 계산

    int tYear, tMonth, tDay;             // 오늘의 year, month, day

    private long d, t, r;

    ////////////////////////////gridview 변수////////////////////////////
    static GridView gridView;           //gridview 선언
    static ArrayList<Item> gridArray = new ArrayList<Item>();       //gridview item을 선언
    static F1_GridViewAdapter customGridAdapter;              //girdview의 adapter를 통한 gridview로 엮어주는 역할


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        f1_view = inflater.inflate(R.layout.activity_f1_fridge, container, false);
        ButterKnife.bind(getActivity());

        F1_DBManager manager = new F1_DBManager(getActivity(), "Fridge.db", null, 1);
        SharedPreferences term = getActivity().getSharedPreferences("term", MODE_PRIVATE);

        gridView = (GridView) f1_view.findViewById(R.id.grid);
        customGridAdapter = new F1_GridViewAdapter(getActivity(), R.layout.activity_f1_fridge_gridview, gridArray);

        FridgeDB_check = term.getBoolean("F1_db", false);
        if(FridgeDB_check) {
            try {
                // 데이터베이스 객체를 얻어오는 다른 간단한 방법
                db = manager.getReadableDatabase();
                Cursor c = db.query("FRIDGE", null, null, null, null, null, null, null);
                int item_num = 0;
                gridArray.clear();
                while (c.moveToNext()) { //db의 id를 하나씩 이동하면서 list를 추가합니다
                    int del = c.getInt(c.getColumnIndex("del"));
                    if (del == 0) {
                        image = c.getInt(c.getColumnIndex("image"));
                        name = c.getString(c.getColumnIndex("name"));
                        year = c.getInt(c.getColumnIndex("year"));
                        month = c.getInt(c.getColumnIndex("month"));
                        day = c.getInt(c.getColumnIndex("day"));

                        Calendar calendar =Calendar.getInstance();              //현재 날짜 불러옴
                        Calendar dCalendar = Calendar.getInstance();
                        dCalendar.set(year,month, day);

                        tYear = calendar.get(Calendar.YEAR);
                        tMonth = calendar.get(Calendar.MONTH);
                        tDay = calendar.get(Calendar.DAY_OF_MONTH);

                        t=calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
                        d=dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
                        r=(d-t)/(24*60*60*1000);
                        dayleft = (int)r;

                        Bitmap imagebitmap;
                        if(image != 0){
                            imagebitmap = BitmapFactory.decodeResource(getContext().getResources(), image);
                        }
                        else{
                            imagebitmap = createImage(300, 300, Color.parseColor("#E1FF36"));
                        }
                        imagebitmap = getCroppedBitmap(imagebitmap, imagebitmap.getHeight() / 2);
                        imagebitmap = border(imagebitmap, dayleft);
                        gridArray.add(new Item(imagebitmap, name ,dayleft));
                        customGridAdapter.notifyDataSetChanged();
                    }
                }

                gridView.setAdapter(customGridAdapter);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        else {
            ///////////////////////////////////////////gridview////////////////////////////////////////
            gridView.setAdapter(customGridAdapter);
        }
        return f1_view;
    }


    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            onResume();
        }
    }

    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
    }
}
