package com.example.kgm13.requestfridge;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.GlobalApplication.getGlobalApplicationContext;
import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.MainActivity.login_head;
import static com.example.kgm13.requestfridge.R.id.fab;

/**
 * Created by kgm13 on 2017-04-09.
 */

public class F1_GridViewAdapter extends ArrayAdapter<Item> {

    Context context;
    int layoutResourceId;
    ArrayList<Item> data = new ArrayList<Item>();
    FloatingActionButton fab;
    static int checknum;
    private int check_in, dayleft;
    private int e_y, e_m, e_d;
    private String listname;
    static boolean list_check = false;


    public F1_GridViewAdapter(Context context, int layoutResourceId, ArrayList<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        checknum = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final RecordHolder holder;
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fab = ((MainActivity) getContext()).getFAB();
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtDate = (TextView) row.findViewById(R.id.grid_date);
            holder.txtTitle = (TextView) row.findViewById(R.id.grid_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.grid_image);
            holder.gridlayout = (LinearLayout) row.findViewById(R.id.grid_layout);
            holder.checkimage = (ImageView) row.findViewById(R.id.checkimage);
            holder.check = false;
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Item item = data.get(position);
        holder.txtDate.setText(String.valueOf(item.getDate()));
        holder.txtTitle.setText(item.getItemname());
        listname = holder.txtTitle.getText().toString();
        dayleft = Integer.parseInt(holder.txtDate.getText().toString());

        if (item.getItemimage() == 0) {
            holder.imageItem.setImageBitmap(item.getItemimage_bitmap());
        } else {
            holder.imageItem.setImageResource(item.getItemimage());
        }

        holder.gridlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.check) {
                    holder.gridlayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ani));
                    holder.checkimage.setVisibility(view.INVISIBLE);
                    holder.check = false;
                    CalExpireday();
                    if (login_check) {
                        Lock lock = new ReentrantLock();
                        lock.lock();
                        try {
                            listname = holder.txtTitle.getText().toString();
                            dayleft = Integer.parseInt(holder.txtDate.getText().toString());
                            check_in = 0;
                            CalExpireday();
                            updateDelete();
                        }finally{
                            lock.unlock();
                        }
                    } else {
                        F1_DBManager dbmanager = new F1_DBManager(getContext().getApplicationContext(), "Fridge.db", null, 1);
                        ;
                        dbmanager.update("update FRIDGE set del = " + 0 + " where name = '" + listname + "';");
                    }
                    if (--checknum == 0) {
                        list_check = false;
                        fab.setBackgroundTintList(ColorStateList.valueOf(getGlobalApplicationContext().getResources().getColor(R.color.colorAccent)));
                        fab.setImageDrawable(getGlobalApplicationContext().getResources().getDrawable(R.drawable.plus));
                        fab.invalidate();
                    }
                } else {
                    list_check = true;
                    holder.gridlayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ani));
                    holder.checkimage.setVisibility(view.VISIBLE);
                    fab.setImageDrawable(getGlobalApplicationContext().getResources().getDrawable(R.drawable.minus));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getGlobalApplicationContext().getResources().getColor(R.color.orange)));
                    fab.refreshDrawableState();
                    holder.check = true;
                    checknum++;
                    CalExpireday();
                    if (login_check) {
                        Lock lock = new ReentrantLock();
                        lock.lock();
                        try {
                            listname = holder.txtTitle.getText().toString();
                            dayleft = Integer.parseInt(holder.txtDate.getText().toString());
                            check_in = 1;
                            CalExpireday();
                            updateDelete();
                        }finally{
                            lock.unlock();
                        }
                    } else {
                        F1_DBManager dbmanager = new F1_DBManager(getContext().getApplicationContext(), "Fridge.db", null, 1);
                        dbmanager.update("update FRIDGE set del = " + 1 + " where name = '" + listname + "';");
                    }
                }

            }
        });
        return row;
    }


    public class RecordHolder {
        TextView txtDate;
        TextView txtTitle;
        ImageView imageItem;
        ImageView checkimage;
        LinearLayout gridlayout;
        boolean check;
    }

    void CalExpireday() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, dayleft);
        e_y = date.get(Calendar.YEAR);
        e_m = date.get(Calendar.MONTH);
        e_d = date.get(Calendar.DAY_OF_MONTH);
    }



    void updateDelete() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_head + "&u_state=" + check_in + "&u_list=" + listname + "&u_year=" + e_y + "&u_month=" + e_m + "&u_day=" + e_d;
                try {
                    URL url = new URL("http://13.124.64.178/update_delete.php");

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
                System.out.println("================delete sql : " + result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
}
