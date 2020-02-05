package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.view.View;

import org.opencv.core.Point;

import org.opencv.android.Utils;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;

import java.util.TreeMap;

public class Main3Activity extends AppCompatActivity {

    private ImageView imageView;
    Bitmap originalBitmap;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = getIntent();
        String[] string = (String[])intent.getSerializableExtra("filter list");
        String bitmapName = (String)intent.getSerializableExtra("bitmap name");

        imageView = findViewById(R.id.imageView2);

        backButton = findViewById(R.id.button3);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        try {
            originalBitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Bitmap bitmap = originalBitmap;
            imageView.setImageBitmap(bitmap);
        }
        catch( FileNotFoundException e){}

        LinearLayout linearlayout = findViewById(R.id.buttonLayout);
        linearlayout.setOrientation(LinearLayout.VERTICAL);

        for(int i = 0; i<string.length;i++)
        {
            LinearLayout linear1 = new LinearLayout(this);
            linear1.setOrientation(LinearLayout.HORIZONTAL);
            linearlayout.addView(linear1);
            Button b = new Button(this);
            b.setText(string[i]);
            b.setId(i);
            b.setTag(string[i]);
            b.setTextSize(10);
            b.setPadding(8, 3, 8, 3);
            b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

            linear1.addView(b);

            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String tag = v.getTag().toString();
                    removeFilter(tag);
                }
            });
        }
    }

    public void back(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void removeFilter(String tag){
        if(tag.equals("Clarendon")) removeClarendon();
        if(tag.equals("Gingham")) removeGingham();
        if(tag.equals("rgb hist")) showHist();
        if(tag.equals("Nashville")) addNashville();
    }

    public void addNashville(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        Mat conv = changeChannel(mat, 0, 0, 0, 0, 133,35, false);
        Mat conv2 = contrast_brightness(conv, 1.05f, 0f);
        Mat conv3 = changeChannel(conv2, 0, 88, 13, 0, 0,0, false);
        Mat conv4 = contrast_brightness(conv3, 1.05f, 0f);
        double[] mask = {250,223,182};
        Mat conv5 = apply_mask(conv4, mask, 1, false);

        Utils.matToBitmap(conv5, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void showHist(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        Mat hist = hist(mat);
        Bitmap bmp = Bitmap.createBitmap(hist.cols(), hist.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(hist, bmp);
        imageView.setImageBitmap(bmp);
    }

    public void removeClarendon() {
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Mat conv = hue_saturation(mat, 1f, .9f);
        Mat conv2 = contrast_brightness(conv, .8f, 0f);

        Utils.matToBitmap(conv2, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void removeGingham(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
        double[] mask = {210,210,210};
        Mat conv = apply_mask(mat, mask, 4, true);
        Mat conv2 = contrast_brightness(conv, .8f, 30f);
        Mat conv3 = hue_saturation(conv2, 1f, 1.4f);

        Utils.matToBitmap(conv3, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public static Mat contrast_brightness(Mat image, float a, float b) {
        Mat freshMat = new Mat();
        Mat freshMat2 = new Mat();
        image.convertTo(freshMat, CvType.CV_8UC4, a);

        Scalar scalar = new Scalar(b, b, b, b);
        Core.add(freshMat, scalar, freshMat2);

        return freshMat2;
    }

    public static Mat hue_saturation(Mat image, float a, float b) {
        Mat freshMat = new Mat();
        Imgproc.cvtColor(image, freshMat, Imgproc.COLOR_RGB2HSV);
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(freshMat, channels);
        channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, a);
        channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, b);

        Core.merge(channels, freshMat);
        Imgproc.cvtColor(freshMat, freshMat, Imgproc.COLOR_HSV2RGB);

        return freshMat;
    }

    public static Mat apply_mask(Mat image, double[] mask, int weight, boolean invert) {
        int cols = image.cols();
        int rows = image.rows();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(i, j);
                double[] newPixel = new double[3];
                for(int c = 0; c < 3; c++) {
                    if (invert) {
                        newPixel[c] = (pixel[c] * (weight+1) - mask[c])/weight;
                    } else {
                        newPixel[c] = (mask[c] + pixel[c] * weight) / (weight + 1);
                    }
                }
                image.put(i, j, newPixel);
            }
        }
        return image;
    }

    public Mat hist(Mat img) {
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(img, bgrPlanes);
        int histSize = 256;
        float[] range = {0, 256};
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );
        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);
        for( int i = 1; i < histSize; i++ ) {
            Log.d("BlueValues", "Point:" + (i-1) + "  Val:" + Math.round(bHistData[i-1]));
            Log.d("GreenValues", "Point:" + (i-1) + "  Val:" + Math.round(gHistData[i-1]));
            Log.d("RedValues", "Point:" + (i-1) + "  Val:" + Math.round(rHistData[i-1]));
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }
        return histImage;
    }

    //in rgb form
    public Mat changeChannel(Mat img, double in_r, double in_b, double in_g, double out_r, double out_b, double out_g, boolean flip){
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(img, channels);

        double r_slope = (255 - out_r)/(255 - in_r);
        double r_val = 255 - r_slope*255;
        double g_slope =  (255 - out_g)/(255 - in_g);
        double g_val = 255 - g_slope*255;
        double b_slope =  (255 - out_b)/(255 - in_b);
        double b_val = 255 - b_slope*255;

        for(int i=0; i < img.rows(); i++){
            for(int j=0; j < img.cols(); j++){
                if(flip){
                    channels.get(0).put(i, j, Math.max(0, (channels.get(0).get(i, j)[0]) - r_val)/r_slope);
                    channels.get(1).put(i, j, Math.max(0, (channels.get(1).get(i, j)[0]) - g_val)/g_slope);
                    channels.get(2).put(i, j, Math.max(0, (channels.get(2).get(i, j)[0]) - b_val)/b_slope);
                }
                else{
                    channels.get(0).put(i, j, Math.max(0, r_slope * (channels.get(0).get(i, j)[0]) + r_val));
                    channels.get(1).put(i, j, Math.max(0, g_slope * (channels.get(1).get(i, j)[0]) + g_val));
                    channels.get(2).put(i, j, Math.max(0, b_slope * (channels.get(2).get(i, j)[0]) + b_val));
                }
            }
        }
        Core.merge(channels, img);
        return img;
    }

/*
    public Bitmap process(Bitmap inputImage) {
        rgbKnots = sortPointsOnXAxis(rgbKnots);
        redKnots = sortPointsOnXAxis(redKnots);
        greenKnots = sortPointsOnXAxis(greenKnots);
        blueKnots = sortPointsOnXAxis(blueKnots);
        if (rgb == null) {
            rgb = BezierSpline.curveGenerator(rgbKnots);
        }

        if (r == null) {
            r = BezierSpline.curveGenerator(redKnots);
        }

        if (g == null) {
            g = BezierSpline.curveGenerator(greenKnots);
        }

        if (b == null) {
            b = BezierSpline.curveGenerator(blueKnots);
        }
        return ImageProcessor.applyCurves(rgb, r, g, b, inputImage);
    }

    public Point[] sortPointsOnXAxis(Point[] points) {
        if (points == null) {
            return null;
        }
        for (int s = 1; s < points.length - 1; s++) {
            for (int k = 0; k <= points.length - 2; k++) {
                if (points[k].x > points[k + 1].x) {
                    float temp = 0;
                    temp = points[k].x;
                    points[k].x = points[k + 1].x; //swapping values
                    points[k + 1].x = temp;
                }
            }
        }
        return points;
    }

    public static Mat interpolation(float[] curve, float[] originalValue) {
        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
        for (int i = 0; i < 256; i++) {
            int j = 0;
            float a = i;
            while (a > originalValue[j]) {
                j++;
            }
            if (a == originalValue[j]) {
                lut.put(1, i, curve[j]);
                continue;
            }
            float slope = ((curve[j] - curve[j - 1])) / ((originalValue[j] - originalValue[j - 1]));
            float constant = curve[j] - slope * originalValue[j];
            lut.put(1, i, (slope * a + constant));
        }
        return lut;
    }

    public static Mat SimplestColorBalance(Mat img, int percent) {
        if (percent <= 0)
            percent = 5;
        img.convertTo(img, CvType.CV_32F);
        List<Mat> channels = new ArrayList<>();
        int rows = img.rows(); // number of rows of image
        int cols = img.cols(); // number of columns of image
        int chnls = img.channels(); //  number of channels of image
        double halfPercent = percent / 200.0;
        if (chnls == 3) Core.split(img, channels);
        else channels.add(img);
        List<Mat> results = new ArrayList<>();
        for (int i = 0; i < chnls; i++) {
            // find the low and high precentile values (based on the input percentile)
            Mat flat = new Mat();
            channels.get(i).reshape(1, 1).copyTo(flat);
            Core.sort(flat, flat, Core.SORT_ASCENDING);
            double lowVal = flat.get(0, (int) Math.floor(flat.cols() * halfPercent))[0];
            double topVal = flat.get(0, (int) Math.ceil(flat.cols() * (1.0 - halfPercent)))[0];
            // saturate below the low percentile and above the high percentile
            Mat channel = channels.get(i);
            for (int m = 0; m < rows; m++) {
                for (int n = 0; n < cols; n++) {
                    if (channel.get(m, n)[0] < lowVal) channel.put(m, n, lowVal);
                    if (channel.get(m, n)[0] > topVal) channel.put(m, n, topVal);
                }
            }
            Core.normalize(channel, channel, 0.0, 255.0 / 2, Core.NORM_MINMAX);
            channel.convertTo(channel, CvType.CV_32F);
            results.add(channel);
        }
        Mat outval = new Mat();
        Core.merge(results, outval);
        return outval;
    }
    */
}

