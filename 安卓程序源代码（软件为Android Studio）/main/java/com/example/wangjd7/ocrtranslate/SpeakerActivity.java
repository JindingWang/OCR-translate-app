package com.example.wangjd7.ocrtranslate;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.String;
import java.net.URLEncoder;
import java.util.Locale;

public class SpeakerActivity extends AppCompatActivity {

    private TextToSpeech tts;
    public final String ENAPIUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=travelapp&key=1268935879&type=data&doctype=json&version=1.1&q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Welcome to use voice assistant!");
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });
    }

    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return false;
        }else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if(networkInfo != null && networkInfo.length > 0) {
                for(int i = 0; i < networkInfo.length; i++) {
                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void cnenClicked(View view) {
        EditText chinese = (EditText) findViewById(R.id.edit11);
        EditText english = (EditText) findViewById(R.id.edit12);
        String word = chinese.getText().toString().trim();
        String meaning = "";
        if (!isNetworkAvailable(SpeakerActivity.this)) {
            Toast.makeText(this, "当前网络不可用！", Toast.LENGTH_SHORT).show();
        } else if (word.isEmpty()) {
            Toast.makeText(this, "字符串不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < word.length()-1; i++) {
                if (word.charAt(i)=='\n' && word.charAt(i+1)=='\n') {
                    word = word.subSequence(0, i) + "" + word.subSequence(i + 1, word.length());
                    i--;
                }
            }
            if (word.charAt(word.length()-1)=='\n') {
                word = word.substring(0, word.length()-2);
            }
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == '\n') {
                    word = word.subSequence(0, i) + "%0A" + word.subSequence(i + 1, word.length());
                } else if (word.charAt(i) == ' ') {
                    word = word.subSequence(0, i) + "" + word.subSequence(i + 1, word.length());
                } else ;
            }

            HttpURLConnection connection=null;
            try {
                String str = URLEncoder.encode(word, "UTF-8");
                URL url = new URL(ENAPIUrl + str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(6000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestMethod("POST");
                connection.connect();

                int resultCode=connection.getResponseCode();
                if(HttpURLConnection.HTTP_OK==resultCode) {
                    StringBuilder sb = new StringBuilder();
                    String readLine;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    meaning = sb.toString();
                } else {
                }
                meaning = meaning.substring(16,meaning.length()-1);
                for (int i=0; i<meaning.length(); i++) {
                    if (meaning.charAt(i)==']') {
                        meaning = meaning.substring(0,i-1);
                        break;
                    }
                }
                int fuhaocount = 0;
                for (int i=0; i<meaning.length(); i++) {
                    if (meaning.charAt(i)=='"' && fuhaocount%2==1) {
                        meaning = meaning.subSequence(0,i) + "\n" + meaning.subSequence(i+2,meaning.length());
                        fuhaocount++;
                    } else if (meaning.charAt(i)=='"' && fuhaocount%2==0) {
                        meaning = meaning.subSequence(0,i) + "" + meaning.subSequence(i+1,meaning.length());
                        fuhaocount++;
                    }
                }
                english.setText(meaning);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        }
    }

    public void playClicked(View view) {
        EditText english = (EditText) findViewById(R.id.edit12);
        String speaker = english.getText().toString().trim();
        if (speaker.isEmpty()){
            Toast.makeText(this, "英文不能为空", Toast.LENGTH_SHORT).show();
        } else {
            speak(speaker);
        }
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
