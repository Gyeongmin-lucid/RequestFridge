package com.example.kgm13.requestfridge;

/**
 * Created by G on 2017-05-12.
 */

public class RecipeInfo {
    String name, info;
    int num;
    public RecipeInfo(String name, String info){
        this.name = name;
        this.num = 111;
        this.info = info;
    }
    public int getNum() { return num;}
    public String getName(){
        return name;
    }
    public String getInfo(){
        return info;
    }
}
