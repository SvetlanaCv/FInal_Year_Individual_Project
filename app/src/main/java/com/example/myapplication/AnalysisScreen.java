package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.StringBuilder;

import android.media.MediaScannerConnection;

public class AnalysisScreen extends AppCompatActivity {

    TextView text;

    static final String[] photoTypes = {"ging", "nash", "clar", "rise", "crem", "perp", "un"};
    static final String photoType = "nash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_screen);

        text = findViewById(R.id.text);
        text.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();

        for(int t = 6; t < 7; t++) {
            int min_r_val_rgb[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};
            int min_g_val_rgb[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};
            int min_b_val_rgb[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};
            int min_r_val_hsv[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};
            int min_g_val_hsv[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};
            int min_b_val_hsv[] = {800, 800, 800, 800, 800, 800, 800, 800, 800, 800};

            int max_r_val_rgb[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int max_g_val_rgb[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int max_b_val_rgb[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int max_r_val_hsv[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int max_g_val_hsv[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int max_b_val_hsv[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

            int min_rgb_in[] = {255, 255, 255};
            int min_hsv_in[] = {255, 255, 255};
            int max_rgb_in[] = {0, 0, 0};
            int max_hsv_in[] = {0, 0, 0};

            int min_rgb_out[] = {255, 255, 255};
            int min_hsv_out[] = {255, 255, 255};
            int max_rgb_out[] = {0, 0, 0};
            int max_hsv_out[] = {0, 0, 0};

            int min_rgb_start[] = {800, 800, 800};
            int min_hsv_start[] = {800, 800, 800};
            int min_rgb_end[] = {800, 800, 800};
            int min_hsv_end[] = {800, 800, 800};

            int max_rgb_start[] = {0, 0, 0};
            int max_hsv_start[] = {0, 0, 0};
            int max_rgb_end[] = {0, 0, 0};
            int max_hsv_end[] = {0, 0, 0};

            sb.append(photoTypes[t] + "\n");
            for (int i = 1; i <= 55; i++) {
                Bitmap bmp = getImage(i, photoTypes[t]);
                Mat mat = new Mat();
                Utils.bitmapToMat(bmp, mat);
                HistData data = new HistData(mat);

                write(Integer.toString(i), photoTypes[t]);

                write("RGB Vals", photoTypes[t]);
                for (int j = 0; j < 10; j++) {
                    write(j + ": " + data.r_val_rgb[j] + " " + data.g_val_rgb[j] + " " + data.b_val_rgb[j], photoTypes[t]);
                    if (data.r_val_rgb[j] > max_r_val_rgb[j]) max_r_val_rgb[j] = data.r_val_rgb[j];
                    if (data.g_val_rgb[j] > max_g_val_rgb[j]) max_g_val_rgb[j] = data.g_val_rgb[j];
                    if (data.b_val_rgb[j] > max_b_val_rgb[j]) max_b_val_rgb[j] = data.b_val_rgb[j];

                    if (data.r_val_rgb[j] < min_r_val_rgb[j]) min_r_val_rgb[j] = data.r_val_rgb[j];
                    if (data.g_val_rgb[j] < min_g_val_rgb[j]) min_g_val_rgb[j] = data.g_val_rgb[j];
                    if (data.b_val_rgb[j] < min_b_val_rgb[j]) min_b_val_rgb[j] = data.b_val_rgb[j];
                }
                write("\nHSV Vals", photoTypes[t]);
                for (int j = 0; j < 10; j++) {
                    write(j + ": " + data.r_val_hsv[j] + " " + data.g_val_hsv[j] + " " + data.b_val_hsv[j], photoTypes[t]);
                    if (data.r_val_hsv[j] > max_r_val_hsv[j]) max_r_val_hsv[j] = data.r_val_hsv[j];
                    if (data.g_val_hsv[j] > max_g_val_hsv[j]) max_g_val_hsv[j] = data.g_val_hsv[j];
                    if (data.b_val_hsv[j] > max_b_val_hsv[j]) max_b_val_hsv[j] = data.b_val_hsv[j];

                    if (data.r_val_hsv[j] < min_r_val_hsv[j]) min_r_val_hsv[j] = data.r_val_hsv[j];
                    if (data.g_val_hsv[j] < min_g_val_hsv[j]) min_g_val_hsv[j] = data.g_val_hsv[j];
                    if (data.b_val_hsv[j] < min_b_val_hsv[j]) min_b_val_hsv[j] = data.b_val_hsv[j];
                }

                write("\nRGB Pop In: " + data.in_rgb[0] + " " + data.in_rgb[1] + " " + data.in_rgb[2], photoTypes[t]);
                write("RGB Pop Out: " + data.out_rgb[0] + " " + data.out_rgb[1] + " " + data.out_rgb[2], photoTypes[t]);
                write("\nHSV Pop In: " + data.in_hsv[0] + " " + data.in_hsv[1] + " " + data.in_hsv[2], photoTypes[t]);
                write("HSV Pop Out: " + data.out_hsv[0] + " " + data.out_hsv[1] + " " + data.out_hsv[2] + "\n", photoTypes[t]);

                write("\nRGB Start Vals: " + data.HistDataRgb[0][0] + " " + data.HistDataRgb[1][0] + " " + data.HistDataRgb[2][0], photoTypes[t]);
                write("RGB End Vals: " + data.HistDataRgb[0][255] + " " + data.HistDataRgb[1][255] + " " + data.HistDataRgb[2][255], photoTypes[t]);
                write("\nHSV Start Vals: " + data.HistDataHsv[0][0] + " " + data.HistDataHsv[1][0] + " " + data.HistDataHsv[2][0], photoTypes[t]);
                write("HSV End Vals: " + data.HistDataHsv[0][255] + " " + data.HistDataHsv[1][255] + " " + data.HistDataHsv[2][255] + "\n", photoTypes[t]);

                for (int j = 0; j < 3; j++) {
                    if (min_rgb_in[j] > data.in_rgb[j]) min_rgb_in[j] = data.in_rgb[j];
                    if (max_rgb_in[j] < data.in_rgb[j]) max_rgb_in[j] = data.in_rgb[j];

                    if (min_rgb_out[j] > data.out_rgb[j]) min_rgb_out[j] = data.out_rgb[j];
                    if (max_rgb_out[j] < data.out_rgb[j]) max_rgb_out[j] = data.out_rgb[j];

                    if (min_hsv_in[j] > data.in_hsv[j]) min_hsv_in[j] = data.in_hsv[j];
                    if (max_hsv_in[j] < data.in_hsv[j]) max_hsv_in[j] = data.in_hsv[j];

                    if (min_hsv_out[j] > data.out_hsv[j]) min_hsv_out[j] = data.out_hsv[j];
                    if (max_hsv_out[j] < data.out_hsv[j]) max_hsv_out[j] = data.out_hsv[j];

                    if (min_rgb_start[j] > data.HistDataRgb[j][0])
                        min_rgb_start[j] = (int) data.HistDataRgb[j][0];
                    if (max_rgb_start[j] < data.HistDataRgb[j][0])
                        max_rgb_start[j] = (int) data.HistDataRgb[j][0];

                    if (min_hsv_start[j] > data.HistDataHsv[j][0])
                        min_hsv_start[j] = (int) data.HistDataHsv[j][0];
                    if (max_hsv_start[j] < data.HistDataHsv[j][0])
                        max_hsv_start[j] = (int) data.HistDataHsv[j][0];

                    if (min_rgb_end[j] > data.HistDataRgb[j][255])
                        min_rgb_end[j] = (int) data.HistDataRgb[j][255];
                    if (max_rgb_end[j] < data.HistDataRgb[j][255])
                        max_rgb_end[j] = (int) data.HistDataRgb[j][255];

                    if (min_hsv_end[j] > data.HistDataHsv[j][255])
                        min_hsv_end[j] = (int) data.HistDataHsv[j][255];
                    if (max_hsv_end[j] < data.HistDataHsv[j][255])
                        max_hsv_end[j] = (int) data.HistDataHsv[j][255];
                }
            }

            sb.append("RGB\nVals:\nMin\n");
            for (int i = 0; i < 10; i++)
                sb.append(i + ": " + min_r_val_rgb[i] + " " + min_g_val_rgb[i] + " " + min_b_val_rgb[i] + "\n");
            sb.append("Max\n");
            for (int i = 0; i < 10; i++)
                sb.append(i + ": " + max_r_val_rgb[i] + " " + max_g_val_rgb[i] + " " + max_b_val_rgb[i] + "\n");

            sb.append("\nHSV\nVals:\nMin\n");
            for (int i = 0; i < 10; i++)
                sb.append(i + ": " + min_r_val_hsv[i] + " " + min_g_val_hsv[i] + " " + min_b_val_hsv[i] + "\n");
            sb.append("Max\n");
            for (int i = 0; i < 10; i++)
                sb.append(i + ": " + max_r_val_hsv[i] + " " + max_g_val_hsv[i] + " " + max_b_val_hsv[i] + "\n");

            sb.append("\nMin Pop In RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_rgb_in[i]);
            sb.append("\nMax Pop In RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_rgb_in[i]);
            sb.append("\nMin Pop Out RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_rgb_out[i]);
            sb.append("\nMax Pop Out RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_rgb_out[i]);
            sb.append("\nMin Start Val RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_rgb_start[i]);
            sb.append("\nMax Start Val RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_rgb_start[i]);
            sb.append("\nMin End Val RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_rgb_end[i]);
            sb.append("\nMax End Val RGB:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_rgb_end[i]);

            sb.append("\n\nMin Pop In HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_hsv_in[i]);
            sb.append("\nMax Pop In HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_hsv_in[i]);
            sb.append("\nMin Pop Out HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_hsv_out[i]);
            sb.append("\nMax Pop Out HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_hsv_out[i]);
            sb.append("\nMin Start Val HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_hsv_start[i]);
            sb.append("\nMax Start Val HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_hsv_start[i]);
            sb.append("\nMin End Val HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + min_hsv_end[i]);
            sb.append("\nMax End Val HSV:");
            for (int i = 0; i < 3; i++) sb.append(" " + max_hsv_end[i]);

            sb.append("\n\n");
        }

        text.setText(sb.toString());
    }

    private void write(String txt, String photoType){
        try {
            File textFile = new File(this.getExternalFilesDir(null), photoType +"Results.txt");
            if (!textFile.exists())
                textFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(textFile, true ));

            writer.write(txt + "\n");
            writer.close();

            MediaScannerConnection.scanFile(this,
                    new String[]{textFile.toString()},
                    null,
                    null);
        } catch (Exception e) { e.printStackTrace(); }
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
