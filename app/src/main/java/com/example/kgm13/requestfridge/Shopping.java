package com.example.kgm13.requestfridge;

/**
 * Created by kgm13 on 2017-04-07.
 */

public class Shopping {
    //private int favorite;
    private String list;
    //private int button;

    public Shopping(){}

    public Shopping(String par_grade) {
        list = par_grade;
    }
//    public Shopping(int par_subject, String par_grade, int par_button) {
//        favorite = par_subject;
//        list = par_grade;
//        button = par_button;
//    }

//    //public int getFavorite() {
//        return this.favorite;
//    }

    public String getList() {
        return this.list;
    }

//    public int getButton() {
//        return this.button;
//    }
}
