package com.example.kgm13.requestfridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.kgm13.requestfridge.MainActivity.strcam;

/**
 * Created by KYS on 2017-06-09.
 */

public class F1_CameraList extends Dialog {

    private Context context;
    public static boolean camera_check = true;

    public F1_CameraList(@NonNull Context _context) {
        super(_context);
        this.context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f1_cameralist);

        final ArrayAdapter camera_adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_single_choice, strcam);

        final ListView camera_listview = (ListView) findViewById(R.id.cameralist);
        camera_listview.setAdapter(camera_adapter);

        Button addButton = (Button)findViewById(R.id.cameralist_add);
        Button modifyButton = (Button)findViewById(R.id.cameralist_modify);
        Button deleteButton = (Button)findViewById(R.id.cameralist_delete);
        Button setButton = (Button)findViewById(R.id.cameralist_set);
        Button registButton = (Button)findViewById(R.id.cameralist_regist);

        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("추가");
                final EditText name = new EditText(context);
                alert.setView(name);

                alert.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addname = name.getText().toString();
                        strcam.add(addname);
                        camera_adapter.notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });

        modifyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int count, checked;
                count = camera_adapter.getCount();

                if (count > 0) {
                    checked = camera_listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("수정");
                        final EditText name = new EditText(context);
                        alert.setView(name);

                        alert.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String addname = name.getText().toString();
                                strcam.set(checked, addname);
                                camera_adapter.notifyDataSetChanged();
                            }
                        });

                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int count, checked;
                count = camera_adapter.getCount();

                if (count > 0) {
                    checked = camera_listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        strcam.remove(checked);
                        camera_listview.clearChoices();
                        camera_adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        setButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        registButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                F1_Dialog dialog = new F1_Dialog(context);
                for (int i = 0; i < strcam.size(); i++) {
                    dialog.listname = strcam.get(i);
                    Log.e("Camera", dialog.listname);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.getData();
                }

                dismiss();

            }
        });

    }



}
