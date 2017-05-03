package com.example.kgm13.requestfridge;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.kgm13.requestfridge.MainActivity.context_final;

/**
 * Created by kgm13 on 2017-04-07.
 */

public class F2_ListViewAdapter extends BaseAdapter implements View.OnClickListener {

    Context mContext;
    LayoutInflater inflater;
    LayoutInflater inflater_final;
    private List<Shopping> Shoppinglist = null;
    private ArrayList<Shopping> arraylist;


    // ListViewAdapter의 생성자

    public F2_ListViewAdapter(Context context_, List<Shopping> Shoppinglist_) {
        mContext = context_;
        Shoppinglist = Shoppinglist_;
        inflater = LayoutInflater.from(mContext);
        inflater_final = LayoutInflater.from(context_final);

        this.arraylist = new ArrayList<Shopping>();
        this.arraylist.addAll(Shoppinglist);
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return Shoppinglist.size();
    }

    @Override
    public Shopping getItem(int position) {
        return Shoppinglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //view를 불러오는 과정
    public View getView(final int position, View view, final ViewGroup parent) {
        final F2_ListViewAdapter.ViewHolder holder;
        if (view == null) {
            holder = new F2_ListViewAdapter.ViewHolder();
            view = inflater.inflate(R.layout.activity_f2_list_listview, null);

            holder.favorite = (ToggleButton) view.findViewById(R.id.favorite_button);
            holder.list = (TextView) view.findViewById(R.id.list_textview);
            holder.button = (Button) view.findViewById(R.id.delete_button);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.favorite.isChecked()) {
                    holder.favorite.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.star_full));
                }
                else {
                    holder.favorite.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.star_blank));
                }

            }
        });
        holder.list.setText(Shoppinglist.get(position).getList());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shoppinglist.remove(position);
                notifyDataSetChanged();
                Snackbar.make(v, "삭제되었습니다", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
        });

        return view;
    }


    @Override
    public void onClick(View v) {

    }

    public class ViewHolder {
        ToggleButton favorite;
        TextView list;
        Button button;
    }
}

