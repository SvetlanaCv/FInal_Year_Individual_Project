package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class TestScreen extends AppCompatActivity {

    static final String[] photoTypes = {"ging", "nash", "clar", "rise", "crem", "perp", "un"};

    TextView results;
    int TP, TN, FP, FN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        results = findViewById(R.id.results);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int t = 0; t < photoTypes.length; t++) {
            for (int i = 1; i <= 55; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                if(checkPlain(data)) {
                    if(photoTypes[t].equals("un")) TP++;
                    else { Log.d("FP", photoTypes[t] + i); FP++;}
                }
                else{
                    if(photoTypes[t].equals("un")) { Log.d("FN", photoTypes[t] + i); FN++; }
                    else TN++;
                }
            }
        }
        String str = "TP: " + TP + "\n" + "TN: " + TN + "\n" + "FP: " + FP + "\n" + "FN: " + FN;
        results.setText(str);
    }

    private boolean checkPlain(HistData data){
        return !checkGingham(data) && !checkPerpetua(data) && !checkCrema(data) && !checkRise(data) && !checkClarendon(data) && !checkNashville(data);
    }

    private boolean checkPerpetua(HistData data){
        boolean rgb_popIn = data.in_rgb[1] > 5 && data.in_rgb[0] < 35 && data.in_rgb[1] < 35 && data.in_rgb[2] < 25;
        boolean rgb_start = data.HistDataRgb[0][0] < 5 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 300;
        boolean rgb_end = data.HistDataRgb[2][255] < 60;
        boolean rgb_val = data.g_val_rgb[0] < 200 && data.b_val_rgb[9] < 400;
        boolean hsv_popIn = data.in_hsv[0] > 5 && data.in_hsv[0] < 45 && data.in_hsv[1] < 20 && data.in_hsv[2] < 20;
        boolean hsv_popOut = data.out_hsv[0] > 215;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 150;
        boolean hsv_end = data.HistDataHsv[1][255] < 300 && data.HistDataHsv[2][255] < 1;
        boolean hsv_vals = data.g_val_hsv[2] > 30 && data.g_val_hsv[3] > 30 && data.g_val_hsv[9] < 30 && data.b_val_hsv[5] < 25 && data.b_val_hsv[6] < 10 && data.b_val_hsv[7] < 15;
        return rgb_popIn && rgb_start && rgb_end && rgb_val && hsv_popIn && hsv_popOut && hsv_start && hsv_end && hsv_vals;
    }

    private boolean checkCrema(HistData data){
        boolean rgb_popOut = data.out_rgb[2] <= 250;
        boolean rgb_start = data.HistDataRgb[0][0] < 15 && data.HistDataRgb[1][0] < 5 && data.HistDataRgb[2][0] < 200;
        boolean rgb_end = data.HistDataRgb[0][255] < 15 && data.HistDataRgb[1][255] < 5 && data.HistDataRgb[2][255] < 1;
        boolean rgb_val = data.b_val_rgb[9] < 80;
        boolean hsv_start = data.HistDataHsv[0][0] < 1 && data.HistDataHsv[1][0] < 250 && data.HistDataHsv[2][0] < 700;
        boolean hsv_end = data.HistDataHsv[0][255] < 10 && data.HistDataHsv[1][255] < 200;
        boolean hsv_val = data.g_val_hsv[9] < 25 && data.b_val_hsv[2] < 40;
        return rgb_popOut && rgb_start && rgb_end && rgb_val && hsv_start && hsv_end && hsv_val;
    }

    private boolean checkRise(HistData data){
        boolean rgb_popIn = data.in_rgb[0] > 10 && data.in_rgb[1] > 5;
        boolean rgb_start = data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 15;
        boolean rgb_end = data.HistDataRgb[2][255] < 50;
        boolean rgb_val = data.g_val_rgb[0] < 140 && data.b_val_rgb[0] < 30;
        boolean hsv_in = data.in_hsv[0] > 10 && data.in_hsv[2] == 0;
        boolean hsv_start = data.HistDataHsv[0][0] < 1;
        boolean hsv_end = data.HistDataHsv[1][255] < 10 && data.HistDataHsv[2][255] < 1;
        boolean hsv_val = data.r_val_hsv[0] < 5 && data.g_val_hsv[9] < 100;
        return rgb_popIn && rgb_start && rgb_end && rgb_val && hsv_in && hsv_start && hsv_end && hsv_val;
    }

    private boolean checkClarendon(HistData data){
        boolean rgb_popOut = data.out_rgb[1] > 200;
        boolean rgb_start = data.HistDataRgb[1][0] < 200;
        boolean rgb_end = data.HistDataRgb[0][255] < 400;
        boolean hsv_popOut = data.out_hsv[0] > 220 && data.out_hsv[1] > 200;
        boolean hsv_start = data.HistDataHsv[0][0] < 1;
        boolean hsv_vals = data.b_val_hsv[5] < 100 & data.b_val_hsv[6] < 100 && data.b_val_hsv[7] < 20;
        return rgb_popOut && rgb_start && rgb_end && hsv_popOut && hsv_start && hsv_vals;
    }

    private boolean checkNashville(HistData data){
        boolean rgb_popIn = data.in_rgb[0] < 5;
        boolean rgb_popOut = data.out_rgb[1] < 250;
        boolean rgb_start = data.HistDataRgb[2][0] < 1;
        boolean rgb_end = data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 5;
        boolean rgb_vals = data.r_val_rgb[0] > 10 && data.b_val_rgb[0] < 5 && data.b_val_rgb[1] < 20 && data.b_val_rgb[9] < 5;
        boolean hsv_vals = data.b_val_hsv[2] < 100;
        return rgb_popIn && rgb_popOut && rgb_start && rgb_end && rgb_vals && hsv_vals;
    }

    private boolean checkGingham(HistData data){
        boolean rgb_popIn = data.in_rgb[0] > 20 && data.in_rgb[1] > 25 && data.in_rgb[2] > 10;
        boolean hsv_popIn = data.in_hsv[0] > 30;
        boolean hsv_popOut = data.out_hsv[2] > 125 && data.out_hsv[0] < 250 && data.out_hsv[1] < 230;
        boolean hsv_start = data.HistDataHsv[0][0] < 1;
        boolean hsv_end = data.HistDataHsv[0][255] < 1 && data.HistDataHsv[1][255] < 1 && data.HistDataHsv[2][255] < 1;
        boolean rgb_popOut = data.out_rgb[0] < 250 && data.out_rgb[1] < 230;
        boolean rgb_start = data.HistDataRgb[0][0] < 1 && data.HistDataRgb[1][0] < 1 && data.HistDataRgb[2][0] < 1;
        boolean rgb_end = data.HistDataRgb[0][255] < 1 && data.HistDataRgb[1][255] < 1 && data.HistDataRgb[2][255] < 1;
        boolean rgb_vals = data.r_val_rgb[0] < 5 && data.r_val_rgb[9] < 10 && data.g_val_rgb[0] < 5 && data.g_val_rgb[9] < 5 && data.b_val_rgb[0] < 5 && data.b_val_rgb[9] < 5;
        boolean hsv_vals = data.g_val_hsv[0] > 50 && data.r_val_hsv[0] < 5 && data.r_val_hsv[9] < 10 && data.g_val_hsv[8] < 10 && data.g_val_hsv[9] < 5;
        return rgb_popIn && rgb_popOut && rgb_start && rgb_end && rgb_vals && hsv_vals && hsv_popIn && hsv_popOut && hsv_start && hsv_end;
    }

    private Bitmap getImage(int val, String photoType) {
        String photoPath = this.getExternalFilesDir(null) + "/" + photoType + val + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        if(bmp==null){
            photoPath = this.getExternalFilesDir(null) + "/" + photoType + val + ".jpeg";
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(photoPath, options);
        }
        return bmp;
    }
}
