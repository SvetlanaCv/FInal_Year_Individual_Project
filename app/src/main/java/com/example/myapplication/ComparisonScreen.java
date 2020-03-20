package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.lang.StringBuilder;

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

        for(int j = 0; j < 1; j++){
            int improvements = 0;
            for(int i = 1; i <= 50; i++) {
                Bitmap original = getImage("/Images/Plain/un (" + i + ")");
                Bitmap filtered = getImage("/Images/" + folder[j] + folderName[j] + " (" + i + ")");
                Bitmap reversed = getImage("/Reversed/" + folder[j] + folderName[j] + i);
                if(compare(original, filtered, reversed)) improvements++;
            }
            sb.append(folder[j] + ": " + improvements + "\n");
        }
        resultView.setText(sb.toString());
    }

    private boolean compare(Bitmap original, Bitmap filtered, Bitmap reversed){
        int[] orig_filt = {0,0,0};
        int[] orig_rev = {0,0,0};

        Mat org_mat = new Mat();
        Utils.bitmapToMat(original, org_mat);
        Mat filt_mat = new Mat();
        Utils.bitmapToMat(filtered, filt_mat);
        Mat rev_mat = new Mat();
        Utils.bitmapToMat(reversed, rev_mat);

        Imgproc.cvtColor(org_mat, org_mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(filt_mat, filt_mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(rev_mat, rev_mat, Imgproc.COLOR_RGBA2RGB);

        Imgproc.resize(filt_mat, filt_mat, new Size(org_mat.width(), org_mat.height()), 0, 0);
        Imgproc.resize(rev_mat, rev_mat, new Size(org_mat.width(), org_mat.height()), 0, 0);

        for(int i = 0; i < org_mat.height(); i++){
            for(int j = 0; j <org_mat.width(); j++){
                double[] org_pixel = org_mat.get(i,j);
                double[] filt_pixel = filt_mat.get(i,j);
                double[] rev_pixel = rev_mat.get(i,j);

                for(int c = 0; c < 3; c++){
                    double org = org_pixel[c];
                    double filt = filt_pixel[c];
                    double rev = rev_pixel[c];
                    int val = (int)(org-filt);
                    int val2 = (int)(org-rev);
                    orig_filt[c] = orig_filt[c] + Math.abs(val);
                    orig_rev[c] = orig_rev[c] + Math.abs(val2);
                }
            }
        }
        int thing = orig_rev[0];
        return (thing + orig_rev[1] + orig_rev[2]) < (orig_filt[0] + orig_filt[1] + orig_filt[2]);
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
