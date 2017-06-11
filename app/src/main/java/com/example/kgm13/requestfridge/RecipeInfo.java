package com.example.kgm13.requestfridge;

/**
 * Created by G on 2017-05-12.
 */

public class RecipeInfo {
    String name, info;
    int num;
    public RecipeInfo(String name, String info, int num){
        this.name = name;
        this.info = info;
        this.num = num;
    }
    public String getName(){
        return name;
    }
    public String getInfo(){
        return info;
    }

    public int getNum() {
        return num;
    }
}
