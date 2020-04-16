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

import java.text.DecimalFormat;

import android.widget.Button;

/*
    This screen performs filter detection
 */
public class Main2Activity extends AppCompatActivity implements Serializable {

    private static final String TAG = "Main2Activity";

    private static DecimalFormat df = new DecimalFormat("0");

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
        int weighting = 1;

        if (!checkSaturation(data)) {
            double[] hist_results = checkHists(data);
            double[] filter_results = {checkPerpetua(data, hist_results[0], weighting), checkCrema(data, hist_results[1], weighting), checkGingham(data, hist_results[2], weighting), checkNashville(data, hist_results[3], weighting), checkRise(data, hist_results[4], weighting), checkClarendon(data, hist_results[5], weighting)};

            filterList.add("\nMatches:\n");
            filterList.add("Perpetua: " + df.format(filter_results[0]*100) + "%");
            filterList.add("Crema: " + df.format(filter_results[1]*100) + "%");
            filterList.add("Gingham: " + df.format(filter_results[2]*100) + "%");
            filterList.add("Nashville: " + df.format(filter_results[3]*100) + "%");
            filterList.add("Rise: " + df.format(filter_results[4]*100) + "%");
            filterList.add("Clarendon: " + df.format(filter_results[5]*100) + "%");
        } else filterList.add("Black and White Image Detected\nNot reversible");
    }

    //perform histogram correlation
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

    private double checkPerpetua(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 13;
        if(data.in_rgb[1] > 5) count++;
        if(Math.round(data.HistDataRgb[2][255]) < 100) count++;
        if(Math.round(data.HistDataRgb[0][0]) < 5) count++;
        if(Math.round(data.HistDataRgb[1][0]) < 5) count++;
        if(Math.round(data.HistDataRgb[2][0]) < 300) count++;
        if(data.in_hsv[0] > 5 && data.in_hsv[1] < 10) count++;
        if(Math.round(data.HistDataHsv[0][0]) == 0) count++;
        if(Math.round(data.HistDataHsv[1][0]) < 150) count++;
        if(data.g_val_hsv[8] < 50) count++;
        if(data.g_val_hsv[9] < 40) count++;
        if(data.b_val_hsv[7] < 40) count++;
        if(data.b_val_hsv[6] < 45) count++;
        if(data.b_val_hsv[5] < 35) count++;
        return count/total;
    }

    private double checkCrema(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 9;
        if(data.g_val_hsv[9] <= 30) count++;
        if(data.g_val_hsv[8] <= 30) count++;
        if(data.g_val_hsv[7] < 100) count++;
        if(Math.round(data.HistDataHsv[0][0]) < 5) count++;
        if(Math.round(data.HistDataHsv[1][0]) < 400) count++;
        if(Math.round(data.HistDataHsv[0][255]) < 100) count++;
        if(Math.round(data.HistDataHsv[1][255]) < 100) count++;
        if(Math.round(data.HistDataRgb[1][255]) < 50) count++;
        if(Math.round(data.HistDataRgb[2][255]) <= 1) count++;
        return count/total;
    }

    private double checkGingham(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 12;
        if(data.r_val_rgb[0] <= 5) count++;
        if(data.r_val_rgb[9] <= 5) count++;
        if(data.g_val_rgb[0] <= 5) count++;
        if(data.g_val_rgb[9] <= 5) count++;
        if(data.b_val_rgb[0] <= 5) count++;
        if(data.b_val_rgb[9] <= 35) count++;
        if(data.out_rgb[0] <= 235) count++;
        if(data.out_rgb[2] <= 235) count++;
        if(data.out_rgb[1] <= 235) count++;
        if(data.in_rgb[0] >= 15) count++;
        if(data.in_rgb[1] >= 15) count++;
        if(data.in_rgb[2] >= 15) count++;
        return count/total;
    }

    private double checkNashville(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 3;
        if(data.b_val_rgb[0] < 10) count++;
        if(data.b_val_rgb[1] < 30) count++;
        if(data.b_val_rgb[9] < 10) count++;
        return count/total;
    }

    private double checkRise(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 9;
        if(Math.round(data.HistDataHsv[0][0]) < 5) count++;
        if(Math.round(data.HistDataHsv[1][0]) < 200) count++;
        if(Math.round(data.HistDataHsv[1][255]) < 50) count++;
        if(data.r_val_hsv[0] < 10) count++;
        if(data.in_hsv[0] > 5) count++;
        if(data.g_val_hsv[8] < 40) count++;
        if(data.g_val_hsv[9] <= 5) count++;
        if(Math.round(data.HistDataRgb[0][0]) <= 0) count++;
        if(Math.round(data.HistDataRgb[2][0]) < 50) count++;
        return count/total;
    }

    private double checkClarendon(HistData data, double hist, int weighting) {
        double count = hist*weighting;
        double total = 6*weighting + 6;
        if(Math.round(data.HistDataRgb[0][255]) < 120) count++;
        if(Math.round(data.HistDataRgb[1][0]) < 100) count++;
        if(Math.round(data.HistDataHsv[0][0]) < 100) count++;
        if(data.b_val_hsv[5] < 50) count++;
        if(data.b_val_hsv[6] < 40) count++;
        if(data.b_val_hsv[7] < 25) count++;
        return count/total;
    }

    //move to next screen
    public void next_Screen(String[] list, String name) {
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("filter list", list);
        intent.putExtra("bitmap name", name);
        startActivity(intent);
    }

    //print detection results to screen
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

    //load image
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