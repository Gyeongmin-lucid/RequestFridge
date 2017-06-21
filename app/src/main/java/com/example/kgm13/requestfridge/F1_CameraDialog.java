package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.F1_Fridge.customGridAdapter;
import static com.example.kgm13.requestfridge.F1_Fridge.gridArray;
import static com.example.kgm13.requestfridge.F1_Fridge.gridView;
import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.MLRoundedImageView.border;
import static com.example.kgm13.requestfridge.MLRoundedImageView.getCroppedBitmap;
import static com.example.kgm13.requestfridge.MainActivity.PACKAGE_NAME;
import static com.example.kgm13.requestfridge.PermissionUtils.isOnline;
import static com.example.kgm13.requestfridge.F1_CameraList.camera_check;

/**
 * Created by kgm13 on 2017-04-09.
 * 설명 : 냉장고 탭에서 + 버튼을 누를시 손으로 추가할 수 있는 창의 역할입니다
 */


public class F1_CameraDialog extends Dialog {
    ////////////////////////////내부DB 변수////////////////////////////
    F1_DBManager dbManager;
    static boolean db1_check = false;

    ////////////////////////////dialog 변수////////////////////////////
    String listname, listcount, memo, url;    // 물품병, 개수 , 메모, image_url
    String location = "cold";      // 위치

    Calendar c;
    int year, month, day;                       // 유통기한 년도(mysql-자동)
    int year_pick, month_pick, day_pick;        // 유통기한 년도(datepicker - 수동)
    int tYear, tMonth, tDay;                    //오늘 연월일 변수
    int dayleft = 0;                            // 오늘로 부터 남은 유통기한 날수


    //library를 이용해서 xml의 id를 불러옴
    //gradle에 compile 'com.jakewharton:butterknife:7.0.1'추가
    //onCreate부의 setContentView밑에 ButteerKnife.bing(this); 추가

    @Nullable @Bind(R.id.f1_cancel) Button f1_cancel;
    @Nullable @Bind(R.id.f1_listname) EditText f1_listname;
    @Nullable @Bind(R.id.f1_listcount) EditText f1_listcount;
    @Nullable @Bind(R.id.f1_datepicker) DatePicker f1_datepicker;
    @Nullable @Bind(R.id.f1_memo) EditText f1_memo;
    @Nullable @Bind(R.id.f1_leftbtn) Button f1_leftbtn;
    @Nullable @Bind(R.id.f1_rightbtn) Button f1_rightbtn;


    public F1_CameraDialog(Context context) {
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

                        Calendar calendar = Calendar.getInstance();              //현재 날짜 불러옴
                        Calendar dCalendar = Calendar.getInstance();
                        dCalendar.set(year_, monthOfYear_, dayOfMonth_);

                        tYear = calendar.get(Calendar.YEAR);
                        tMonth = calendar.get(Calendar.MONTH);
                        tDay = calendar.get(Calendar.DAY_OF_MONTH);

                        long t = calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
                        long d = dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
                        long r = (d - t) / (24 * 60 * 60 * 1000);
                        dayleft = (int) r;

                        if (dayleft < 0)
                            Snackbar.make(view, "유통기한을 확인해주세요!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                });


        ////////////////////////////////구현해야 할 사항////////////////////////////////////
        //'계속 추가하기'에 대한 event
        f1_leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();
                if (dayleft < 0 || (TextUtils.isEmpty(listname)))
                    Snackbar.make(v, "설정을 다시해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else {
                    getData();
                    Snackbar.make(v, listname +"이(가) 추가되었습니다!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    db1_check = true;
                }
            }
        });

        //'등록'에 대한 event
        f1_rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditToString();
                if (dayleft < 0 || (TextUtils.isEmpty(listname)))
                    Snackbar.make(v, "설정을 다시해주세요!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else {
                    getData();
                    db1_check = true;
                    dismiss();
                }
            }
        });

        f1_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public int imagechoose(String listname) {
        int icon;
        if (listname.equals("milk"))        icon = R.drawable.milk;
        else if (listname.equals("beef"))   icon = R.drawable.beef;
        else if (listname.equals("ramen"))  icon = R.drawable.ramen;
        else if (listname.equals("juice"))  icon = R.drawable.juice;
        else{
            icon = 0;
        }
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
    public void EditToString() {
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

    //////////////////////////sql -> JSON 연동////////////////////////////////////////
    void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String param = "&u_name=" + listname;
                try {
                    URL url = new URL("http://13.124.64.178/select_list.php");

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
                System.out.println("==========result : " + result);
                showList(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    //////////////////////////JSON -> android 연동////////////////////////////////////////
    void showList(String myJSON) {

        final String TAG_RESULTS = "result";
        final String TAG_URL = "list";
        final String TAG_EXPIRE = "expire";
        final String TAG_LOCATION = "location";
        try {
            if(myJSON.equals("null")){
                throw new Exception ("오류발생!");
            }
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_RESULTS);
            JSONObject c = jsonArray.getJSONObject(0);

            url = c.getString(TAG_URL);
            location = c.getString(TAG_LOCATION);
            dayleft = c.getInt(TAG_EXPIRE);

            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, dayleft);

            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);
            day = date.get(Calendar.DAY_OF_MONTH);
            String resName = "@drawable/" + url;
            int image = getContext().getResources().getIdentifier(resName, "drawable", PACKAGE_NAME);
            setImageToSQLite(image);

        }
        catch (JSONException e) {
            int image = imagechoose(listname);
            setImageToSQLite(image);
        }
        catch (Exception t) {
            int image = imagechoose(listname);
            setImageToSQLite(image);
        }
    }

    void setImageToSQLite(int image) {
        Bitmap bit_image;
        if(image == 0) {
            bit_image = createImage(300, 300, Color.parseColor("#E1FF36"));
        }
        else {
            bit_image = BitmapFactory.decodeResource(getContext().getResources(), image);
        }
        bit_image = getCroppedBitmap(bit_image, bit_image.getHeight() / 2);
        bit_image = border(bit_image, dayleft);

        Item item_temp = new Item(bit_image, listname, dayleft);

        gridArray.add(item_temp);
        customGridAdapter.notifyDataSetChanged();
        gridView.setAdapter(customGridAdapter);
        if(login_check)
            setData(image);
        else {
            Log.e("DB", String.valueOf(image));
            Log.e("DB", listname);
            camera_check = true;
            dbManager.insert("insert into FRIDGE values(null, '" + location + "', " + image + ", " + 0 + ", '" + listname + "', " + year + ", " + month + ", " + day + ", " + 0 + ");");

        }
    }
    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

    void setData(int image) {

        class set_fridge extends AsyncTask<String, Void, String> {
            int img;
            set_fridge(int par_img){
                img = par_img;
            }

            @Override
            protected String doInBackground(String... strings) {
                String param = "&u_id=" + login_id + "&u_location=" + location + "&u_imageUrl=" + String.valueOf(img)+ "&u_name=" + listname + "&u_year=" + String.valueOf(year) +
                        "&u_month=" + String.valueOf(month) + "&u_day=" + String.valueOf(day) + "&u_del=" + "0";
                try {
                    URL url = new URL("http://13.124.64.178/set_fridge.php");

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
                    camera_check = true;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        set_fridge g = new set_fridge(image);
        g.execute();
    }
}
