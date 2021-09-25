package com.example.wangjd7.ocrtranslate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ocrClicked(View view) {
        Intent intent = new Intent(MainActivity.this,OcrActivity.class);
        startActivity(intent);
    }

    public void loginClicked(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void speechClicked(View view) {
        Intent intent = new Intent(MainActivity.this,SpeakerActivity.class);
        startActivity(intent);
    }

    public void weatherClicked(View view) {
        Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
        startActivity(intent);
    }

    public void sightClicked(View view) {
        Intent intent = new Intent(MainActivity.this,SightActivity.class);
        startActivity(intent);
    }

}