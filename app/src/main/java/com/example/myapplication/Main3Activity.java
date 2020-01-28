package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.graphics.Color;
import android.view.View;

import com.zomato.photofilters.geometry.BezierSpline;
import com.zomato.photofilters.geometry.Point;
import com.zomato.photofilters.imageprocessors.ImageProcessor;

import org.opencv.android.Utils;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Main3Activity extends AppCompatActivity {

    private ImageView imageView;
    Bitmap originalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = getIntent();
        String[] string = (String[])intent.getSerializableExtra("filter list");
        String bitmapName = (String)intent.getSerializableExtra("bitmap name");

        imageView = findViewById(R.id.imageView2);

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

    public void removeFilter(String tag){
        if(tag.equals("Clarendon")) removeClarendon();
        if(tag.equals("Gingham")) addGingham();
    }

    public void removeClarendon() {
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Mat conv = hue_saturation(mat, 1f, .9f);
        Mat conv2 = contrast_brightness(conv, .8f, 0f);

        /*
        redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(56, 68);
        redKnots[2] = new Point(196, 206);
        redKnots[3] = new Point(255, 255);

        greenKnots = new Point[4];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(46, 77);
        greenKnots[2] = new Point(160, 200);
        greenKnots[3] = new Point(255, 255);

        blueKnots = new Point[4];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(33, 86);
        blueKnots[2] = new Point(126, 220);
        blueKnots[3] = new Point(255, 255);

        rgbKnots = new Point[2];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(255, 255);

        float[] x = {0, 128, 255};
        float[] y = {0, 192, 255};

        Mat a = interpolation(x, y);
        Mat dst = new Mat();
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY);
        Core.LUT(mat, a, dst);

        Imgproc.cvtColor(conv2, conv2, Imgproc.COLOR_RGBA2RGB);
        double[] mask = {164, 158, 158};
        Mat finalMat = apply_mask(conv2, mask, 5);
        */

        Utils.matToBitmap(conv2, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void addGingham(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Mat conv = hue_saturation(mat, 1f, 1.1f);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
        Mat conv2 = contrast_brightness(conv, 1.1f, 10f);
        double[] mask = {210,210,210};
        Mat conv3 = apply_mask(conv2, mask, 4, false);

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

    public static Mat apply_mask(Mat image, double[] mask, int originalWeight, boolean invert) {
        int cols = image.cols();
        int rows = image.rows();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(i, j);
                double[] newPixel = new double[3];
                if(invert){
                    double val = (mask[0] + pixel[0] * originalWeight) / (originalWeight + 1);
                    if(val > pixel[0]) newPixel[0] = pixel[0] - (val - pixel[0]);
                    else newPixel[0] = pixel[0] + (pixel[0] - val);

                    val = (mask[1] + pixel[1] * originalWeight) / (originalWeight + 1);
                    if(val > pixel[1]) newPixel[1] = pixel[1] - (val - pixel[1]);
                    else newPixel[1] = pixel[1] + (pixel[1] - val);

                    val = (mask[2] + pixel[2] * originalWeight) / (originalWeight + 1);
                    if(val > pixel[2]) newPixel[2] = pixel[2] - (val - pixel[2]);
                    else newPixel[2] = pixel[2] + (pixel[2] - val);
                }
                else {
                    newPixel[0] = (mask[0] + pixel[0] * originalWeight) / (originalWeight + 1);
                    newPixel[1] = (mask[1] + pixel[1] * originalWeight) / (originalWeight + 1);
                    newPixel[2] = (mask[2] + pixel[2] * originalWeight) / (originalWeight + 1);
                }
                image.put(i, j, newPixel);
            }
        }
        return image;
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
