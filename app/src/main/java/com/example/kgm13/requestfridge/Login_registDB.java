package com.example.kgm13.requestfridge;

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

public class Login_registDB extends AsyncTask<Void, Integer, Void> {
    String sId;
    String sPw;
    Login_registDB(String par_sId, String par_sPw){
        sId = par_sId;
        sPw = par_sPw;
    }

    @Override
    protected Void doInBackground(Void... unused) {

/* 인풋 파라메터값 생성 */
        String param = "&u_id=" + sId + "&u_pw=" + sPw + "";
        try {
/* 서버연결 */
            URL url = new URL(
                    "http://13.124.64.178/sign_up.php");
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