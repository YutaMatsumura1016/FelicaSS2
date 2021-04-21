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

    SoundPool soundPool;
    int soundpi;
    NfcAdapter nfcAdapter;
    static String gate;




    //スーパークラス両方分
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //nfcAdapter初期化
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //オーディオ
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





        class MyReaderCallback implements NfcAdapter.ReaderCallback {

            @RequiresApi(api = Build.VERSION_CODES.N)
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


                //音を鳴らしてSSに送る
                final Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.activity_main);
                        WebView myWebView = (WebView) findViewById(R.id.webView1);
                        //アプリ内ブラウザを使用
                        myWebView.setWebViewClient(new WebViewClient());
                        String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString + "&&gate=" + gate;
                        myWebView.loadUrl(sentURL);
                        Log.d("huga", sentURL);

                        soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);
                    }
                });

            }

        }


        Button buttonWaseda = findViewById(R.id.buttonWaseda);
        Button buttonToyama = findViewById(R.id.buttonToyama);
        TextView nowReading = findViewById(R.id.nowReading);


        //javaがxmlより優先される性質を利用
        buttonWaseda.setText("早稲田");
        buttonToyama.setText("戸 山");
        buttonWaseda.setVisibility(View.VISIBLE);
        buttonToyama.setVisibility(View.VISIBLE);





        buttonWaseda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = new MainActivity();
                String gate1 = "早稲田";
                ma.gate = gate1;

                nowReading.setText(gate + "キャンパスで読み取りを開始しました。");


                //Felica読み取りスタート
                nfcAdapter.enableReaderMode(MainActivity.this, new MyReaderCallback(), NfcAdapter.FLAG_READER_NFC_F, null);
            }
        });




        buttonToyama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity ma = new MainActivity();
                String gate1 = "戸山";
                 ma.gate = gate1;

                nowReading.setText(gate + "キャンパスで読み取りを開始しました。");

                //Felica読み取りスタート
                nfcAdapter.enableReaderMode(MainActivity.this, new MyReaderCallback(), NfcAdapter.FLAG_READER_NFC_F, null);
            }
        });






    }//スーパークラス終わり
}




