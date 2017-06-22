package com.example.kgm13.requestfridge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageContext;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.F1_Dialog.db1_check;
import static com.example.kgm13.requestfridge.F1_Fridge.customGridAdapter;
import static com.example.kgm13.requestfridge.F1_Fridge.f1_view;
import static com.example.kgm13.requestfridge.F1_Fridge.gridArray;
import static com.example.kgm13.requestfridge.F1_Fridge.gridView;
import static com.example.kgm13.requestfridge.F2_List.f2_view;
import static com.example.kgm13.requestfridge.LoginActivity.login_auto;
import static com.example.kgm13.requestfridge.LoginActivity.login_check;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.LoginActivity.login_token;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private TabLayout tabLayout;                    // 타이틀 3개를 나눠주는 tablayout
    private ViewPager viewPager;                    // 3개의 각각 layout를 띄울 페이저 변수
  //  private static int ONE_MINUTE=5626;
    TimePicker mTimePicker;
    Calendar mCalendar;
    public static Context context_final;
    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    @Nullable @Bind(R.id.fab) FloatingActionButton fab;
    //실시간 db변수
    public static String login_head = "";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    public static int perform = 1;//처음 부른 시간을 가져옴. 중복으로 들고오는걸 막아줌
    public static String[] ocrtemp = new String[200];
    public static ArrayList<String> strcam = new ArrayList<String>();

    //token 변수
    boolean tokenout = false;

    //navigation 변수
    NavigationView navigationView;
    Menu nav_Menu;

    //spinner 내부
    private String[] NavSortItem = {"유통기한 짧은 순서", "먼저 들어온 순서", "카테고리 별"}; // Spinner items
    private String[] NavAlarmHourItem = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13","14", "15", "16", "17","18","19","20","21","22","23","24"};
    private String[] NavAlarmMinItem = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13","14", "15", "16", "17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","0"};

    //viewpiger 변수
    boolean f1 = true;                              //fridge에 대한 페이저 view on,off 확인
    boolean f2 = false;                             //list에 대한 페이저 view on,off 확인

    public static String PACKAGE_NAME;              //현재 패키지 명에 대한 변수 : drawble에 있는 이미지에 대해서 string->int로 변환할때 쓰는 변수
    BackPressCloseHandler backPressCloseHandler;    //cancel를 두번 눌렸을때 취소가 되게 하기 위한 변수


    private static final String CLOUD_VISION_API_KEY = "AIzaSyC2xSl-DIQ3DAODIrFROW_-fHF-tqxmP9s";
    public static final String FILE_NAME = "temp.jpg";

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
        new mAlarm(getApplicationContext()).Alarm();
        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);


        PACKAGE_NAME = getApplicationContext().getPackageName();
        context_final = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initstr(ocrtemp);
        getlist();

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
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                    builder2
                                            .setMessage("사진을 선택해 주세요")
                                            .setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startGalleryChooser();
                                                }
                                            })
                                            .setNegativeButton("카메라", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startCamera();
                                                }
                                            });
                                    builder2.create().show();
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
                if (position == 0) {
                    f1 = true;
                    f2 = false;
                    fab.setVisibility(View.VISIBLE);
                }
                if (position == 1) {
                    f1 = false;
                    f2 = true;
                    fab.setVisibility(View.VISIBLE);
                }
                if (position == 2) {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2)
                    fab.setVisibility(View.GONE);
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

//spinner
        final Spinner alarmspinner;
        final Spinner alarmspinnermin;
//        final Spinner sortspinner;
        Menu menu1 = navigationView.getMenu();
//        mTimePicker = (TimePicker)menu1.findItem(R.id.nav_time_picker);
//        Menu menu2 = navigationView.getMenu();
        MenuItem alarmitem = menu1.findItem(R.id.alarm_switch);

        SwitchCompat switchCompat = (SwitchCompat) alarmitem.getActionView().findViewById(R.id.switchcompat);
        switchCompat.setOnCheckedChangeListener(this);
//        sortspinner = (Spinner) menu1.findItem(R.id.nav_sort_spinner).getActionView();
//        sortspinner.setAdapter(new ArrayAdapter<String>(
//                this, android.R.layout.simple_spinner_dropdown_item, NavSortItem));
//        sortspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String a = NavSortItem[0];
//                if (!a.equals(sortspinner.getSelectedItem().toString())) {
//                    Toast.makeText(MainActivity.this, NavSortItem[position], Toast.LENGTH_SHORT).show();
//                    a = NavSortItem[position];
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        alarmspinner = (Spinner) menu1.findItem(R.id.nav_alarm_spinner_hour).getActionView();
        alarmspinnermin = (Spinner) menu1.findItem(R.id.nav_alarm_spinner_min).getActionView();
        alarmspinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, NavAlarmHourItem));
        alarmspinnermin.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, NavAlarmMinItem));
        alarmspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = NavAlarmHourItem[0];
                if (!a.equals(alarmspinner.getSelectedItem().toString())) {
                    Toast.makeText(MainActivity.this, NavAlarmHourItem[position] + " 시", Toast.LENGTH_SHORT).show();
                    a = NavAlarmHourItem[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        alarmspinnermin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = NavAlarmMinItem[0];
                if (!a.equals(alarmspinner.getSelectedItem().toString())) {
                    Toast.makeText(MainActivity.this, NavAlarmMinItem[position] + " 분", Toast.LENGTH_SHORT).show();
                    a = NavAlarmMinItem[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        hideItem();
        if(login_head.equals("")) {
            checkhead();
        }
        mCalendar = Calendar.getInstance();
        int hour, min;
        databaseReference.child("share").limitToLast(1).addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                FirebaseDB firemessage = dataSnapshot.getValue(FirebaseDB.class);  // chatData를 가져오고
                final String TAG_SENDTIME = "sendtime";
                try {
                    JSONObject c = new JSONObject(firemessage.getMessage());
                    long timecheck = System.currentTimeMillis() - c.getLong(TAG_SENDTIME);
                    if ((perform-- == 1) && (timecheck < 3000)) {
                        getShare();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStop() {
        SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
        SharedPreferences.Editor editor = term.edit();
        if (db1_check)
            editor.putBoolean("F1_db", true); //First라는 key값으로 infoFirst 데이터를 저장한다.

        editor.putString("ID", login_id); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.putBoolean("login_check", login_check);
        editor.putBoolean("login_auto", login_auto);
        editor.putString("ID_head", login_head);
        editor.putString("Token", login_token);
        editor.commit();
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
        if (id == R.id.logout) {
            tokenDelete(login_id);
            while (!tokenout) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            login_check = false;
            login_auto = false;
            login_head = "";
            login_id = "";
            login_token = "";
            SharedPreferences term = getSharedPreferences("term", MODE_PRIVATE);
            SharedPreferences.Editor editor = term.edit();
            editor.putString("ID", ""); //First라는 key값으로 infoFirst 데이터를 저장한다.
            editor.putBoolean("login_check", false);
            editor.putBoolean("login_auto", false);
            editor.putString("Token", "");
            editor.commit();

            gridArray.clear();
            gridView.setAdapter(customGridAdapter);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.share) {
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

    private void hideItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (login_check) {
            nav_Menu.findItem(R.id.login).setVisible(false);
            getShare();
        } else {
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

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uploadImage(Uri.fromFile(getCameraFile()));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            private ProgressDialog mDlg;

            @Override
            protected void onPreExecute() {
                mDlg = new ProgressDialog(MainActivity.this);
                mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDlg.setMessage("잠시만 기다려 주세요...");
                mDlg.show();

                super.onPreExecute();
            }
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        ImageContext imageContext = new ImageContext();
                        String[] languages = {"ko"};
                        imageContext.setLanguageHints(Arrays.asList(languages));
                        annotateImageRequest.setImageContext(imageContext);

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                } catch (IOException e) {
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mDlg.dismiss();
                strcam.clear();
                for (int i = 0; i < ocrtemp.length; i++) {
                    if (result.contains(ocrtemp[i])) {
                        strcam.add(ocrtemp[i]);
                        Log.i("YSTest2", ocrtemp[i]);
                    }
                }
                camdialog();
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "Recognition results:\n";
        StringBuilder builder = new StringBuilder(message);

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        Log.i("JackTest", "total labels:" + labels.size());
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++) {
                EntityAnnotation label = labels.get(i);
                if (i == 0) {
                    builder.append("Locale: ");
                    builder.append(label.getLocale());
                }
                builder.append(label.getDescription());
                builder.append("\n");
                Log.i("YSTest : ", label.getDescription());
                //TODO: Draw rectangles later

                String ocr = label.getDescription();

                Log.i("YSTest", String.valueOf(ocrtemp.length));

                for (int j = 0; j < ocrtemp.length; j++) {
                    if (ocr.contains(ocrtemp[j]))
                        Log.i("YSTest", "Contain : " + ocrtemp[j]);
                    else
                        Log.i("YSTest", "Not Contain : " + ocrtemp[j]);
                }

                break;
            }
        } else {
            builder.append("nothing");
        }

        return builder.toString();
    }

    void initstr(String[] str) {
        for (int i = 0; i < str.length; i++)
            str[i] = "nothing";
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
                if (!result.equals("null")) {
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

    void getlist() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            String param = "";

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("http://13.124.64.178/get_listname.php");

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
                String myJSON = result;
                setlist(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    //////////////////////////JSON -> android 연동////////////////////////////////////////
    void setlist(String myJSON) {
        String list;
        final String TAG_RESULTS = "result";
        final String TAG_LIST = "list_korean";

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray jsonArray = jsonObj.getJSONArray(TAG_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                list = c.getString(TAG_LIST);
                ocrtemp[i] = list;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    ///////////////////////////head 확인/////////////////////////////////////////////////
    void checkhead() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id;
                try {
                    URL url = new URL("http://13.124.64.178/find_sharehead.php");

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
                login_head = result;
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void camdialog() {
        final F1_CameraList dialog = new F1_CameraList(this);
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
    public class mAlarm{
        private Context context;
        public mAlarm(Context context){
            this.context = context;
        }
        public void Alarm(){
            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 13,05,0);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
}