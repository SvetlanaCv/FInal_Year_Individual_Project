package com.example.myapplication;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.ImageDecoder;
import android.net.Uri;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/*
    Starting screen
 */
public class MainActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "MainActivity";

    private Button selectPhoto, analysis, test, histTest, compare;

    private static final int PICK_IMAGE = 1;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        selectPhoto = findViewById(R.id.button);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(PICK_IMAGE);
            }
        });

        /*
        //these buttons lead to testing screens not shown in the final product

        analysis = findViewById(R.id.analysis);
        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analysisScreen();
            }
        });

        test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testScreen();
            }
        });

        compare = findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comparisonScreen();
            }
        });
        histTest = findViewById(R.id.histTest);
        histTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                histTestScreen();
            }
        });
         */
    }

    /*
        Lead to testing screens
     */
    private void comparisonScreen(){
        Intent i = new Intent(this, ComparisonScreen.class);
        startActivity(i);
    }

    private void testScreen(){
        Intent i = new Intent(this, TestScreen.class);
        startActivity(i);
    }

    private void histTestScreen(){
        Intent i = new Intent(this, HistTestScreen.class);
        startActivity(i);
    }

    private void analysisScreen(){
        Intent i = new Intent(this, AnalysisScreen.class);
        startActivity(i);
    }

    //open image gallery for selection
    private void openGallery(int num) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, num);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri imageUri = data.getData();
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);

                    Intent intent = new Intent(this, Main2Activity.class);

                    String filename = "image";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    FileOutputStream fo = openFileOutput(filename, Context.MODE_PRIVATE);
                    fo.write(bytes.toByteArray());
                    // remember close file output
                    fo.close();

                    intent.putExtra("image name", filename);
                    startActivity(intent);
                } catch (IOException e) {}
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}