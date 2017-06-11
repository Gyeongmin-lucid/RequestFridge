package com.example.kgm13.requestfridge;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kgm13.requestfridge.F1_Fridge.customGridAdapter;
import static com.example.kgm13.requestfridge.F1_Fridge.gridArray;
import static com.example.kgm13.requestfridge.F1_Fridge.gridView;
import static com.example.kgm13.requestfridge.LoginActivity.login_id;
import static com.example.kgm13.requestfridge.MainActivity.login_head;

public class Dialog_Shareconfirm extends Dialog {

    @Nullable @Bind(R.id.shareconfirm_cancel) Button shareconfirm_cancel;
    @Nullable @Bind(R.id.shareconfirm_accept) Button shareconfirm_accept;
    @Nullable @Bind(R.id.shareconfirm_id) TextView shareconfirm_text;
    String id, result;
    public Dialog_Shareconfirm(@NonNull Context context, String par_id) {
        super(context);
        id = par_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_shareconfirm);
        ButterKnife.bind(this);

        shareconfirm_text.setText(id + "님의 공유 요청이에요!\n 수락하시면 " + id + "의 데이터로 통합됩니다.");
        shareconfirm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = "cancel";
                share_result();
                dismiss();
            }
        });
        shareconfirm_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = "accept";
                login_head = id;
                share_result();
                gridArray.clear();
                F1_Fridge f1_fridge = new F1_Fridge();
                f1_fridge.SQLgetdata();
                gridView.setAdapter(customGridAdapter);
                dismiss();
            }
        });
    }
    void share_result() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String param = "&u_id=" + login_id + "&u_result=" + result;
                try {
                    URL url = new URL("http://13.124.64.178/share_result.php");

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
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

}
