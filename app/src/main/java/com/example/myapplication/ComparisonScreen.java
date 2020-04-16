package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.lang.StringBuilder;

/*
    Screen to test accuracy of reversal in either discrete or continuous form
*/
public class ComparisonScreen extends AppCompatActivity {

    TextView resultView;

    String[] folder = {"Perpetua/", "Crema/", "Gingham/", "Nashville/", "Rise/", "Clarendon/"};
    String[] folderName = {"perp", "crem", "ging", "nash", "rise", "clar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison_screen);

        resultView = findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringBuilder sb = new StringBuilder();

        for(int j = 0; j < folder.length; j++){
            double red = 0.0;
            double green = 0.0;
            double blue = 0.0;
            double hue = 0.0;
            double saturation = 0.0;
            int improvements = 0;
            double value = 0.0;
            for(int i = 1; i <= 50; i++) {
                Bitmap original = getImage("/Images/Plain/un (" + i + ")");
                Bitmap filtered = getImage("/Images/" + folder[j] + folderName[j] + " (" + i + ")");
                Bitmap reversed = getImage("/Reversed/" + folder[j] + folderName[j] + i);
                if(compare(original, filtered, reversed)) improvements++;
                //red += result[0]; green += result[1]; blue += result[2]; hue += result[3]; saturation += result[4]; value += result[5];
            }
            Log.d(folder[j], improvements + "");
            //red /= 50; green /= 50; blue /= 50; hue /= 50; saturation /= 50; value /= 50;
            //sb.append(folder[j] + ": " + red + " " + green + " " + blue + " " + hue + " " + saturation + " " + value + "\n");
            sb.append(folder[j] + ": " + improvements + "\n");
        }
        resultView.setText(sb.toString());
    }

    private boolean compare(Bitmap original, Bitmap filtered, Bitmap reversed){
        int[] orig_filt = {0,0,0,0,0,0};
        int[] orig_rev = {0,0,0,0,0,0};

        Mat hsv_org_mat = new Mat();
        Mat hsv_filt_mat = new Mat();
        Mat hsv_rev_mat = new Mat();

        Mat rgb_org_mat = new Mat();
        Mat rgb_filt_mat = new Mat();
        Mat rgb_rev_mat = new Mat();

        Utils.bitmapToMat(original, rgb_org_mat);
        Utils.bitmapToMat(filtered, rgb_filt_mat);
        Utils.bitmapToMat(reversed, rgb_rev_mat);

        Imgproc.cvtColor(rgb_org_mat, rgb_org_mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(rgb_filt_mat, rgb_filt_mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(rgb_rev_mat, rgb_rev_mat, Imgproc.COLOR_RGBA2RGB);

        Imgproc.cvtColor(rgb_org_mat, hsv_org_mat, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(rgb_filt_mat, hsv_filt_mat, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(rgb_rev_mat, hsv_rev_mat, Imgproc.COLOR_RGB2HSV);

        Imgproc.resize(rgb_filt_mat, rgb_filt_mat, new Size(rgb_org_mat.width(), rgb_org_mat.height()), 0, 0);
        Imgproc.resize(rgb_rev_mat, rgb_rev_mat, new Size(rgb_org_mat.width(), rgb_org_mat.height()), 0, 0);

        Imgproc.resize(hsv_filt_mat, hsv_filt_mat, new Size(hsv_org_mat.width(), hsv_org_mat.height()), 0, 0);
        Imgproc.resize(hsv_rev_mat, hsv_rev_mat, new Size(hsv_org_mat.width(), hsv_org_mat.height()), 0, 0);

        for(int i = 0; i < rgb_org_mat.height(); i++){
            for(int j = 0; j < rgb_org_mat.width(); j++){
                double[] rgb_org_pixel = rgb_org_mat.get(i,j);
                double[] rgb_filt_pixel = rgb_filt_mat.get(i,j);
                double[] rgb_rev_pixel = rgb_rev_mat.get(i,j);
                double[] hsv_org_pixel = hsv_org_mat.get(i,j);
                double[] hsv_filt_pixel = hsv_filt_mat.get(i,j);
                double[] hsv_rev_pixel = hsv_rev_mat.get(i,j);

                for(int c = 0; c < 3; c++){
                    double rgb_org = rgb_org_pixel[c];
                    double rgb_filt = rgb_filt_pixel[c];
                    double rgb_rev = rgb_rev_pixel[c];

                    double hsv_org = hsv_org_pixel[c];
                    double hsv_filt = hsv_filt_pixel[c];
                    double hsv_rev = hsv_rev_pixel[c];

                    orig_filt[c] = orig_filt[c] + Math.abs((int)(rgb_org-rgb_filt));
                    orig_rev[c] = orig_rev[c] + Math.abs((int)(rgb_org-rgb_rev));

                    orig_filt[c+3] = orig_filt[c+3] + Math.abs((int)(hsv_org-hsv_filt));
                    orig_rev[c+3] = orig_rev[c+3] + Math.abs((int)(hsv_org-hsv_rev));
                }
            }
        }
        //Log.d("Values", orig_filt[0] + " " + orig_filt[1] + " " + orig_filt[2]);
        //Log.d("Values", orig_rev[0] + " " + orig_rev[1] + " " + orig_rev[2]);
        //double[] result = {orig_rev[0]/orig_filt[0], orig_rev[1]/orig_filt[1], orig_rev[2]/orig_filt[2], orig_rev[3]/orig_filt[3], orig_rev[4]/orig_filt[4], orig_rev[5]/orig_filt[5]};
        return (orig_rev[3] + orig_rev[4] + orig_rev[5]) < (orig_filt[3] + orig_filt[4] + orig_filt[5]);
    }

    private Bitmap getImage(String filename) {
        String photoPath = this.getExternalFilesDir(null) + filename + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        if(bmp==null){
            photoPath = this.getExternalFilesDir(null) + filename + ".jpeg";
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(photoPath, options);
        }
        return bmp;
    }
}
