package com.example.kgm13.requestfridge;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kgm13.requestfridge.F1_Dialog.createImage;
import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.MLRoundedImageView.border;
import static com.example.kgm13.requestfridge.MLRoundedImageView.getCroppedBitmap;
import static com.example.kgm13.requestfridge.MainActivity.PACKAGE_NAME;
import static com.example.kgm13.requestfridge.PermissionUtils.isOnline;


public class F1_Fridge extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static View f1_view;         //frdige에 대한 view를 공유 (dialog와 같이)

    ////////////////////////////DB 변수////////////////////////////
    SQLiteDatabase db;
    boolean FridgeDB_check;              // 끄고 다시 들어올때 db가 있는지 없는지 체크

    int image;
    String location, name;
    int year, month, day, dayleft, del;       // D-day의 year,month, day + dayleft : day기준 얼마나 남았는지 계산



    ////////////////////////////gridview 변수////////////////////////////
    SwipeRefreshLayout swipeRefreshLayout;
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


        swipeRefreshLayout = (SwipeRefreshLayout) f1_view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.yellow, R.color.red, R.color.Black, R.color.blue);

        FridgeDB_check = term.getBoolean("F1_db", false);
        if(FridgeDB_check && !login_check) {
            try {
                // 데이터베이스 객체를 얻어오는 다른 간단한 방법
                //location을 저장을 하고 들고오지 않음 아직!!! 이거 수정해야함!!!!
                db = manager.getReadableDatabase();
                Cursor c = db.query("FRIDGE", null, null, null, null, null, null, null);
                gridArray.clear();
                while (c.moveToNext()) { //db의 id를 하나씩 이동하면서 list를 추가합니다
                    int del = c.getInt(c.getColumnIndex("del"));
                    if (del == 0) {
                        image = c.getInt(c.getColumnIndex("image"));
                        name = c.getString(c.getColumnIndex("name"));
                        year = c.getInt(c.getColumnIndex("year"));
                        month = c.getInt(c.getColumnIndex("month"));
                        day = c.getInt(c.getColumnIndex("day"));

                        dayleft = set_dayleft(year,month,day);
                        gridArray.add(new Item(set_image(image, dayleft), name ,dayleft));
                        customGridAdapter.notifyDataSetChanged();
                    }
                }

                gridView.setAdapter(customGridAdapter);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        else {
            gridArray.clear();
            SQLgetdata();
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

    public int set_dayleft(int year, int month, int day){
        long d, t, r;

        Calendar calendar =Calendar.getInstance();              //현재 날짜 불러옴
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(year,month, day);

        t=calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d=dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r=(d-t)/(24*60*60*1000);

        return (int)r;
    }

    public Bitmap set_image(int image, int dayleft){
        Bitmap imagebitmap;
        System.out.println("======image, dayleft : " + image + dayleft);
        if(image != 0){
            imagebitmap = BitmapFactory.decodeResource(f1_view.getContext().getResources(), image);
        }
        else{
            imagebitmap = createImage(300, 300, Color.parseColor("#E1FF36"));
        }
        System.out.println("========imagebitmap : " + imagebitmap );
        imagebitmap = getCroppedBitmap(imagebitmap, imagebitmap.getHeight() / 2);
        imagebitmap = border(imagebitmap, dayleft);

        return imagebitmap;
    }

    //////////////////////////sql -> JSON 연동////////////////////////////////////////
    void SQLgetdata() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id;
                try {
                    URL url = new URL("http://13.124.64.178/get_fridge.php");

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
                showList(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    //////////////////////////JSON -> android 연동////////////////////////////////////////
    void showList(String myJSON) {
        final String TAG_RESULTS = "result";
        final String TAG_location = "location";
        final String TAG_URL = "image_url";
        final String TAG_name = "name";
        final String TAG_year = "year";
        final String TAG_month = "month";
        final String TAG_day = "day";
        final String TAG_del = "del";

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i = 0; i < jsonArray.length() ; i++) {
                //순서 location, url, name, ytaer, month, day, del
                System.out.println("=================i : " + i);
                JSONObject c = jsonArray.getJSONObject(i);
                location = c.getString(TAG_location);
                image = Integer.parseInt(c.getString(TAG_URL));
                name = c.getString(TAG_name);
                year = Integer.parseInt(c.getString(TAG_year));
                month = Integer.parseInt(c.getString(TAG_month));
                day = Integer.parseInt(c.getString(TAG_day));
                del = Integer.parseInt(c.getString(TAG_del));
                dayleft = set_dayleft(year,month,day);
                System.out.println("======location, image, name, year, month, day :" + location + "\t"+ image+ "\t" + name+ "\t" + year + month + "\t" + day);
                gridArray.add(new Item(set_image(image, dayleft), name ,dayleft));
                customGridAdapter.notifyDataSetChanged();
            }
            gridView.setAdapter(customGridAdapter);

        } catch (JSONException e) {
            System.out.println("error33");
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gridArray.clear();
                SQLgetdata();
                gridView.setAdapter(customGridAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        },2000);
    }
}
