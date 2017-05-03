package com.example.kgm13.requestfridge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TabPagerAdapter extends FragmentStatePagerAdapter {

  private int tabCount;



  public TabPagerAdapter(FragmentManager fm, int tabCount) {
    super(fm);
    this.tabCount = tabCount;
  }

  @Override
  public Fragment getItem(int position) {
    // Returning the current tabs
    switch (position) {
      case 0:
        F1_Fridge fridge = new F1_Fridge();
        return fridge;
      case 1:
        F2_List list = new F2_List();
        return list;
      case 2:
        F3_Recipe recipe = new F3_Recipe();
        return recipe;
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return tabCount;
  }

}