package com.example.kgm13.requestfridge;

/**
 * Created by G on 2017-05-12.
 */

public class RecipeInfo {
    String name, info;
    public RecipeInfo(String name, String info){
        this.name = name;
        this.info = info;
    }
    public String getName(){
        return name;
    }
    public String getInfo(){
        return info;
    }
}
