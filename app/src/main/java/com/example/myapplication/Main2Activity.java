package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaScannerConnection;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.LinkedList;

import android.widget.Button;

public class Main2Activity extends AppCompatActivity implements Serializable {

    private static final String TAG = "Main2Activity";

    private Button continueButton;
    Bitmap bitmap;
    private ImageView imageView;
    private static TextView results;
    private static String[] stringArray;

    String[] folder = {"Perpetua/", "Crema/", "Gingham/", "Nashville/", "Rise/", "Clarendon/"};
    String[] folderName = {"perp", "crem", "ging", "nash", "rise", "clar"};

    LinkedList<String> filterList = new LinkedList<>();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
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
            bitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Utils.bitmapToMat(bitmap, mat);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
        }
        results = findViewById(R.id.textView3);

        detection(mat);

        //convert to array
        Object[] objectArray = filterList.toArray();
        int length = objectArray.length;
        stringArray = new String[length];
        for (int i = 0; i < length; i++) {
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

        if (!checkSaturation(data)) {
            double[] hist_results = checkHists(data);
            double[] filter_results = {checkPerpetua(data, hist_results[0]), checkCrema(data, hist_results[1]), checkGingham(data, hist_results[2]), checkNashville(data, hist_results[3]), checkRise(data, hist_results[4]), checkClarendon(data, hist_results[5])};
            double[] total_results = {0,0,0,0,0,0};
            for(int j = 0; j < 6; j++) total_results[j] += filter_results[j];

            filterList.add("Perpetua: " + total_results[0] + "%\n");
            filterList.add("Crema: " + total_results[1] + "%\n");
            filterList.add("Gingham: " + total_results[2] + "%\n");
            filterList.add("Nashville: " + total_results[3] + "%\n");
            filterList.add("Rise: " + total_results[4] + "%\n");
            filterList.add("Clarendon: " + total_results[5] + "%\n");
        } else filterList.add("Black and White Image Detected\nNot reversible");
    }

    private double[] checkHists(HistData data) {
        double[] count = {0, 0, 0, 0, 0, 0, 0};
        double largest_r = Double.MIN_VALUE;
        double largest_g = Double.MIN_VALUE;
        double largest_b = Double.MIN_VALUE;
        double largest_h = Double.MIN_VALUE;
        double largest_s = Double.MIN_VALUE;
        double largest_v = Double.MIN_VALUE;

        int r_name = 0;
        int g_name = 0;
        int b_name = 0;
        int h_name = 0;
        int s_name = 0;
        int v_name = 0;

        for (int j = 0; j < folder.length; j++) {
            double red_total = 0;
            double green_total = 0;
            double blue_total = 0;
            double hue_total = 0;
            double sat_total = 0;
            double val_total = 0;

            for (int i = 1; i <= 100; i++) {
                Bitmap bmp = getImage("/Images/" + folder[j] + folderName[j] + " (" + i + ")");
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData comparedData = new HistData(mat);
                double hue = Imgproc.compareHist( data.r_hist_hsv, comparedData.r_hist_hsv, 0 );
                double sat = Imgproc.compareHist( data.g_hist_hsv, comparedData.g_hist_hsv, 0 );
                double val = Imgproc.compareHist( data.b_hist_hsv, comparedData.b_hist_hsv, 0 );
                double red = Imgproc.compareHist( data.r_hist_rgb, comparedData.r_hist_rgb, 0 );
                double green = Imgproc.compareHist( data.g_hist_rgb, comparedData.g_hist_rgb, 0 );
                double blue = Imgproc.compareHist( data.b_hist_rgb, comparedData.b_hist_rgb, 0 );
                red_total += red; green_total += green; blue_total += blue;
                hue_total += hue; sat_total += sat; val_total += val;
            }

            red_total /= 100; green_total /= 100; blue_total /= 100; hue_total /= 100; sat_total /= 100; val_total /= 100;
            if(largest_r < red_total) {largest_r = red_total; r_name=j; }
            if(largest_g < green_total) {largest_g = green_total; g_name=j; }
            if(largest_b < blue_total) {largest_b = blue_total; b_name=j; }
            if(largest_h < hue_total) {largest_h = hue_total; h_name=j; }
            if(largest_s < sat_total) {largest_s = sat_total; s_name=j; }
            if(largest_v < val_total) {largest_v = val_total; v_name=j; }
        }
        count[r_name]++;
        count[g_name]++;
        count[b_name]++;
        count[h_name]++;
        count[s_name]++;
        count[v_name]++;
        return count;
    }

    private boolean checkSaturation(HistData data) {
        boolean bnw = false;
        if (data.g_val_hsv[0] < 50 && data.g_val_hsv[1] < 5 && data.g_val_hsv[2] < 5 && data.g_val_hsv[3] < 5 &&
                data.g_val_hsv[4] < 5 && data.g_val_hsv[5] < 5 && data.g_val_hsv[6] < 5 && data.g_val_hsv[7] < 5 &&
                data.g_val_hsv[8] < 5 && data.g_val_hsv[9] < 5) bnw = true;
        return bnw;
    }

    private double checkPerpetua(HistData data, double hist) {
        double count = hist;
        double total = 6 + 8;
        if (data.in_rgb[1] > 5 && data.in_rgb[0] < 35 && data.in_rgb[1] < 35 && data.in_rgb[2] < 25) count++;
        if (data.HistDataRgb[0][0] < 5 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 300) count++;
        if (data.HistDataRgb[2][255] < 60) count++;
        if (data.g_val_rgb[0] < 200 && data.b_val_rgb[9] < 400) count++;
        if (data.in_hsv[0] > 5 && data.in_hsv[0] < 45 && data.in_hsv[1] < 25 && data.in_hsv[2] < 20) count++;
        if (data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 150) count++;
        if (data.HistDataHsv[1][255] < 300 && data.HistDataHsv[2][255] < 1) count++;
        if (data.g_val_hsv[9] < 30 && data.b_val_hsv[5] < 25 && data.b_val_hsv[6] < 10 && data.b_val_hsv[7] < 15) count++;
        return count/total;
    }

    private double checkCrema(HistData data, double hist) {
        double count = hist;
        double total = 6 + 8;
        if (data.out_rgb[2] <= 250) count++;
        if (data.HistDataRgb[0][0] < 20 && data.HistDataRgb[1][0] < 5 && data.HistDataRgb[2][0] < 250) count++;
        if (data.HistDataRgb[0][255] < 15 && data.HistDataRgb[1][255] < 5 && data.HistDataRgb[2][255] < 1) count++;
        if (data.b_val_rgb[9] < 80) count++;
        if (data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 500 && data.HistDataHsv[2][0] < 700) count++;
        if (data.HistDataHsv[0][255] < 10 && data.HistDataHsv[1][255] < 250) count++;
        if (data.g_val_hsv[9] < 150 && data.b_val_hsv[2] < 200) count++;
        return count/total;
    }

    private double checkGingham(HistData data, double hist) {
        double count = hist;
        double total = 6 + 8;
        if (data.in_rgb[0] > 20 && data.in_rgb[1] > 25 && data.in_rgb[2] > 10) count++;
        if (data.in_hsv[0] > 30) count++;
        if (data.out_hsv[2] > 125 && data.out_hsv[0] < 250 && data.out_hsv[1] < 230) count++;
        if (data.HistDataHsv[0][255] < 1 && data.HistDataHsv[1][255] < 1 && data.HistDataHsv[2][255] < 1 && data.HistDataHsv[0][0] < 1) count++;
        if (data.out_rgb[0] < 250 && data.out_rgb[1] < 230) count++;
        if (data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 1) count++;
        if (data.HistDataRgb[0][255] < 1 && data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 1) count++;
        if (data.r_val_rgb[0] < 5 && data.r_val_rgb[9] < 10 && data.g_val_rgb[0] < 5 && data.g_val_rgb[9] < 5 && data.b_val_rgb[0] < 5 && data.b_val_rgb[9] < 5)  count++;
        return count/total;
    }

    private double checkNashville(HistData data, double hist) {
        double count = hist;
        double total = 6 + 6;
        if (data.in_rgb[0] < 10) count++;
        if (data.out_rgb[1] < 250) count++;
        if (data.HistDataRgb[2][0] < 1) count++;
        if (data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 5) count++;
        if (data.b_val_rgb[0] < 5 && data.b_val_rgb[1] < 20 && data.b_val_rgb[9] < 5) count++;
        if (data.b_val_hsv[2] < 200) count++;
        return count/total;
    }

    private double checkRise(HistData data, double hist) {
        double count = hist;
        double total = 6 + 8;
        if (data.in_rgb[0] > 5 && data.in_rgb[1] > 5) count++;
        if (data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 15) count++;
        if (data.HistDataRgb[2][255] < 50) count++;
        if (data.g_val_rgb[0] < 140 && data.b_val_rgb[0] < 50) count++;
        if (data.in_hsv[0] > 10 && data.in_hsv[2] == 0) count++;
        if (data.HistDataHsv[0][0] < 1) count++;
        if (data.HistDataHsv[1][255] < 10 && data.HistDataHsv[2][255] < 1) count++;
        if (data.r_val_hsv[0] < 5 && data.g_val_hsv[9] < 400) count++;
        return count/total;
    }

    private double checkClarendon(HistData data, double hist) {
        double count = hist;
        double total = 6 + 5;
        if (data.HistDataRgb[1][0] < 200) count++;
        if (data.HistDataRgb[0][255] < 600) count++;
        if (data.out_hsv[0] > 220 && data.out_hsv[1] > 200) count++;
        if (data.HistDataHsv[0][0] < 1) count++;
        if (data.b_val_hsv[5] < 100 & data.b_val_hsv[6] < 200 && data.b_val_hsv[7] < 100) count++;
        return count/total;
    }

    public void next_Screen(String[] list, String name) {
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("filter list", list);
        intent.putExtra("bitmap name", name);
        startActivity(intent);
    }

    public static void print_results(String[] list) {
        String string = "";
        if (list.length != 0) {
            for (int i = 0; i < list.length - 1; i++) {
                string += list[i];
                string += "\n";
            }
            string += list[list.length - 1];
        }
        results.setText(string);
    }

    private Bitmap getImage(String name) {
        String photoPath = this.getExternalFilesDir(null) + name + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        if (bmp == null) {
            photoPath = this.getExternalFilesDir(null) + name + ".jpeg";
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(photoPath, options);
        }
        return bmp;
    }

    @Override
    public void onResume() {
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