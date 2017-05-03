package com.example.kgm13.requestfridge;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kgm13 on 2017-04-09.
 */

public class F1_GridViewAdapter extends ArrayAdapter<Item> {

    Context context;
    int layoutResourceId;
    ArrayList<Item> data = new ArrayList<Item>();


    public F1_GridViewAdapter(Context context, int layoutResourceId, ArrayList<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtDate =  (TextView) row.findViewById(R.id.grid_date);
            holder.txtTitle = (TextView) row.findViewById(R.id.grid_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.grid_image);
            row.setTag(holder);
        }
        else {
            holder = (RecordHolder) row.getTag();
        }

        Item item = data.get(position);
        holder.txtDate.setText(String.valueOf(item.getDate()));
        holder.txtTitle.setText(item.getItemname());
        if(item.getItemimage() == 0) {
            holder.imageItem.setImageBitmap(item.getItemimage_bitmap());
        }
        else {
            holder.imageItem.setImageResource(item.getItemimage());
        }
        return row;
    }

    static class RecordHolder {
        TextView txtDate;
        TextView txtTitle;
        ImageView imageItem;
    }

}
