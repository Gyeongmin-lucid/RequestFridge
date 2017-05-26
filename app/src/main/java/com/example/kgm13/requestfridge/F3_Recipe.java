package com.example.kgm13.requestfridge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class F3_Recipe extends Fragment {

    F3_ListViewAdapter adapter1;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_f3_recipe, container, false);
        ListView list = (ListView) view.findViewById(R.id.recipeListview);
        final ArrayList<RecipeInfo> recipes= new ArrayList<RecipeInfo>();
        recipes.add(new RecipeInfo("나물비빔밥","육수로 지은 밥에 야채를 듬뿍 넣은 영양만점 나물비빔밥!"));
        recipes.add(new RecipeInfo("오곡밥","정월대보름에 먹던 오곡밥! 영양을 한그릇에 담았습니다."));







        //listview로 선택된 레시피 정보 넘김
        adapter1= new F3_ListViewAdapter(recipes, this.getContext());
        list.setAdapter(adapter1);
        return view;
    }
}
