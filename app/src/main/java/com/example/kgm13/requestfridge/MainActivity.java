package com.example.kgm13.requestfridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.F1_Dialog.db1_check;
import static com.example.kgm13.requestfridge.F1_Fridge.customGridAdapter;
import static com.example.kgm13.requestfridge.F1_Fridge.f1_view;
import static com.example.kgm13.requestfridge.F1_Fridge.gridArray;
import static com.example.kgm13.requestfridge.F1_Fridge.gridView;
import static com.example.kgm13.requestfridge.F2_List.f2_view;
import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.LoginActivity.login_token;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private TabLayout tabLayout;                    // 타이틀 3개를 나눠주는 tablayout
    private ViewPager viewPager;                    // 3개의 각각 layout를 띄울 페이저 변수
    public static Context context_final;
    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable @Bind(R.id.fab) FloatingActionButton fab;

    //token 변수
    boolean tokenout = false;

    //navigation 변수
    NavigationView navigationView;
    Menu nav_Menu;

    //spinner 내부
    private String[] NavSortItem = { "유통기한 짧은 순서", "먼저 들어온 순서", "카테고리 별"}; // Spinner items
    private String[] NavAlarmDateItem = {"1일", "2일","3일", "5일", "7일"};

    //viewpiger 변수
    boolean f1 = true;                              //fridge에 대한 페이저 view on,off 확인
    boolean f2 = false;                             //list에 대한 페이저 view on,off 확인

    public static String PACKAGE_NAME;              //현재 패키지 명에 대한 변수 : drawble에 있는 이미지에 대해서 string->int로 변환할때 쓰는 변수
    BackPressCloseHandler backPressCloseHandler;    //cancel를 두번 눌렸을때 취소가 되게 하기 위한 변수




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_Menu = navigationView.getMenu();

        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
        login_check = term.getBoolean("login_check", false);
        if(login_check){
            login_id = term.getString("ID", "");
            login_token = term.getString("Token", "");
        }

        PACKAGE_NAME = getApplicationContext().getPackageName();
        context_final = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder
                            .setMessage("추가")
                            .setPositiveButton("카메라로 추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, OCRActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("손으로 추가", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fab1();
                                }
                            });
                    builder.create().show();
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
                    f1 = true; f2 = false;
                    fab.setVisibility(View.VISIBLE);
                }
                if(position == 1){
                    f1 = false; f2 = true;
                    fab.setVisibility(View.VISIBLE);
                }
                if(position == 2){
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 2)
                    fab.setVisibility(View.GONE);

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

        hideItem();
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
        backPressCloseHandler.onBackPressed();
    }

    //로그인에 대한 보이고 안보이고의 상태 변화
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_sort_spinner) {
        }
        if (id == R.id.logout){
            tokenDelete(login_id);
            while(!tokenout){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            login_check = false;
            login_id = "";
            login_token = "";
            SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
            SharedPreferences.Editor editor = term.edit();
            editor.putString("ID", ""); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.putBoolean("login_check", false);
            editor.putString("Token", "");
            editor.commit();

            gridArray.clear();
            gridView.setAdapter(customGridAdapter);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.login){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.share){
            Dialog_share dialog = new Dialog_share(context_final);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideItem()
    {
        if(login_check){
            nav_Menu.findItem(R.id.login).setVisible(false);
            getShare();
        }
        else{
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.share).setVisible(false);
        }
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
            public void onShow(DialogInterface dialog) {

            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dia) {
            }
        });

        dialog.show();
    }

    /////////////////////sql code /////////////////////////
    //////////////////////////sql -> JSON 연동////////////////////////////////////////
    void getShare() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id;
                try {
                    URL url = new URL("http://13.124.64.178/share_confirm.php");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param.getBytes("UTF-8"));
                    outs.flush();
                    outs.close();

/* 서버 -> 안드로이드 파라메터값 전달 */
                    InputStream is = null;
                    BufferedReader in = null;
                    String data = "";

                    //is = conn.getErrorStream();
                    is = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                    String json;
                    StringBuilder sb = new StringBuilder();
                    while ((json = in.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if(!result.equals("null")){
                    final Dialog_Shareconfirm dialog = new Dialog_Shareconfirm(context_final, result);
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
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    public void tokenDelete(String id) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id;
                try {

                    URL url = new URL("http://13.124.64.178/token_delete.php");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

/* 안드로이드 -> 서버 파라메터값 전달 */
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param.getBytes("UTF-8"));
                    outs.flush();
                    outs.close();
                    tokenout = true;
/* 서버 -> 안드로이드 파라메터값 전달 */
                    InputStream is = null;
                    BufferedReader in = null;
                    String data = "";

                    is = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                    String json;
                    StringBuilder sb = new StringBuilder();
                    while ((json = in.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
}
