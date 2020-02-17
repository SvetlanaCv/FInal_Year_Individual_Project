package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.graphics.ImageDecoder;

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

import javax.xml.transform.Source;

import java.io.FileOutputStream;
import java.io.File;

public class Main3Activity extends AppCompatActivity {

    private ImageView imageView;
    Bitmap originalBitmap;
    Bitmap currentBitmap;

    Button save;
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

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        try {
            originalBitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Bitmap bitmap = originalBitmap;
            currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
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
        if(tag.equals("Nashville")) removeNashville();
        if(tag.equals("Original")) showOriginal();
    }

    public void showOriginal(){
        imageView.setImageBitmap(originalBitmap);
        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);;
    }

    public void removeNashville(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        /*
        //Imitate
        Mat conv = changeChannel(mat, 0, 0, 0, 0, 133,35, false);
        Mat conv2 = contrast_brightness(conv, 1.2f, 0f);
        Mat conv3 = changeChannel(conv2, 0, 88, 13, 0, 0,0, false);
        double[] mask = {250,223,182};
        Mat conv4 = apply_mask(conv3, mask, 1, false);
        Mat conv5 = changeChannel(conv4, 0, 0, 0, 0, 50,0, false);
         */

        //Reverse
        //Mat conv = changeChannel(mat, 0, 0, 0, 0, 50,0, true);
        double[] mask = {250,223,182};
        Mat conv2 = apply_mask(mat, mask, 1, true);
        Mat conv3 = changeChannel(conv2, 0, 0, 0, 0, 0,0, 207, 207, 182, true);
        //Mat conv4 = contrast_brightness(conv3, .8f, 20f);
        //Mat conv5 = changeChannel(conv4, 0, 0, 0, 0, 133,35, true);


        Utils.matToBitmap(conv3, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
    }

    public void showHist(){
        Bitmap bitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
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
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        //Reverse
        double[] red = {0, 1.2786, -.004691, 0.0000141, 0};
        double[] green = {0.1364955, 1.24768, -0.006457, 0.00002566, -0.0000000164};
        double[] blue = {0, 1.5749, -0.0109, .000034, 0};
        mat = applyCubic(mat, red, green, blue);
        /*
        //Imitate
        double[] red = {0, 0.629, 0.0047, -0.0000127, 0};
        double[] green = {0, -1.03, 0.0423, -0.000244, 0.000000429};
        double[] blue = {0, -0.616, 0.03646, -.0002097, 0.000000359};
        mat = applyCubic(mat, red, green, blue);
         */

        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
    }

    public Mat applyCubic(Mat img, double[] red, double[] green, double[] blue){
        for(int i = 0; i < img.rows(); i++){
            for(int j = 0; j < img.cols(); j++){
                double newRed = red[0] + red[1]*img.get(i,j)[0] + red[2]*img.get(i,j)[0]*img.get(i,j)[0] + red[3]*img.get(i,j)[0]*img.get(i,j)[0]*img.get(i,j)[0] + red[4]*img.get(i,j)[0]*img.get(i,j)[0]*img.get(i,j)[0]*img.get(i,j)[0];
                double newGreen = green[0] + green[1]*img.get(i,j)[1] + green[2]*img.get(i,j)[1]*img.get(i,j)[1] + green[3]*img.get(i,j)[1]*img.get(i,j)[1]*img.get(i,j)[1] + green[4]*img.get(i,j)[1]*img.get(i,j)[1]*img.get(i,j)[1]*img.get(i,j)[1];
                double newBlue = blue[0] + blue[1]*img.get(i,j)[2] + blue[2]*img.get(i,j)[2]*img.get(i,j)[2] + blue[3]*img.get(i,j)[2]*img.get(i,j)[2]*img.get(i,j)[2] + blue[4]*img.get(i,j)[2]*img.get(i,j)[2]*img.get(i,j)[2]*img.get(i,j)[2];
                double[] all = {newRed, newGreen, newBlue};
                img.put(i,j, all);
            }
        }
        return img;
    }

    public void removeGingham(){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        /*
        //Reverse
        double[] mask = {210,210,210};
        Mat conv = apply_mask(mat, mask, 4, true);
        Mat conv2 = contrast_brightness(conv, .8f, 30f);
        Mat conv3 = hue_saturation(conv2, 1f, 1.4f);
        */
        Mat conv = hue_saturation(mat, 1f, 1.3f);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
        Mat conv2 = contrast_brightness(conv, 1.2f, 30f);
        double[] mask = {210,210,210};
        Mat conv3 = apply_mask(conv2, mask, 1,false);

        Utils.matToBitmap(conv3, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
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
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(image, channels);
        if(invert) {
            channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, (255 / mask[0]));
            channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, (255 / mask[1]));
            channels.get(2).convertTo(channels.get(2), CvType.CV_8UC1, (255 / mask[2]));
        }
        else{
            channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, (mask[0]/255));
            channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, (mask[1]/255));
            channels.get(2).convertTo(channels.get(2), CvType.CV_8UC1, (mask[2]/255));
        }
        Core.merge(channels, image);
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
            //Log.d("BlueValues", "Point:" + (i-1) + "  Val:" + Math.round(bHistData[i-1]));
            //Log.d("GreenValues", "Point:" + (i-1) + "  Val:" + Math.round(gHistData[i-1]));
            //Log.d("RedValues", "Point:" + (i-1) + "  Val:" + Math.round(rHistData[i-1]));
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
    public Mat changeChannel(Mat img, double in_r, double in_b, double in_g, double out_r, double out_b, double out_g, double r_y, double g_y, double b_y, boolean flip){
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(img, channels);

        double r_slope = (r_y - out_r)/(255 - in_r);
        double r_val = r_y - r_slope*255;
        double g_slope =  (g_y - out_g)/(255 - in_g);
        double g_val = g_y - g_slope*255;
        double b_slope =  (b_y - out_b)/(255 - in_b);
        double b_val = b_y - b_slope*255;

        if(flip) {
            if (r_val > 0) {
                Scalar scalar = new Scalar(r_val);
                Core.subtract(channels.get(0), scalar, channels.get(0));
                scalar = new Scalar(1 / r_slope);
                Core.multiply(channels.get(0), scalar, channels.get(0));
            } else {
                Scalar scalar = new Scalar(r_slope);
                Core.divide(channels.get(0), scalar, channels.get(0));
                scalar = new Scalar((r_val / r_slope));
                Core.subtract(channels.get(0), scalar, channels.get(0));
            }

            if (g_val > 0) {
                Scalar scalar = new Scalar(g_val);
                Core.subtract(channels.get(1), scalar, channels.get(1));
                scalar = new Scalar(1 / g_slope);
                Core.multiply(channels.get(1), scalar, channels.get(1));
            } else {
                Scalar scalar = new Scalar(g_slope);
                Core.divide(channels.get(1), scalar, channels.get(1));
                scalar = new Scalar((g_val / g_slope));
                Core.subtract(channels.get(1), scalar, channels.get(1));
            }

            if (b_val > 0) {
                Scalar scalar = new Scalar(b_val);
                Core.subtract(channels.get(2), scalar, channels.get(2));
                scalar = new Scalar(1 / b_slope);
                Core.multiply(channels.get(2), scalar, channels.get(2));
            } else {
                Scalar scalar = new Scalar(b_slope);
                Core.divide(channels.get(2), scalar, channels.get(2));
                scalar = new Scalar((b_val / b_slope));
                Core.subtract(channels.get(2), scalar, channels.get(2));
            }
        }
        else{
            //these extensions are untested, may need work
            if(r_slope<1) {
                Scalar scalar = new Scalar(r_slope);
                Core.multiply(channels.get(0), scalar, channels.get(0));
                scalar = new Scalar(r_val);
                Core.add(channels.get(0), scalar, channels.get(0));
            }
            else{
                Scalar scalar = new Scalar(r_slope/2);
                Core.multiply(channels.get(0), scalar, channels.get(0));
                scalar = new Scalar(r_val/2);
                Core.add(channels.get(0), scalar, channels.get(0));
                scalar = new Scalar(2);
                Core.multiply(channels.get(0), scalar, channels.get(0));
            }

            if(g_slope<1) {
                Scalar scalar = new Scalar(g_slope);
                Core.multiply(channels.get(1), scalar, channels.get(1));
                scalar = new Scalar(g_val);
                Core.add(channels.get(1), scalar, channels.get(1));
            }
            else{
                Scalar scalar = new Scalar(g_slope/2);
                Core.multiply(channels.get(1), scalar, channels.get(1));
                scalar = new Scalar(g_val/2);
                Core.add(channels.get(1), scalar, channels.get(1));
                scalar = new Scalar(2);
                Core.multiply(channels.get(1), scalar, channels.get(1));
            }
            if(b_slope<1) {
                Scalar scalar = new Scalar(b_slope);
                Core.multiply(channels.get(2), scalar, channels.get(2));
                scalar = new Scalar(b_val);
                Core.add(channels.get(2), scalar, channels.get(2));
            }
            else{
                Scalar scalar = new Scalar(b_slope/2);
                Core.multiply(channels.get(2), scalar, channels.get(2));
                scalar = new Scalar(b_val/2);
                Core.add(channels.get(2), scalar, channels.get(2));
                scalar = new Scalar(2);
                Core.multiply(channels.get(2), scalar, channels.get(2));
            }
            Core.merge(channels, img);
        }
        return img;
    }

    public void save(){
        File photoFile = new File(this.getExternalFilesDir(null), "Image" + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            Bitmap bitmap = currentBitmap;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

