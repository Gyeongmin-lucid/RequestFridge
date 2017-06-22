package com.example.kgm13.requestfridge;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.kgm13.requestfridge.RecommandDB.get_cuisine;
import static com.example.kgm13.requestfridge.RecommandDB.get_stage;

/**
 * Created by G on 2017-06-09.
 */

public class F3_Recipeview extends Activity {
    ImageView image;
    Bitmap img;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f3_recipeview);
        String [] details = new String[10];
        String todetails= "";
        image = (ImageView)findViewById(R.id.recipeimage);
        TextView name = (TextView)findViewById(R.id.recipename);
        TextView info = (TextView)findViewById(R.id.recipeinfo);
        TextView detail = (TextView)findViewById(R.id.recipedetail);
        Intent intent = getIntent();
        int recipenum = intent.getIntExtra("num", 0);
        if (recipenum == 0){
            name.setText("error");
        }
        else {
            name.setText(get_cuisine(recipenum, 1));
            info.setText(get_cuisine(recipenum,2));
            new DownloadImageTask().execute(get_cuisine(recipenum,3));
            details = get_stage(recipenum);
            for(int i =0; i<10;i++){
                if(details[i] == null){
                    break;
                }
                todetails += details[i];
                todetails += "\n";
            }
            detail.setText(todetails);
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls){
            // TODO Auto-generated method sub
            try{
                URL myurl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myurl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                img= BitmapFactory.decodeStream(is);
            }catch (IOException e){
                e.printStackTrace();;
            }
            return img;
        }
        protected void onPostExecute(Bitmap img){
            image.setImageBitmap(img);
        }
    }
}