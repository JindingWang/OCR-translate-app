package com.example.wangjd7.ocrtranslate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import android.widget.EditText;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Toast;

public class TranslateActivity extends AppCompatActivity {

    public String ocrenglish;
    public final String DATABASE_PATH = "/data/data/com.example.wangjd7.ocrtranslate/databases/";
    public final String APIUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=travelapp&key=1268935879&type=data&doctype=json&version=1.1&q=";
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        Intent intent = getIntent();
        ocrenglish = intent.getStringExtra("english");
        EditText english = (EditText) findViewById(R.id.text1);
        english.setText(ocrenglish);
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

    public void APIClicked(View view) {
        EditText english = (EditText) findViewById(R.id.text1);
        TextView chinese = (TextView) findViewById(R.id.text2);
        String word = english.getText().toString().trim();
        String meaning = "";
        if (!isNetworkAvailable(TranslateActivity.this)) {
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
            english.setText(word);
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == '\n') {
                    word = word.subSequence(0, i) + "%0A" + word.subSequence(i + 1, word.length());
                } else if (word.charAt(i) == ' ') {
                    word = word.subSequence(0, i) + "%20" + word.subSequence(i + 1, word.length());
                } else ;
            }
            //chinese.setText(word);
            HttpURLConnection connection=null;
            try {
                URL url = new URL(APIUrl + word);
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
                chinese.setText(meaning);
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
    public void translateClicked(View view) {
        EditText english = (EditText) findViewById(R.id.text1);
        TextView chinese = (TextView) findViewById(R.id.text2);
        String word = english.getText().toString().trim();
        String meaning = "";
        String temp = "";
        try {
            if (!(new File(DATABASE_PATH).exists())) {
                File f = new File(DATABASE_PATH);
                if (!f.exists()) {
                    f.mkdir();
                }
                InputStream is = getBaseContext().getAssets().open("Dict.db"); //欲导入的数据库
                FileOutputStream os = new FileOutputStream(DATABASE_PATH+"Dict.db");
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        database = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH+"Dict.db",null);

        for (int i=0; i<word.length(); i++) {
            if (word.charAt(i)=='\n') {
                if (temp.isEmpty()) {
                    meaning += '\n';
                } else {
                    Cursor cursor = database.rawQuery("select * from dict where english=?",new String[]{temp});
                    if (cursor.moveToFirst()) {
                        meaning += cursor.getString(2);
                    } else {
                        meaning += temp;
                    }
                    cursor.close();
                    meaning += '\n';
                    temp = "";
                }
            } else if (word.charAt(i)==' ') {
                Cursor cursor = database.rawQuery("select * from dict where english=?",new String[]{temp});
                if (cursor.moveToFirst()) {
                    meaning += cursor.getString(2);
                } else {
                    meaning += temp;
                }
                cursor.close();
                temp = "";
            } else {
                temp += word.charAt(i);
            }
        }
        if (!temp.isEmpty()) {
            Cursor cursor = database.rawQuery("select * from dict where english=?",new String[]{temp});
            if (cursor.moveToFirst()) {
                meaning += cursor.getString(2);
            } else {
                meaning += temp;
            }
            cursor.close();
            temp = "";
        }
        chinese.setText(meaning);
        database.close();
    }
}
