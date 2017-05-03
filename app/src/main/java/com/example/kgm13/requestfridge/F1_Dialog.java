package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.F1_Fridge.customGridAdapter;
import static com.example.kgm13.requestfridge.F1_Fridge.gridArray;
import static com.example.kgm13.requestfridge.F1_Fridge.gridView;
import static com.example.kgm13.requestfridge.MLRoundedImageView.border;
import static com.example.kgm13.requestfridge.MLRoundedImageView.getCroppedBitmap;

/**
 * Created by kgm13 on 2017-04-09.
 * 설명 : 냉장고 탭에서 + 버튼을 누를시 손으로 추가할 수 있는 창의 역할입니다
 */

//int imageresource = getResources().getIdentifier("@drawable/your_image", "drawable", getActivity().getPackageName());
//        image.setImageResource(imageresource);

public class F1_Dialog extends Dialog {
    ////////////////////////////DB 변수////////////////////////////
    F1_DBManager dbManager;
    static boolean db1_check = false;

    ////////////////////////////dialog 변수////////////////////////////
    private String listname;    // 물품병
    private String listcount;   // 개수
    private String memo;        // 메모

    Calendar c;
    private int year;        // 유통기한 년도
    private int month;       // 유통기한 월
    private int day;        // 유통기한 날짜
    private int dayleft = 0;

    int tYear;           //오늘 연월일 변수
    int tMonth;
    int tDay;

    //library를 이용해서 xml의 id를 불러옴
    //gradle에 compile 'com.jakewharton:butterknife:7.0.1'추가

    @Nullable @Bind(R.id.f1_cancel)
    Button f1_cancel;

    @Nullable @Bind(R.id.f1_listname)
    EditText f1_listname;

    @Nullable @Bind(R.id.f1_listcount)
    EditText f1_listcount;

    @Nullable @Bind(R.id.f1_datepicker)
    DatePicker f1_datepicker;

    @Nullable @Bind(R.id.f1_memo)
    EditText f1_memo;

    @Nullable @Bind(R.id.f1_leftbtn)
    Button f1_leftbtn;

    @Nullable @Bind(R.id.f1_rightbtn)
    Button f1_rightbtn;


    public F1_Dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f1_dialog);
        ButterKnife.bind(this);

        dbManager = new F1_DBManager(getContext().getApplicationContext(), "Fridge.db", null, 1);

        f1_datepicker.init(f1_datepicker.getYear(),
                f1_datepicker.getMonth(),
                f1_datepicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year_, int monthOfYear_, int dayOfMonth_) {
                        year = year_;
                        month = monthOfYear_;
                        day = dayOfMonth_;

                        Calendar calendar =Calendar.getInstance();              //현재 날짜 불러옴
                        Calendar dCalendar = Calendar.getInstance();
                        dCalendar.set(year,month, day);

                        tYear = calendar.get(Calendar.YEAR);
                        tMonth = calendar.get(Calendar.MONTH);
                        tDay = calendar.get(Calendar.DAY_OF_MONTH);

                        long t=calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
                        long d=dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
                        long r=(d-t)/(24*60*60*1000);
                        dayleft = (int)r;

                        if(dayleft < 0)
                            Snackbar.make(view, "유통기한을 확인해주세요!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                });


        ////////////////////////////////구현해야 할 사항////////////////////////////////////
        //'계속 추가하기'에 대한 event
            f1_leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();
                if(dayleft < 0 || (TextUtils.isEmpty(listname))) {
                    Snackbar.make(v, "설정을 다시해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    int temp = imagechoose(listname);

                    Bitmap item2 = BitmapFactory.decodeResource(getContext().getResources(), temp);
                    item2 = getCroppedBitmap(item2, item2.getHeight() / 2);
                    item2 = border(item2, dayleft);

                    Item item_temp = new Item(item2,listname, dayleft);
                    gridArray.add(item_temp);
                    customGridAdapter.notifyDataSetChanged();
                    gridView.setAdapter(customGridAdapter);
                    if(item_temp.getItemimage() == 0) {
                        System.out.println("====================2222===================" + item_temp.getItemimage());
                        dbManager.insert("insert into FRIDGE values(null, " + "'out', " + 0 + ", " + temp + ", '" + listname + "', " + year + ", " + month + ", " + day + ", " + 0 + ");");
                    }
                    else{
                        System.out.println("===================111====================" + item_temp.getItemimage());
                        dbManager.insert("insert into FRIDGE values(null, " + "'out', " + temp + ", " + 0 + ", '" + listname + "', " + year + ", " + month + ", " + day + ", " + 0 + ");");
                    }

                    db1_check = true;
                }
            }
        });

        //'등록'에 대한 event
        f1_rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();

                if(dayleft < 0 || (TextUtils.isEmpty(listname))) {
                    Snackbar.make(v, "설정을 다시해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    int temp = imagechoose(listname);

                    Bitmap item1 = BitmapFactory.decodeResource(getContext().getResources(), temp);
                    item1 = getCroppedBitmap(item1, item1.getHeight() / 2);
                    item1 = border(item1, dayleft);

                    Item item_temp = new Item(item1,listname, dayleft);
                    gridArray.add(item_temp);
                    customGridAdapter.notifyDataSetChanged();
                    gridView.setAdapter(customGridAdapter);
                    if(item_temp.getItemimage() == 0) {
                        System.out.println("====================2222===================" + item_temp.getItemimage());
                        dbManager.insert("insert into FRIDGE values(null, " + "'out', " + 0 + ", " + temp + ", '" + listname + "', " + year + ", " + month + ", " + day + ", " + 0 + ");");
                    }
                    else{
                        System.out.println("===================111====================" + item_temp.getItemimage());
                        dbManager.insert("insert into FRIDGE values(null, " + "'out', " + temp + ", " + 0 + ", '" + listname + "', " + year + ", " + month + ", " + day + ", " + 0 + ");");
                    }

                    db1_check = true;
                    dismiss();
                }
            }
        });
        f1_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    public int imagechoose(String listname) {
        int icon = R.drawable.mint;;
        if (listname.equals("milk")) {
            icon = R.drawable.milk;
        }
        else if (listname.equals("beef")) icon = R.drawable.beef;
        else if (listname.equals("ramen")) icon = R.drawable.ramen;
        else if (listname.equals("juice")) icon = R.drawable.juice;
        return icon;
    }
    //////////////////dialog-editext에 대해 각각string 변환//////////////////

    public String getlistname() {
        return listname;
    }

    public String getlistcount() {
        return listcount;
    }

    public String getmemo() {
        return memo;
    }

    public void setlistname(String listname) {
        this.listname = listname;
        f1_listname.setText(listname);
        f1_listname.setFocusable(true);
    }

    public void setlistcount(String listcount) {
        this.listcount = listcount;
        f1_listcount.setText(listcount);
        f1_listcount.setFocusable(true);
    }

    public void setmemo(String memo) {
        this.memo = memo;
        f1_memo.setText(memo);
        f1_memo.setFocusable(true);
    }

    //////////////////dialog-editext에 대해 string 변환//////////////////
    public void EditToString(){
        listname = f1_listname.getText().toString();
        listcount = f1_listcount.getText().toString();
        memo = f1_memo.getText().toString();
    }



    //datepicker에서 들고온 함수이긴한데 뭐하는건지는 잘 모르겠습니다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getOwnerActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //////////////////////////border 함수////////////////////////////////////////


}
