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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import android.widget.Button;
import android.os.Environment;

import com.example.myapplication.MainActivity.ActivityOneData;

import java.util.List;
import java.util.ListIterator;

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

        filterList.add("Original");
        filterList.add("Gingham");

        imageView = findViewById(R.id.imageView);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Utils.bitmapToMat(bitmap, mat);
            imageView.setImageBitmap(bitmap);
        }
        catch( FileNotFoundException e){}
        results = findViewById(R.id.textView3);

        filterList.add("rgb hist");
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
        List<Mat> bgrPlanes = new ArrayList<>();
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
        Core.split(img, bgrPlanes);

        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2HSV);
        List<Mat> hsv_planes = new ArrayList<>();
        Core.split(img, hsv_planes);

        int histSize = 256;
        float[] range = {0, 256};
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;

        Mat b_hist_rgb = new Mat(), g_hist_rgb = new Mat(), r_hist_rgb = new Mat();
        Mat b_hist_hsv = new Mat(), g_hist_hsv = new Mat(), r_hist_hsv = new Mat();

        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), r_hist_rgb, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), g_hist_rgb, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), b_hist_rgb, new MatOfInt(histSize), histRange, accumulate);

        Imgproc.calcHist(hsv_planes, new MatOfInt(2), new Mat(), r_hist_hsv,  new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(hsv_planes, new MatOfInt(1), new Mat(), g_hist_hsv,  new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(hsv_planes, new MatOfInt(0), new Mat(), b_hist_hsv,  new MatOfInt(histSize), histRange, accumulate);

        int histW = 1024, histH = 800;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );

        Core.normalize(b_hist_rgb, b_hist_rgb, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(g_hist_rgb, g_hist_rgb, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(r_hist_rgb, r_hist_rgb, 0, histImage.rows(), Core.NORM_MINMAX);

        Core.normalize(b_hist_hsv, b_hist_hsv, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(g_hist_hsv, g_hist_hsv, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(r_hist_hsv, r_hist_hsv, 0, histImage.rows(), Core.NORM_MINMAX);

        float[] rHistDataRgb = new float[(int) (r_hist_rgb.total() * r_hist_rgb.channels())];
        r_hist_rgb.get(0, 0, rHistDataRgb);
        float[] gHistDataRgb = new float[(int) (g_hist_rgb.total() * g_hist_rgb.channels())];
        g_hist_rgb.get(0, 0, gHistDataRgb);
        float[] bHistDataRgb = new float[(int) (b_hist_rgb.total() * b_hist_rgb.channels())];
        b_hist_rgb.get(0, 0, bHistDataRgb);

        float[] rHistDataHsv = new float[(int) (r_hist_hsv.total() * r_hist_hsv.channels())];
        r_hist_hsv.get(0, 0, rHistDataHsv);
        float[] gHistDataHsv = new float[(int) (g_hist_hsv.total() * g_hist_hsv.channels())];
        g_hist_hsv.get(0, 0, gHistDataHsv);
        float[] bHistDataHsv = new float[(int) (b_hist_hsv.total() * b_hist_hsv.channels())];
        b_hist_hsv.get(0, 0, bHistDataHsv);

        int b_in_rgb = -1;
        int g_in_rgb = -1;
        int r_in_rgb = -1;
        int b_out_rgb = 0;
        int g_out_rgb = 0;
        int r_out_rgb = 0;

        int b_in_hsv = -1;
        int g_in_hsv = -1;
        int r_in_hsv = -1;
        int b_out_hsv = 0;
        int g_out_hsv = 0;
        int r_out_hsv = 0;

        int r_val_rgb[] = { 0,0,0,0,0,0,0,0,0,0 };
        int g_val_rgb[] = { 0,0,0,0,0,0,0,0,0,0 };
        int b_val_rgb[] = { 0,0,0,0,0,0,0,0,0,0 };
        int r_val_hsv[] = { 0,0,0,0,0,0,0,0,0,0 };
        int g_val_hsv[] = { 0,0,0,0,0,0,0,0,0,0 };
        int b_val_hsv[] = { 0,0,0,0,0,0,0,0,0,0 };

        for( int i = 0; i < histSize; i++ ) {
            if(i < 25) { r_val_rgb[0] += Math.round(rHistDataRgb[i]); g_val_rgb[0] += Math.round(gHistDataRgb[i]); b_val_rgb[0] += Math.round(bHistDataRgb[i]); }
            else if (i < 50) { r_val_rgb[1] +=  Math.round(rHistDataRgb[i]); g_val_rgb[1] += Math.round(gHistDataRgb[i]);  b_val_rgb[1] += Math.round(bHistDataRgb[i]); }
            else if (i < 75) { r_val_rgb[2] +=  Math.round(rHistDataRgb[i]); g_val_rgb[2] += Math.round(gHistDataRgb[i]);  b_val_rgb[2] += Math.round(bHistDataRgb[i]); }
            else if (i < 100) { r_val_rgb[3] +=  Math.round(rHistDataRgb[i]); g_val_rgb[3] += Math.round(gHistDataRgb[i]);  b_val_rgb[3] += Math.round(bHistDataRgb[i]); }
            else if (i < 125) { r_val_rgb[4] +=  Math.round(rHistDataRgb[i]); g_val_rgb[4] += Math.round(gHistDataRgb[i]);  b_val_rgb[4] += Math.round(bHistDataRgb[i]); }
            else if (i < 150) { r_val_rgb[5] +=  Math.round(rHistDataRgb[i]); g_val_rgb[5] += Math.round(gHistDataRgb[i]);  b_val_rgb[5] += Math.round(bHistDataRgb[i]); }
            else if (i < 175) { r_val_rgb[6] +=  Math.round(rHistDataRgb[i]); g_val_rgb[6] += Math.round(gHistDataRgb[i]);  b_val_rgb[6] += Math.round(bHistDataRgb[i]); }
            else if (i < 200) { r_val_rgb[7] +=  Math.round(rHistDataRgb[i]); g_val_rgb[7] += Math.round(gHistDataRgb[i]);  b_val_rgb[7] += Math.round(bHistDataRgb[i]); }
            else if (i < 225) { r_val_rgb[8] +=  Math.round(rHistDataRgb[i]); g_val_rgb[8] += Math.round(gHistDataRgb[i]);  b_val_rgb[8] += Math.round(bHistDataRgb[i]); }
            else if (i < histSize) { r_val_rgb[9] +=  Math.round(rHistDataRgb[i]); g_val_rgb[9] += Math.round(gHistDataRgb[i]);  b_val_rgb[9] += Math.round(bHistDataRgb[i]); }

            if (i < 25) { r_val_hsv[0] += Math.round(rHistDataHsv[i]); g_val_hsv[0] += Math.round(gHistDataHsv[i]);  b_val_hsv[0] += Math.round(bHistDataHsv[i]); }
            else if (i < 50) { r_val_hsv[1] += Math.round(rHistDataHsv[i]); g_val_hsv[1] += Math.round(gHistDataHsv[i]);  b_val_hsv[1] += Math.round(bHistDataHsv[i]); }
            else if (i < 75) { r_val_hsv[2] += Math.round(rHistDataHsv[i]); g_val_hsv[2] += Math.round(gHistDataHsv[i]);  b_val_hsv[2] += Math.round(bHistDataHsv[i]); }
            else if (i < 100) { r_val_hsv[3] += Math.round(rHistDataHsv[i]); g_val_hsv[3] += Math.round(gHistDataHsv[i]);  b_val_hsv[3] += Math.round(bHistDataHsv[i]); }
            else if (i < 125) { r_val_hsv[4] += Math.round(rHistDataHsv[i]); g_val_hsv[4] += Math.round(gHistDataHsv[i]);  b_val_hsv[4] += Math.round(bHistDataHsv[i]); }
            else if (i < 150) { r_val_hsv[5] += Math.round(rHistDataHsv[i]); g_val_hsv[5] += Math.round(gHistDataHsv[i]);  b_val_hsv[5] += Math.round(bHistDataHsv[i]); }
            else if (i < 175) { r_val_hsv[6] += Math.round(rHistDataHsv[i]); g_val_hsv[6] += Math.round(gHistDataHsv[i]);  b_val_hsv[6] += Math.round(bHistDataHsv[i]); }
            else if (i < 200) { r_val_hsv[7] += Math.round(rHistDataHsv[i]); g_val_hsv[7] += Math.round(gHistDataHsv[i]);  b_val_hsv[7] += Math.round(bHistDataHsv[i]); }
            else if (i < 225) { r_val_hsv[8] += Math.round(rHistDataHsv[i]); g_val_hsv[8] += Math.round(gHistDataHsv[i]);  b_val_hsv[8] += Math.round(bHistDataHsv[i]); }
            else if (i < histSize) { r_val_hsv[9] += Math.round(rHistDataHsv[i]); g_val_hsv[9] += Math.round(gHistDataHsv[i]);  b_val_hsv[9] += Math.round(bHistDataHsv[i]); }

            if (b_in_rgb == -1 && Math.round(bHistDataRgb[i]) != 0) b_in_rgb = i;
            if (g_in_rgb == -1 && Math.round(gHistDataRgb[i]) != 0) g_in_rgb = i;
            if (r_in_rgb == -1 &&  Math.round(rHistDataRgb[i]) != 0) r_in_rgb = i;
            if (Math.round(bHistDataRgb[i]) != 0) b_out_rgb = i;
            if (Math.round(gHistDataRgb[i]) != 0) g_out_rgb = i;
            if (Math.round(rHistDataRgb[i]) != 0) r_out_rgb = i;

            if (b_in_hsv == -1 && Math.round(bHistDataHsv[i])!= 0) b_in_hsv = i;
            if (g_in_hsv == -1 && Math.round(gHistDataHsv[i]) != 0) g_in_hsv = i;
            if (r_in_hsv == -1 && Math.round(rHistDataHsv[i]) != 0) r_in_hsv = i;
            if (Math.round(bHistDataHsv[i]) != 0) b_out_hsv = i;
            if (Math.round(gHistDataHsv[i]) != 0) g_out_hsv = i;
            if (Math.round(rHistDataHsv[i]) != 0) r_out_hsv = i;
        }
        for (int i = 0; i < 9; i++) {
            r_val_hsv[i] /= 25; r_val_rgb[i] /= 25;
            g_val_hsv[i] /= 25; g_val_rgb[i] /= 25;
            b_val_hsv[i] /= 25; b_val_rgb[i] /= 25;
        }
        r_val_rgb[9] /= 30; g_val_rgb[9] /= 30; b_val_rgb[9] /= 30;
        r_val_hsv[9] /= 30; g_val_hsv[9] /= 30; b_val_hsv[9] /= 30;

        int thing = 0;

        //Gingham, RGB
        if (r_val_rgb[0] <= 5 && r_val_rgb[9] <= 5 && g_val_rgb[0] <= 5 && g_val_rgb[9] <= 5 && b_val_rgb[0] <= 5 && b_val_rgb[9] <= 35 && r_out_rgb <= 235 && b_out_rgb <= 235 && g_out_rgb <= 235 && r_in_rgb >= 15 && g_in_rgb >= 15 && b_in_rgb >= 15) filterList.add("Gingham");
        else {
            //Nashville, RGB
            if (b_val_rgb[0] < 10 && b_val_rgb[1] < 30 && b_val_rgb[9] < 10) filterList.add("Nashville");

            //Clarendon, RGB & HSV
            if (Math.round(rHistDataRgb[255]) < 120 && Math.round(gHistDataRgb[0]) < 100
                    && Math.round(rHistDataHsv[0]) < 100 && b_val_hsv[5] < 50 && b_val_hsv[6] < 40 && b_val_hsv[7] < 25) filterList.add("Clarendon");

            //Perpetua, RGB & HSV
            if (g_in_rgb > 5 && Math.round(bHistDataRgb[255]) < 100 && Math.round(rHistDataRgb[0]) < 5 && Math.round(gHistDataRgb[0]) < 5 && Math.round(bHistDataRgb[0]) < 300
                    && r_in_hsv > 5 && g_in_hsv < 10 && Math.round(rHistDataHsv[0]) == 0 &&  Math.round(gHistDataHsv[0]) < 150
                        && g_val_hsv[8] < 50 && g_val_hsv[9] < 40 && b_val_hsv[7] < 40 && b_val_hsv[6] < 45 && b_val_hsv[5] < 35) filterList.add("Perpetua");

            //Crema, HSV & RGB
            if (g_val_hsv[9] <= 30 && g_val_hsv[8] <= 30 && g_val_hsv[7] < 100 && Math.round(rHistDataHsv[0]) < 5 && Math.round(gHistDataHsv[0]) < 400 && Math.round(rHistDataHsv[255]) < 100 && Math.round(gHistDataHsv[255]) < 100
                    && Math.round(gHistDataRgb[255]) < 50 && Math.round(bHistDataRgb[255]) <= 1) filterList.add("Crema");

            //Rise, HSV && RGB
            if (Math.round(rHistDataHsv[0]) < 5 && Math.round(gHistDataHsv[0]) < 200 && Math.round(gHistDataHsv[255]) < 50 && r_val_hsv[0] < 10 && r_in_hsv > 5 && g_val_hsv[8] < 40 && g_val_hsv[9] <= 5
                    && Math.round(rHistDataRgb[0]) <= 0 && Math.round(bHistDataRgb[0]) < 50) filterList.add("Rise");

        }
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
                string += ", ";
            }
            string += list[list.length - 1];
        }
        results.setText("Possible filters used:" + string);
    }

    public static int[] detect_contrast_saturation(Mat image) {
        int[] val = new int[2];
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);
        int rows = image.rows();
        int cols = image.cols();
        int totalPixels = rows * cols;
        double saturationTotal = 0;
        double lo_hi = 0;
        double mid = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(i, j);
                saturationTotal += pixel[1];
                if (pixel[2] > 180 || pixel[2] < 100) lo_hi++;
                else mid++;
            }
        }
        val[0] = (int)((lo_hi) / (lo_hi + mid) * 100);
        val[1] = (int)saturationTotal/totalPixels;
        return val;
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