package com.example.wangjd7.ocrtranslate;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class WeatherActivity extends AppCompatActivity {

    public final String DATABASE_PATH = "/data/data/com.example.wangjd7.ocrtranslate/databases/";
    private SQLiteDatabase database;
    public String locCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getLocation();
    }

    /** 基站信息结构体 */
    public class SCell{
        public int MCC;
        public int MNC;
        public int LAC;
        public int CID;
    }
    /** 城市信息结构体 */
    public class Location{
        public String CITY = "";
        public String REGION = "";
        public String COUNTRY = "";
    }

    public void getLocation() {
        TextView text = (TextView) findViewById(R.id.TV1);
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("请稍等");
        mProgressDialog.setMessage("定位中...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        String loc = "定位结果:\n";
        try {
            SCell cell = getCellInfo(); //获取基站数据
            Location location = search(cell);
            loc = loc + "国家：" + location.COUNTRY + '\n';
            loc = loc + "省份：" + location.REGION + '\n';
            loc = loc + "城市：" + location.CITY;
            text.setText(loc);
            locCity = location.CITY;
            mProgressDialog.dismiss(); //关闭缓冲对话框
        } catch (Exception e) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "手机无信号或未插入SIM卡！", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonweatherClicked(View view) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("查询天气中...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        try {
            if (!locCity.isEmpty()) {
                getweather(locCity);
                mProgressDialog.dismiss(); //关闭缓冲对话框
            } else {
                Toast.makeText(this, "未定位成功，不能查询！", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss(); //关闭缓冲对话框
            }
        } catch (Exception e) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "出现未知错误，请重试！", Toast.LENGTH_SHORT).show();
        }
    }

    public void getweather(String city) throws IOException {
        if (!isNetworkAvailable(WeatherActivity.this)) {
            Toast.makeText(this, "当前网络不可用！", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView text2 = (TextView) findViewById(R.id.TV3);
        String weather = "";
        String APIUrl = "https://free-api.heweather.com/v5/weather?city=";
        city = URLEncoder.encode(city, "UTF-8");
        URL url = new URL(APIUrl + city + "&key=60ffeed9afe64703893b7af00004109e");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
        String readLine = "";
        if(HttpURLConnection.HTTP_OK==resultCode) {
            StringBuilder sb = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            readLine = sb.toString();
        }
        try {
            JSONObject jsonObject = new JSONObject(readLine);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            JSONObject allJsonObject = jsonArray.getJSONObject(0);
            String status = allJsonObject.getString("status");
            if (status.equals("ok")) {
                JSONObject aqi = allJsonObject.getJSONObject("aqi");
                JSONObject citya = aqi.getJSONObject("city");
                weather = weather + "空气质量指数：" + citya.getString("aqi") + ' '+citya.getString("qlty") + '\n';
                JSONObject now = allJsonObject.getJSONObject("now");
                weather = weather + "温度："+now.getString("tmp")+"度\n";
                JSONObject now_cond = now.getJSONObject("cond");
                weather = weather + "天气："+now_cond.getString("txt")+'\n';
                JSONObject now_wind = now.getJSONObject("wind");
                weather = weather + now_wind.getString("dir")+"，风力"+now_wind.getString("sc")+"级\n";
                JSONObject sugg = allJsonObject.getJSONObject("suggestion");
                JSONObject uv = sugg.getJSONObject("uv");
                weather = weather +"建议："+ uv.getString("txt");
                text2.setText(weather);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public Location search(SCell cell) {
        Location location = new Location();
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
        String mcc = String.valueOf(cell.MCC);
        String mnc = String.valueOf(cell.MNC);
        String lac = String.valueOf(cell.LAC);

        database = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH+"Dict.db",null);

        Cursor cursor = database.rawQuery("select * from jizhan where MCC=? and MNC=? and LAC =?",new String[]{mcc, mnc, lac});
        if (cursor.moveToFirst()) {
            location.CITY = cursor.getString(6);
            location.REGION = cursor.getString(5);
            location.COUNTRY = cursor.getString(7);
        } else {
            location.CITY = "未找到";
            location.REGION = "未找到";
            location.COUNTRY = "未找到";
        }
        cursor.close();
        database.close();
        return location;
    }

    public SCell getCellInfo() throws Exception {
        SCell cell = new SCell();
        TelephonyManager mTelNet = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //获取基站信息
        GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
        if (location == null) {
            throw new Exception("获取基站信息失败");
        }
        String operator = mTelNet.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        int cid = location.getCid();
        int lac = location.getLac();
        cell.MCC = mcc;
        cell.MNC = mnc;
        cell.LAC = lac;
        cell.CID = cid;
        return cell;
    }

}
