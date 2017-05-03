package com.example.kgm13.requestfridge;

import android.graphics.Bitmap;

/**
 * Created by kgm13 on 2017-04-09.
 */

public class Item {
    private int itemimage;
    private Bitmap itemimage_bitmap;
    private String itemname;
    private int date;

    public Item(){}

    public Item(int par_itemimage, String par_itemname, int par_date) {
        itemimage = par_itemimage;
        itemname = par_itemname;
        date = par_date;
    }
    public Item(Bitmap par_itemimage_bitmap, String par_itemname, int par_date){
        itemimage = 0;
        itemimage_bitmap = par_itemimage_bitmap;
        itemname = par_itemname;
        date = par_date;
    }
    //
    public int getItemimage() {
        return this.itemimage;
    }
    public Bitmap getItemimage_bitmap(){
        return this.itemimage_bitmap;
    }

    public String getItemname() {
        return this.itemname;
    }

    public int getDate() {
        return this.date;
    }
}
