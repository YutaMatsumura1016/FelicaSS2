package com.example.felicass2;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    //SS
    private TextView mOutputText;
    private Button mCallApiButton;
    private static final String BUTTON_TEXT = "読み取り開始";
    private static final String BUTTON_TEXT2 = "読み取り中";


    //Felica
    SoundPool soundPool;
    int soundpi;
    NfcAdapter nfcAdapter;
    static int i = 1;


    //スーパークラス両方分
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Felicaシステムここから
        //オーディオについて
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        soundpi = soundPool.load(this, R.raw.pi_cut, 1);
        //オーディオについてここまで

        //nfcAdapter初期化
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        class MyReaderCallback implements NfcAdapter.ReaderCallback {

            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
            public void onTagDiscovered(Tag tag) {
                //idmを取得
                byte[] idm = tag.getId();
                Log.d("piyo", "タグを読み取れたよ");

                //16進数に変換
                StringBuilder sb = new StringBuilder();
                for (byte d : idm) {
                    sb.append(String.format("%02X", d));
                }
                String idmString = sb.toString();
                Log.d("hoge", idmString);
                MyReaderCallback mr = new MyReaderCallback();


                //音を鳴らしてSSに送る
                final Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.activity_main);
                        WebView myWebView = (WebView) findViewById(R.id.webView1);
                        //標準ブラウザを起動させない
                        myWebView.setWebViewClient(new WebViewClient());
                        String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString;
                        myWebView.loadUrl(sentURL);
                        Log.d("huga", sentURL);

                        soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);
                    }
                });

            }

        }


        //------------------元SSAPIのスーパークラス---------------------
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                mCallApiButton.setEnabled(true);
                //Felica読み取りスタート
                nfcAdapter.enableReaderMode(MainActivity.this, new MyReaderCallback(), NfcAdapter.FLAG_READER_NFC_F, null);
                mCallApiButton.setText(BUTTON_TEXT2);

            }
        });
        activityLayout.addView(mCallApiButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "「" + BUTTON_TEXT + "」ボタンを押して読み取りを始める");
        activityLayout.addView(mOutputText);
        setContentView(activityLayout);


    }//スーパークラス終わり
}




