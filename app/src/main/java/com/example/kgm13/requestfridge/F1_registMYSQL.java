package com.example.kgm13.requestfridge;

/**
 * Created by kgm13 on 2017-05-06.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class F1_registMYSQL extends AsyncTask<Void, Integer, Void> {
    String location, image_url, name;
    int year, month, day, del;
    F1_registMYSQL(String par_location, String par_image_url, String par_name,
                   int par_year, int par_month, int par_day, int par_del){
        location = par_location; image_url = par_image_url; name = par_name;
        year = par_year; month = par_month; day = par_day; del = par_del;
    }

    @Override
    protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
        String param = "&u_location=" + location + "&u_image_url" + image_url + "&u_name" + name +
                        "&u_year" + year + "&u_month" + month + "&u_day" + day + "&u_del" + del;
        try {
/* 서버연결 */
            URL url = new URL(
                    "http://13.124.64.178/.php");
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