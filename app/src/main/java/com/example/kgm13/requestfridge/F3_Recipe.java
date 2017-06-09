package com.example.kgm13.requestfridge;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import java.util.Date;

import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.RecommandDB.get_cuisine;
import static com.example.kgm13.requestfridge.RecommandDB.get_ingredient;

public class F3_Recipe extends Fragment {

    F3_ListViewAdapter adapter1;
    View view;
    String[] expirelist = new String[101];
    int[] recipelist = new int[500];
    int year, month, day;
    int t_year, t_month, t_day;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_f3_recipe, container, false);
        ListView list = (ListView) view.findViewById(R.id.recipeListview);
        ArrayList<RecipeInfo> recipes = new ArrayList<RecipeInfo>();
        recipes.add(new RecipeInfo("나물비빔밥", "육수로 지은 밥에 야채를 듬뿍 넣은 영양만점 나물비빔밥!", 1));
        recipes.add(new RecipeInfo("오곡밥", "정월대보름에 먹던 오곡밥! 영양을 한그릇에 담았습니다.",2));

        expireList(7, 5);
        int i =0;
        int j=0;
        while(get_ingredient("계란")[j]!= 0 && i <10){
            recipelist[j] = get_ingredient("계란")[j];
            recipes.add(new RecipeInfo(get_cuisine(recipelist[j],1),get_cuisine(recipelist[j],2),recipelist[j]));
            i++;
            j++;
        }
//        while(expirelist[i] != null){
//            while(get_ingredient("계란")[j]!= 0){
//                recipelist[i+j] = get_ingredient(expirelist[i])[j];
//            }
//        }
        for(int k=0; k<10; k++){

                recipes.add(new RecipeInfo(get_cuisine(recipelist[k],1),get_cuisine(recipelist[k],2),recipelist[k]));
        }



        adapter1 = new F3_ListViewAdapter(recipes, this.getContext());
        list.setAdapter(adapter1);

        return view;
    }



    private int set_dayleft(int year, int month, int day){
        long d, t, r;

        Calendar calendar =Calendar.getInstance();              //현재 날짜 불러옴
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(year,month, day);

        t=calendar.getTimeInMillis();                 //오늘 날짜를 밀리타임으로 바꿈
        d=dCalendar.getTimeInMillis();              //디데이날짜를 밀리타임으로 바꿈
        r=(d-t)/(24*60*60*1000);

        return (int)r;
    }

    void expireList(int expire, int listnum) {
        if (login_check) {
            //SQL 서버에 들어가게 되는 코드 (로그인을 한 유저의 sql DB접근하여 값을 뽑아냄)
            //0일 부터 n일(expire) 기준으로 할때 0일부터 차례대로 뽑아옵니다. (유통기한이 짧은 순으로 우선순위)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            t_year = calendar.get(Calendar.YEAR);
            t_month = calendar.get(Calendar.MONTH);
            t_day = calendar.get(Calendar.DATE);
            calendar.add(Calendar.DATE, expire);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
            get_expireList(listnum, year, month, day, t_year, t_month, t_day);
        }
        else {
            //내부디비에 접근하여 값을 불러오는 코드(비로그인 회원의 내부디비에 저장 되어 있는 부분을 가지고 옴)
            //아직 완벽하게 구현 X (0~n일 순이 아닌, n(expire)일 안에 순서대로 k(listnum)개를 뽑아오는 방식)
            F1_DBManager manager = new F1_DBManager(getActivity(), "Fridge.db", null, 1);
            SQLiteDatabase db = manager.getReadableDatabase();
            Cursor c = db.query("FRIDGE", null, null, null, null, null, null, null);
            int itemnum = 0;
            while (c.moveToNext()) { //db의 id를 하나씩 이동하면서 list를 추가합니다
                int del = c.getInt(c.getColumnIndex("del"));
                int year = c.getInt(c.getColumnIndex("year"));
                int month = c.getInt(c.getColumnIndex("month"));
                int day = c.getInt(c.getColumnIndex("day"));
                int dayleft = set_dayleft(year, month, day);
                if (del == 0 && dayleft <= expire && itemnum <listnum)
                    expirelist[itemnum++] = c.getString(c.getColumnIndex("name"));
            }
        }
    }

    void get_expireList(int listnum, int y, int m, int d, int ty, int tm, int td) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            String param;
            GetDataJSON(int par_l, int par_y, int par_m, int par_d, int par_ty, int par_tm, int par_td){
                param = "&u_id=" + login_id + "&u_listnum=" + par_l +
                        "&u_year=" + par_y + "&u_month=" + par_m + "&u_day=" + par_d +
                        "&u_tyear=" + par_ty + "&u_tmonth=" + par_tm + "&u_tday=" + par_td;
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    Log.i("get_expireList", param);
                    URL url = new URL("http://13.124.64.178/get_expirelist.php");

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
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                String myJSON = result;
                setlist(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON(listnum, y, m, d, ty, tm, td);
        g.execute();
    }

    //////////////////////////JSON -> android 연동////////////////////////////////////////
    void setlist(String myJSON) {
        String list;
        final String TAG_RESULTS = "result";
        final String TAG_NAME = "name";

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i = 0; i < jsonArray.length() ; i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                list = c.getString(TAG_NAME);
                expirelist[i] = list;
            }
            // expirelist를 쓰실거면 이부분 부터 이어서 사용해주세요!! 함수로 구현동작을 묶어서 함수 한개만 만 이 주석 부분에 놔두셔도 됩니다.
            // oncreate으로 돌아가서 expirelist 사용하는건 삼가해주세요!!(동작 방식으로 인해서 oncreate부에서 사용할시 값이 제대로 들어가지 않습니다.)
            // expirelist는 {a, b, c, d , null, null, null, .... ,null} 이런식으로 들어가게 됩니다.
            // 마지막을 체킹 할때 null을 이용해서 체킹해주세요. 그리고 listnum 을 100개 이상 뽑으시고 싶으시면 expirelist declare 부에 new String[~]을 조절해주세요.
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}