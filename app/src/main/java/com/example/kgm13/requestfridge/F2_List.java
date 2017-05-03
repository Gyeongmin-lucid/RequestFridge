package com.example.kgm13.requestfridge;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class F2_List extends Fragment {
    static View f2_view;

    /*****************************listview variable*******************************/
//    int[] items_toggle = new int[100];
//    String[] items_list = new String[100];
//    int[] items_button = new int[100];
    static ArrayList<Shopping> arrayList_freeze = new ArrayList<Shopping>(); // list들의 집합
    static F2_ListViewAdapter adapter_freeze;                                // list들의 집합들의 adapter
    static ArrayList<Shopping> arrayList_cold = new ArrayList<Shopping>();
    static F2_ListViewAdapter adapter_cold;
    static ArrayList<Shopping> arrayList_out = new ArrayList<Shopping>();
    static F2_ListViewAdapter adapter_out;

    static ListView listView_freeze;
    static ListView listView_cold;
    static ListView listView_out;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        f2_view = inflater.inflate(R.layout.activity_f2_list, container, false);

        final View freeze_header = inflater.inflate(R.layout.activity_f2_freeze_header, null, false);
        final View cold_header = inflater.inflate(R.layout.activity_f2_cold_header, null, false);
        final View out_header = inflater.inflate(R.layout.activity_f2_out_header, null, false);

        listView_freeze = (ListView) f2_view.findViewById(R.id.f2_list_listview_freeze);
        listView_cold = (ListView) f2_view.findViewById(R.id.f2_list_listview_cold);
        listView_out = (ListView) f2_view.findViewById(R.id.f2_list_listview_out);

        listView_freeze.addHeaderView(freeze_header);
        listView_cold.addHeaderView(cold_header);
        listView_out.addHeaderView(out_header);

        adapter_freeze = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_freeze);
        adapter_cold = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_cold);
        adapter_out = new F2_ListViewAdapter(getActivity().getApplicationContext(), arrayList_out);

//        listView_freeze.setAdapter(adapter_freeze);
//        listView_cold.setAdapter(adapter_cold);
//        listView_out.setAdapter(adapter_out);

        /* setListViewHeightBasedOnChildren(listView_freeze);
        setListViewHeightBasedOnChildren(listView_cold);
        setListViewHeightBasedOnChildren(listView_out); */

        return f2_view;
    }

    public void fab2(){
        final F2_Dialog dialog = new F2_Dialog(getActivity());
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dia) {
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dia) {
            }
        });

        dialog.show();
    }

    /* public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    } */
}
