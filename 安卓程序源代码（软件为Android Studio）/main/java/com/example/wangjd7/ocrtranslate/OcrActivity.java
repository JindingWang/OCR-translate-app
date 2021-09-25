package com.example.wangjd7.ocrtranslate;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.io.File;

public class OcrActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FROM_ALBUM = 1;
    private static final int REQUEST_CODE_FROM_PHOTO = 2;
    private String mFilePath;
    private Uri photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        mFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + "temp.jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FROM_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (photoUri == null) {
                        return;
                    } else {
                        jumpPictureActivity(photoUri);
                    }
                }
                break;
            case REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    photoUri = data.getData();
                    if (photoUri == null) {
                        return;
                    } else {
                        jumpPictureActivity(photoUri);
                    }
                }
                break;
        }
    }

    protected void jumpPictureActivity(Uri imageUri) {
        Intent intent = new Intent(OcrActivity.this,PictureActivity.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
        //MainActivity.this.finish();
    }

    public void photoClicked(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = Uri.fromFile(new File(mFilePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_CODE_FROM_PHOTO);
    }

    public void albumClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }

}