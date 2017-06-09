package com.example.kgm13.requestfridge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by G on 2017-05-12.
 */

public class F3_ListViewAdapter extends ArrayAdapter<RecipeInfo> {
    private ArrayList<RecipeInfo> recipes;
    Context context1;

    private static class ViewHolder{
        TextView name;
        TextView info;

    }
    public F3_ListViewAdapter(ArrayList<RecipeInfo> recipes, Context context){
        super(context, R.layout.activity_f3_listview, recipes);
        this.recipes=recipes;
        this.context1 = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RecipeInfo dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_f3_listview, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.textname);
            viewHolder.info = (TextView) convertView.findViewById(R.id.textinfo);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.name.setText(dataModel.getName());
        viewHolder.info.setText(dataModel.getInfo());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context1, F3_Recipeview.class);
                intent.putExtra("position", position);
                intent.putExtra("name", getItem(position).getName());
                intent.putExtra("info", getItem(position).getInfo());
                context1.startActivity(intent);
//                int astring = getItem(position).getNum();
//                Toast.makeText(context1,getItem(position).getName() + "이 선택되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

}
