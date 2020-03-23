package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity implements Serializable {

    private static final String TAG = "Main2Activity";

    private Button continueButton;
    private ImageView imageView;
    private static TextView results;
    private static String[] stringArray;

    LinkedList<String> filterList = new LinkedList<>();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
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

    public Main2Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();

        Mat mat = new Mat();
        final String bitmapName = intent.getStringExtra("image name");

        imageView = findViewById(R.id.imageView);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Utils.bitmapToMat(bitmap, mat);
            imageView.setImageBitmap(bitmap);
        }
        catch( FileNotFoundException e){}
        results = findViewById(R.id.textView3);

        detection(mat);

        //convert to array
        Object[] objectArray = filterList.toArray();
        int length = objectArray.length;
        stringArray = new String[length];
        for(int i =0; i < length; i++) {
            stringArray[i] = (String) objectArray[i];
        }

        print_results(stringArray);

        continueButton = findViewById(R.id.button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_Screen(stringArray, bitmapName);
            }
        });
    }

    public void detection(Mat img) {
        HistData data = new HistData(img);

        if(!checkSaturation(data)) {
            filterList.add("Gingham\n" + checkGingham(data) + "\n");
            filterList.add("Nashville\n" + checkNashville(data) + "\n");
            filterList.add("Clarendon\n" + checkClarendon(data) + "\n");
            filterList.add("Perpetua\n" + checkPerpetua(data) + "\n");
            filterList.add("Crema\n" + checkCrema(data) + "\n");
            filterList.add("Rise\n" + checkRise(data) + "\n");
        }
        else filterList.add("Black and White Image Detected\nNot reversable");
    }

    private boolean checkSaturation(HistData data){
        boolean bnw = false;
        if(data.g_val_hsv[0] < 50 && data.g_val_hsv[1] < 5 && data.g_val_hsv[2] < 5 && data.g_val_hsv[3] < 5 &&
                data.g_val_hsv[4] < 5 && data.g_val_hsv[5] < 5 && data.g_val_hsv[6] < 5 && data.g_val_hsv[7] < 5 &&
                data.g_val_hsv[8] < 5 && data.g_val_hsv[9] < 5) bnw = true;
        return bnw;
    }

    private String checkPerpetua(HistData data){
        int count = 0;
        String result = "";

        if(data.in_rgb[1] > 5 && data.in_rgb[0] < 35 && data.in_rgb[1] < 35 && data.in_rgb[2] < 25) count++;
        if(data.HistDataRgb[0][0] < 5 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 300) count++;
        if(data.HistDataRgb[2][255] < 60) count++;
        if(data.g_val_rgb[0] < 200 && data.b_val_rgb[9] < 400) count++;
        if(data.in_hsv[0] > 5 && data.in_hsv[0] < 45 && data.in_hsv[1] < 25 && data.in_hsv[2] < 20) count++;
        if(data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 150) count++;
        if(data.HistDataHsv[1][255] < 300 && data.HistDataHsv[2][255] < 1) count++;
        if(data.g_val_hsv[9] < 30 && data.b_val_hsv[5] < 25 && data.b_val_hsv[6] < 10 && data.b_val_hsv[7] < 15) count++;
        if(count < 2)  result = "Extremely Unlikely";
        else if(count < 4) result = "Unlikely";
        else if(count < 6) result = "Likely";
        else if(count <= 8) result = "Extremely Likely";

        return result;
    }

    private String checkCrema(HistData data){
        int count = 0;
        String result = "";

        if(data.out_rgb[2] <= 250) count++;
        if(data.HistDataRgb[0][0] < 20 && data.HistDataRgb[1][0] < 5 && data.HistDataRgb[2][0] < 250) count++;
        if(data.HistDataRgb[0][255] < 15 && data.HistDataRgb[1][255] < 5 && data.HistDataRgb[2][255] < 1) count++;
        if(data.b_val_rgb[9] < 80) count++;
        if(data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 500 && data.HistDataHsv[2][0] < 700) count++;
        if(data.HistDataHsv[0][255] < 10 && data.HistDataHsv[1][255] < 250) count++;
        if(data.g_val_hsv[9] < 150 && data.b_val_hsv[2] < 200) count++;

        if(count < 1)  result = "Extremely Unlikely";
        else if(count < 3) result = "Unlikely";
        else if(count < 5) result = "Likely";
        else if(count <= 7) result = "Extremely Likely";

        return result;
    }

    private String checkRise(HistData data){
        int count = 0;
        String result = "";

        if(data.in_rgb[0] > 5 && data.in_rgb[1] > 5) count++;
        if(data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 15) count++;
        if(data.HistDataRgb[2][255] < 50) count++;
        if(data.g_val_rgb[0] < 140 && data.b_val_rgb[0] < 50) count++;
        if(data.in_hsv[0] > 10 && data.in_hsv[2] == 0) count++;
        if(data.HistDataHsv[0][0] < 1) count++;
        if(data.HistDataHsv[1][255] < 10 && data.HistDataHsv[2][255] < 1) count++;
        if(data.r_val_hsv[0] < 5 && data.g_val_hsv[9] < 400) count++;

        if(count < 2)  result = "Extremely Unlikely";
        else if(count < 4) result = "Unlikely";
        else if(count < 6) result = "Likely";
        else if(count <= 8) result = "Extremely Likely";

        return result;
    }

    private String checkClarendon(HistData data){
        int count = 0;
        String result = "";

        if(data.HistDataRgb[1][0] < 200) count++;
        if(data.HistDataRgb[0][255] < 600) count++;
        if(data.out_hsv[0] > 220 && data.out_hsv[1] > 200) count++;
        if(data.HistDataHsv[0][0] < 1) count++;
        if(data.b_val_hsv[5] < 100 & data.b_val_hsv[6] < 200 && data.b_val_hsv[7] < 100) count++;

        if(count < 1)  result = "Extremely Unlikely";
        else if(count < 3) result = "Unlikely";
        else if(count <= 5) result = "Likely";

        return result;
    }

    private String checkNashville(HistData data){
        int count = 0;
        String result = "";

        if(data.in_rgb[0] < 10) count++;
        if(data.out_rgb[1] < 250) count++;
        if(data.HistDataRgb[2][0] < 1) count++;
        if(data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 5) count++;
        if(data.b_val_rgb[0] < 5 && data.b_val_rgb[1] < 20 && data.b_val_rgb[9] < 5) count++;
        if(data.b_val_hsv[2] < 200) count++;

        if(count < 2)  result = "Extremely Unlikely";
        else if(count < 6) result = "Likely";
        else if(count <= 8) result = "Extremely Likely";

        return result;
    }

    private String checkGingham(HistData data){
        int count = 0;
        String result = "";

        if(data.in_rgb[0] > 20 && data.in_rgb[1] > 25 && data.in_rgb[2] > 10) count++;
        if(data.in_hsv[0] > 30) count++;
        if(data.out_hsv[2] > 125 && data.out_hsv[0] < 250 && data.out_hsv[1] < 230) count++;
        if(data.HistDataHsv[0][255] < 1 && data.HistDataHsv[1][255] < 1 && data.HistDataHsv[2][255] < 1 && data.HistDataHsv[0][0] < 1) count++;
        if(data.out_rgb[0] < 250 && data.out_rgb[1] < 230) count++;
        if(data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 1) count++;
        if(data.HistDataRgb[0][255] < 1 && data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 1) count++;
        if(data.r_val_rgb[0] < 5 && data.r_val_rgb[9] < 10 && data.g_val_rgb[0] < 5 && data.g_val_rgb[9] < 5 && data.b_val_rgb[0] < 5 && data.b_val_rgb[9] < 5) {
            result = "Extremely Likely";
            count++;
        }
        if(data.r_val_hsv[0] < 5 && data.r_val_hsv[9] < 10 && data.g_val_hsv[8] < 15 && data.g_val_hsv[9] < 5) count++;

        if(result.equals("")) {
            if (count < 2) result = "Extremely Unlikely";
            else if (count < 4) result = "Unlikely";
            else if (count < 6) result = "Likely";
            else if (count <= 8) result = "Extremely Likely";
        }

        return result;
    }

    public void next_Screen(String[] list, String name){
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("filter list", list);
        intent.putExtra("bitmap name", name);
        startActivity(intent);
    }

    public static void print_results(String[] list){
        String string = "";
        if(list.length != 0) {
            for (int i = 0; i < list.length - 1; i++) {
                string += list[i];
                string += "\n";
            }
            string += list[list.length - 1];
        }
        results.setText(string);
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