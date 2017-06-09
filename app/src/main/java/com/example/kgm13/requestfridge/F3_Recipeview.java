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
        image = (ImageView)findViewById(R.id.recipeimage);
        TextView name = (TextView)findViewById(R.id.recipename);
        TextView info = (TextView)findViewById(R.id.recipeinfo);

        Intent intent = getIntent();
        int a = intent.getIntExtra("position", 0);
        name.setText(intent.getStringExtra("name"));
        info.setText(intent.getStringExtra("info"));
        new DownloadImageTask().execute("http://file.okdab.com/UserFiles/searching/recipe/005600.jpg");


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
