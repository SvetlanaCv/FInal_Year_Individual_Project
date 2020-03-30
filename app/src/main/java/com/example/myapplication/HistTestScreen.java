package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.StringBuilder;
import android.text.method.ScrollingMovementMethod;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HistTestScreen extends AppCompatActivity {

    String[] folder = {"Perpetua/", "Crema/", "Gingham/", "Nashville/", "Rise/", "Clarendon/", "Plain/"};
    String[] folderName = {"perp", "crem", "ging", "nash", "rise", "clar", "un"};


    StringBuilder sb;
    TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_test_screen);

        resultData = findViewById(R.id.results);
        resultData.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sb = new StringBuilder();

        for(int t = 0; t < folder.length; t++) {
            sb.append(folder[t] + "\n");
            double[] total_results = {0,0,0,0,0,0,0};
            for (int i = 1; i <= 20; i++) {
                Bitmap bmp = getImage("/Test/" + folder[t] + folderName[t] + " (" + i + ")");
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);
                double[] hist_results = checkHists(data);
                double[] filter_results = {checkPerpetua(data, hist_results[0]), checkCrema(data, hist_results[1]), checkGingham(data, hist_results[2]), checkNashville(data, hist_results[3]), checkRise(data, hist_results[4]), checkClarendon(data, hist_results[5])};

                for(int j = 0; j < 6; j++) total_results[j] += filter_results[j];
                total_results[6] += hist_results[6]/6;

                for(int j = 0; j < 6; j++) hist_results[j] /= 6;
                write(folderName[t] + " " + i + ": " + hist_results[0] + " " + hist_results[1] + " " +
                        hist_results[2] + " " + hist_results[3] + " " + hist_results[4] + " " + hist_results[5] + " " +
                            hist_results[6] + "\n", "/HistTest.txt");
                write(folderName[t] + " " + i + ": " + filter_results[0] + " " + filter_results[1] + " " +
                        filter_results[2] + " " + filter_results[3] + " " + filter_results[4] + " " + filter_results[5] +
                            "\n", "/AllTest.txt");
            }
            for(int r = 0; r < total_results.length; r++) {
                total_results[r] /= 20;
                sb.append(folderName[r] + " " + total_results[r] + "\n");
            }
            sb.append("\n");
        }
        resultData.setText(sb.toString());
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

    private void write(String txt, String filename){
        try {
            File textFile = new File(this.getExternalFilesDir(null), filename);
            if (!textFile.exists())
                textFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true ));

            writer.write(txt);
            writer.close();

            MediaScannerConnection.scanFile(this,
                    new String[]{textFile.toString()},
                    null,
                    null);
        } catch (Exception e) { e.printStackTrace(); }
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
}
