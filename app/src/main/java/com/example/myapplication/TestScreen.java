package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.lang.StringBuilder;
import android.text.method.ScrollingMovementMethod;

/*
    Screen to check for TP,FP,FN,FP
 */
public class TestScreen extends AppCompatActivity {

    static final String[] photoTypes = {"Gingham/ging", "Nashville/nash", "Clarendon/clar", "Rise/rise", "Crema/crem", "Perpetua/perp", "Plain/un"};

    StringBuilder sb;
    TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        results = findViewById(R.id.results);
        results.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sb = new StringBuilder();
        int TP, TN, FP, FN;
        TP = 0; TN = 0; FP = 0; FN = 0;

        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkPlain(data)) {
                    if(photoTypes[t].equals("Plain/un")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Plain/un")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Un" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkClarendon(data)) {
                    if(photoTypes[t].equals("Clarendon/clar")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Clarendon/clar")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Clar" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkNashville(data)) {
                    if(photoTypes[t].equals("Nashville/nash")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Nashville/nash")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Nash" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkGingham(data)) {
                    if(photoTypes[t].equals("Gingham/ging")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Gingham/ging")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Ging" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkCrema(data)) {
                    if(photoTypes[t].equals("Crema/crem")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Crema/crem")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Crem" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkRise(data)) {
                    if(photoTypes[t].equals("Rise/rise")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Rise/rise")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Rise" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");

        TP = 0; TN = 0; FP = 0; FN = 0;
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkPerpetua(data)) {
                    if(photoTypes[t].equals("Perpetua/perp")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("Perpetua/perp")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        sb.append("Perp" + "\nTP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN + "\n\n");


        results.setText(sb.toString());
    }

    private boolean checkPlain(HistData data){
        return !checkGingham(data) && !checkPerpetua(data) && !checkCrema(data) && !checkRise(data) && !checkClarendon(data) && !checkNashville(data);
    }

    private boolean checkPerpetua(HistData data){
        /*
        boolean rgb_popIn = data.in_rgb[1] > 5 && data.in_rgb[2] < 25;
        boolean rgb_start = data.HistDataRgb[0][0] < 600 && data.HistDataRgb[1][0] < 1;
        boolean rgb_end = data.HistDataRgb[2][255] < 600;
        boolean rgb_val = data.g_val_rgb[0] < 200 && data.b_val_rgb[9] < 400;
        boolean hsv_popIn = data.in_hsv[0] > 5 && data.in_hsv[0] < 65 && data.in_hsv[1] < 25 && data.in_hsv[2] < 25;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 200;
        boolean hsv_vals = data.r_val_hsv[0] < 150 && data.g_val_hsv[9] < 60 && data.b_val_hsv[5] < 25 && data.b_val_hsv[6] < 50 && data.b_val_hsv[7] < 100;
         */
        return data.in_rgb[1] > 5 && Math.round(data.HistDataRgb[2][255]) < 100 && Math.round(data.HistDataRgb[0][0]) < 5 && Math.round(data.HistDataRgb[1][0]) < 5 && Math.round(data.HistDataRgb[2][0]) < 300
                && data.in_hsv[0] > 5 && data.in_hsv[1] < 10 && Math.round(data.HistDataHsv[0][0]) == 0 &&  Math.round(data.HistDataHsv[1][0]) < 150
                && data.g_val_hsv[8] < 50 && data.g_val_hsv[9] < 40 && data.b_val_hsv[7] < 40 && data.b_val_hsv[6] < 45 && data.b_val_hsv[5] < 35;
    }

    private boolean checkCrema(HistData data){
        /*
        boolean rgb_start = data.HistDataRgb[0][0] < 500 && data.HistDataRgb[1][0] < 5;
        boolean rgb_end = data.HistDataRgb[0][255] < 700 && data.HistDataRgb[1][255] < 10 && data.HistDataRgb[2][255] < 10;
        boolean rgb_val = data.b_val_rgb[9] < 150;
        boolean hsv_in = data.in_hsv[0] > 4 && data.in_hsv[0] < 70 && data.in_hsv[1] < 20 && data.in_hsv[2] < 1;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 500;
        boolean hsv_end = data.HistDataHsv[0][255] < 700;
        boolean hsv_val = data.r_val_hsv[0] < 200 & data.g_val_hsv[9] < 150;
         */
        return data.g_val_hsv[9] <= 30 && data.g_val_hsv[8] <= 30 && data.g_val_hsv[7] < 100 && Math.round(data.HistDataHsv[0][0]) < 5 && Math.round(data.HistDataHsv[1][0]) < 400 && Math.round(data.HistDataHsv[0][255]) < 100 && Math.round(data.HistDataHsv[1][255]) < 100
                && Math.round(data.HistDataRgb[1][255]) < 50 && Math.round(data.HistDataRgb[2][255]) <= 1;
    }

    private boolean checkRise(HistData data){
        /*
        boolean rgb_popIn = data.in_rgb[1] > 5 && data.in_rgb[2] < 50;
        boolean rgb_start = data.HistDataRgb[0][0] < 20 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 400;
        boolean rgb_end = data.HistDataRgb[2][255] < 200;
        boolean rgb_val = data.r_val_rgb[0] < 200 && data.g_val_rgb[0] < 150 && data.b_val_rgb[0] < 150 && data.b_val_rgb[9] < 300;
        boolean hsv_in = data.in_hsv[0] > 10 && data.in_hsv[1] < 50 && data.in_hsv[2] < 1;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 500;
        boolean hsv_end = data.HistDataHsv[1][255] < 300;
        boolean hsv_val = data.r_val_hsv[0] < 5 && data.g_val_hsv[9] < 100;
         */
        return Math.round(data.HistDataHsv[0][0]) < 5 && Math.round(data.HistDataHsv[1][0]) < 200 && Math.round(data.HistDataHsv[1][255]) < 50 && data.r_val_hsv[0] < 10 && data.in_hsv[0] > 5 && data.g_val_hsv[8] < 40 && data.g_val_hsv[9] <= 5
                && Math.round(data.HistDataRgb[0][0]) <= 0 && Math.round(data.HistDataRgb[2][0]) < 50;
    }

    private boolean checkClarendon(HistData data){
        /*
        boolean rgb_start = data.HistDataRgb[1][0] < 200;
        boolean hsv_start = data.HistDataHsv[0][0] < 10;
        boolean hsv_vals = data.b_val_hsv[5] < 150 & data.b_val_hsv[6] < 400 && data.b_val_hsv[7] < 150;
         */
        return Math.round(data.HistDataRgb[0][255]) < 120 && Math.round(data.HistDataRgb[1][0]) < 100
                && Math.round(data.HistDataHsv[0][0]) < 100 && data.b_val_hsv[5] < 50 && data.b_val_hsv[6] < 40 && data.b_val_hsv[7] < 25;
    }

    private boolean checkNashville(HistData data){
        /*
        boolean rgb_start = data.HistDataRgb[2][0] < 50;
        boolean rgb_end = data.HistDataRgb[1][255] < 5 && data.HistDataRgb[2][255] < 5;
        boolean rgb_vals = data.b_val_rgb[0] < 15 && data.b_val_rgb[1] < 100 && data.b_val_rgb[8] < 250 && data.b_val_rgb[9] < 15;
        boolean hsv_vals = data.r_val_hsv[0] < 10 && data.r_val_hsv[1] < 40;
        boolean hsv_in = data.in_hsv[0] > 5;
        boolean hsv_out = data.out_hsv[0] > 150;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 310 && data.HistDataHsv[2][0] < 500;
        return rgb_start && rgb_end && rgb_vals && hsv_vals && hsv_in && hsv_out && hsv_start;
         */
        return data.b_val_rgb[0] < 10 && data.b_val_rgb[1] < 30 && data.b_val_rgb[9] < 10;
    }

    private boolean checkGingham(HistData data){
        /*
        boolean rgb_popIn = data.in_rgb[1] > 15;
        boolean hsv_popIn = data.in_hsv[0] > 20;
        boolean hsv_popOut = data.out_hsv[0] > 90 && data.out_hsv[1] > 40 && data.out_hsv[2] > 70;
        boolean hsv_start = data.HistDataHsv[0][0] < 1;
        boolean hsv_end = data.HistDataHsv[0][255] < 50 && data.HistDataHsv[1][255] < 5 && data.HistDataHsv[2][255] < 1;
        boolean rgb_popOut = data.out_rgb[0] > 80 && data.out_rgb[1] > 80 && data.out_rgb[1] < 240;
        boolean rgb_start = data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 50;
        boolean rgb_end = data.HistDataRgb[0][255] < 80 && data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 1;
        boolean rgb_vals = data.r_val_rgb[0] < 20 && data.r_val_rgb[1] < 250 && data.r_val_rgb[9] < 80 && data.g_val_rgb[0] < 5 && data.g_val_rgb[1] < 250 && data.g_val_rgb[9] < 10 && data.b_val_rgb[0] < 50 && data.b_val_rgb[1] < 250 && data.b_val_rgb[9] < 10;
        boolean hsv_vals = data.r_val_hsv[0] < 5 && data.r_val_hsv[1] < 200 && data.r_val_hsv[9] < 50 && data.g_val_hsv[8] < 15 && data.g_val_hsv[9] < 5;
         */
        return data.r_val_rgb[0] <= 5 && data.r_val_rgb[9] <= 5 && data.g_val_rgb[0] <= 5 && data.g_val_rgb[9] <= 5 && data.b_val_rgb[0] <= 5 && data.b_val_rgb[9] <= 35 && data.out_rgb[0] <= 235 && data.out_rgb[2] <= 235 && data.out_rgb[1] <= 235 && data.in_rgb[0] >= 15 && data.in_rgb[1] >= 15 && data.in_rgb[2] >= 15;
    }

    private Bitmap getImage(int val, String photoType) {
        String photoPath = this.getExternalFilesDir(null) + "/Test/" + photoType + " (" + val + ").jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        if(bmp==null){
            photoPath = this.getExternalFilesDir(null) + "/Test/" + photoType + " (" + val + ").jpeg";
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(photoPath, options);
        }
        return bmp;
    }
}
