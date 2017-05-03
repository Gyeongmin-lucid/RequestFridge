package com.example.kgm13.requestfridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.kgm13.requestfridge.F1_Dialog.db1_check;
import static com.example.kgm13.requestfridge.F1_Fridge.f1_view;
import static com.example.kgm13.requestfridge.F2_List.f2_view;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    Toolbar toolbar;
    private TabLayout tabLayout;    // 타이틀 3개를 나눠주는 tablayout
    private ViewPager viewPager;    // 3개의 각각 layout를 띄울 페이저 변수
    public static Context context_final;
    FloatingActionButton fab;


    //spinner 내부
    private String[] NavSortItem = { "유통기한 짧은 순서", "먼저 들어온 순서", "카테고리 별"}; // Spinner items
    private String[] NavAlarmDateItem = {"1일", "2일","3일", "5일","7일"};
    boolean f1 = true;
    boolean f2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_final = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f1) {
                    fab1();
                } else if (f2) {
                    fab2();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    f1 = true;
                    f2 = false;
                    fab.setVisibility(View.VISIBLE);
                }
                if(position == 1){
                    f1 = false;
                    f2 = true;
                    fab.setVisibility(View.VISIBLE);
                }
                if(position == 2){
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 2){
                    fab.setVisibility(View.GONE);
                }
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


//spinner
        final Spinner alarmspinner;
        final Spinner sortspinner;
        Menu menu1 = navigationView.getMenu();
        Menu menu2 = navigationView.getMenu();
        MenuItem alarmitem = menu2.findItem(R.id.alarm_switch);
        SwitchCompat switchCompat = (SwitchCompat)alarmitem.getActionView().findViewById(R.id.switchcompat);
        switchCompat.setOnCheckedChangeListener(this);
        sortspinner = (Spinner) menu1.findItem(R.id.nav_sort_spinner).getActionView();
        sortspinner.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, NavSortItem));
        sortspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = NavSortItem[0];
                if(!a.equals(sortspinner.getSelectedItem().toString())){
                    Toast.makeText(MainActivity.this, NavSortItem[position], Toast.LENGTH_SHORT).show();
                    a = NavSortItem[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }});
        alarmspinner = (Spinner) menu2.findItem(R.id.nav_alarm_spinner).getActionView();
        alarmspinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, NavAlarmDateItem));
        alarmspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = NavAlarmDateItem[0];
                if(!a.equals(alarmspinner.getSelectedItem().toString())){
                    Toast.makeText(MainActivity.this, NavAlarmDateItem[position] + " 전 알람", Toast.LENGTH_SHORT).show();
                    a = NavAlarmDateItem[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onStop() {
        if(db1_check) {
            SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
            SharedPreferences.Editor editor = term.edit();
            editor.putBoolean("F1_db", true); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.commit();
        }

        super.onStop();
    }

    /*************************************** 좌측 탭바 부분 **********************************************/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sort_spinner) {
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*************************************** main Tablayout **********************************************/
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("냉장고");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.refrigerator, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("장보기 목록");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.shopping_cart, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("레시피 검색");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.recipe, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new F1_Fridge(), "냉장고");
        adapter.addFrag(new F2_List(), "장보기 목록");
        adapter.addFrag(new F3_Recipe(), "레시피 검색");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }


    public void fab1() {
        final F1_Dialog dialog = new F1_Dialog(f1_view.getContext());
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

    public void fab2() {
        final F2_Dialog dialog = new F2_Dialog(f2_view.getContext());
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
}
