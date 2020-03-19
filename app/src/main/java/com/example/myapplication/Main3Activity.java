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
import java.io.IOException;
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
import org.opencv.core.Size;

import javax.xml.transform.Source;

import java.io.FileOutputStream;
import java.io.File;

public class Main3Activity extends AppCompatActivity {

    private ImageView imageView;
    Bitmap originalBitmap;
    Bitmap currentBitmap;

    Button save;
    Button backButton;

    int n = 0;

    String[] folder = {"Perpetua/", "Crema/", "Gingham/", "Nashville/", "Rise/", "Clarendon/"};
    String[] folderName = {"perp", "crem", "ging", "nash", "rise", "clar"};

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
        if(tag.equals("Clarendon")) removeClarendon(null);
        if(tag.equals("Gingham")) removeGingham(null);
        if(tag.equals("rgb hist")) showHist();
        if(tag.equals("Nashville")) removeNashville(null);
        if(tag.equals("Original")) showOriginal();
        if(tag.equals("Rise")) removeRise(null);
        if(tag.equals("Crema")) removeCrema(null);
        if(tag.equals("Perpetua")) removePerpetua(null);
        if(tag.equals("Check All")) checkAll();
    }

    public void checkAll(){
        for(int j = 0; j < folder.length; j++) {
            for (int i = 1; i <= 50; i++) {
                Bitmap bmp = getImage("/Images/" + folder[j] +  folderName[j] + " (" + i + ")");
                Bitmap converted = removePerpetua(bmp);
                if(j==1) converted = removeCrema(bmp);
                if(j==2) converted = removeGingham(bmp);
                if(j==3) converted = removeNashville(bmp);
                if(j==4) converted = removeRise(bmp);
                if(j==5) converted = removeClarendon(bmp);
                saveImage(converted, "/Reversed/" + folder[j] +  folderName[j] + i + ".jpg");
                Log.d("Reverse", folderName[j] + i);
            }
        }
    }

    private Bitmap getImage(String name){
        String photoPath = this.getExternalFilesDir(null) + name + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        if(bmp==null){
            photoPath = this.getExternalFilesDir(null) + name + ".jpeg";
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(photoPath, options);
        }
        return bmp;
    }

    public String saveImage(Bitmap photo, String name){
        File photoFile = new File(this.getExternalFilesDir(null), name);
        try {
            if (!photoFile.exists()) photoFile.createNewFile();
            FileOutputStream out = new FileOutputStream(photoFile);
            Bitmap bitmap = photo.copy(photo.getConfig(), true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write data.");
        }
        return name;
    }

    public Bitmap removePerpetua(Bitmap bmp){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        /*
        //Imitate
        double[] red = {15, -0.387, 0.023, -0.000116, .00000018};
        double[] green = {15, 0.851, 0.003488, -0.000013, 0};
        double[] blue = {18, 0.49, 0.0051, -.0000133, 0};
        mat = applyCubic(mat, red, green, blue);
         */

        /*
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.perpetua_mask);
        Mat icon_mat = new Mat();
        Utils.bitmapToMat(icon, icon_mat);
        Imgproc.cvtColor(icon_mat, icon_mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
        Imgproc.resize(icon_mat, icon_mat, new Size(mat.width(), mat.height()), 0, 0);
        Core.subtract(mat, icon_mat, mat);

        //Reverse
        double[] red = {0, 0.289, 0.0332, -0.000256, .000000539};
        double[] green = {0, 0.289, 0.0332, -0.000256, .000000539};
        double[] blue = {0, 0.289, 0.0332, -0.000256, .000000539};
        mat = applyCubic(mat, red, green, blue);

         */
        Scalar scalar = new Scalar(0, -10,-10);
        Core.add(mat, scalar, mat);
        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public Bitmap removeCrema(Bitmap bmp){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        /*
        //Imitate
        mat = changeChannel(mat, 0,0,0, 35, 0,0,230, 255,255,255,255,255,false);
        mat = hue_saturation(mat, 1f,0.7f);
        mat = changeChannel(mat, 0,0,0, 0, 0,0,255, 255,255,230,240,240,false);
        */

        //Reverse
        mat = hue_saturation(mat, 1f, 1.3f);
        mat = changeChannel(mat, 0,0,0,0,0,0,255,255,255,230, 230,255, false);

        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public Mat vignette(Mat mat) {
        Mat newMat = new Mat(mat.size(), mat.type(), new Scalar(0,0,0));
        double maxDist = Math.sqrt(Math.pow((0 - newMat.cols() / 2), 2) + Math.pow((0 - newMat.rows() / 2), 2));
        Point point = new Point(newMat.cols() / 2, newMat.rows() / 2);
        for(int i = 0; i < maxDist; i+=2) {
            double val = (60)-i*(60/maxDist);
            Scalar colour = new Scalar(val, val, val);
            for(int j = i; j < i+2; j++) {
                Size size = new Size(maxDist - j, maxDist - j);
                Imgproc.ellipse(newMat,
                        point,
                        size,
                        0.0,
                        0.0,
                        360.0,
                        colour,
                        1,
                        4,
                        0);
            }
        }
        return newMat;
    }

    public void showOriginal(){
        imageView.setImageBitmap(originalBitmap);
        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);;
    }

    public Bitmap removeNashville(Bitmap bmp){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;
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
        mat = equaliseHistManual(mat, 0,0,60,256,240,210);

        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public Mat equalize(Mat img, boolean red, boolean green, boolean blue){
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(img, channels);

        if(red) Imgproc.equalizeHist( channels.get(0), channels.get(0) );
        if(green) Imgproc.equalizeHist( channels.get(1), channels.get(1) );
        if(blue) Imgproc.equalizeHist( channels.get(2), channels.get(2) );

        Core.merge(channels, img);
        return img;
    }

    public void showHist(){
        Bitmap bitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        Mat hist = hist(mat);
        Bitmap bmp = Bitmap.createBitmap(hist.cols(), hist.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(hist, bmp);
        currentBitmap = bmp;
        imageView.setImageBitmap(bmp);
    }

    public Bitmap removeRise(Bitmap bmp) {
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        //Reverse
        //Mat mask = vignette(mat);
        //Core.add(mat, mask, mat);
        mat = equaliseHistManual(mat, 30, 30,30,256,256,256);
        mat = changeChannel(mat, 20,0,0,0,0,0,  255,255,255, 220,220,255, false);

        /*
        //Imitate
        //mat = changeChannel(mat, 0, 0, 0, 60, 16, 12, 222, 237, 220, false);
        Mat mask = vignette(mat);
        Core.subtract(mat, mask, mat);
        */

        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public Bitmap removeClarendon(Bitmap bmp) {
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;

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
        return bitmap;
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

    public Bitmap removeGingham(Bitmap bmp){
        Bitmap bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        if(bmp!=null) bitmap = bmp;
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);

        //Reverse
        HistData data = new HistData(mat);

        double r_in = data.in_rgb[0];
        double g_in = data.in_rgb[1];
        double b_in = data.in_rgb[2];
        double r_out = data.out_rgb[0];
        double g_out = data.out_rgb[1];
        double b_out = data.out_rgb[2];

        if(data.out_rgb[0]<200 || data.out_rgb[1]<200 || data.out_rgb[2]<200) r_out = 200; g_out = 200; b_out = 200;
        if(data.in_rgb[0]>50 || data.in_rgb[1]>50 || data.in_rgb[2]>50) r_in = 30; g_in = 30; b_in = 20;

        mat = equaliseHistManual(mat, r_in, g_in, b_in, r_out, g_out, b_out);
        mat = contrast_brightness(mat, 1, -20f);

        /*
        //Immitate
        Mat conv = changeChannel(mat, 0, 0, 0, 31, 36, 39,200,200,212, false);
        Mat conv2 = contrast_brightness(conv, 1, 30f);
         */

        Utils.matToBitmap(mat, bitmap);
        currentBitmap = bitmap.copy(bitmap.getConfig(), true);
        imageView.setImageBitmap(bitmap);
        return bmp;
    }

    public static Mat contrast_brightness(Mat image, float a, float b) {
        Mat freshMat = new Mat();
        Mat freshMat2 = new Mat();
        image.convertTo(freshMat, CvType.CV_8UC3, a);

        Scalar scalar = new Scalar(b, b, b);
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
        int histW = 1024, histH = 800;
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
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }
        return histImage;
    }

    public Mat equaliseHistManual(Mat img, double red_in, double green_in, double blue_in, double red_out, double green_out, double blue_out){
        double red_low_ratio = 128/(128-red_in);
        double red_high_ratio = 128/(red_out-128);
        double green_low_ratio = 128/(128-green_in);
        double green_high_ratio = 128/(green_out-128);
        double blue_low_ratio = 128/(128-blue_in);
        double blue_high_ratio = 128/(blue_out-128);

        for(int i = 0; i < img.rows(); i++){
            for(int j = 0; j <img.cols(); j++){
                double[] pixel = img.get(i,j);
                double[] newPixel = new double[3];
                if(pixel[0]==200 || pixel[1]==200 || pixel[2]==200){
                    int thing = 0;
                }
                if(pixel[0] < 128){ newPixel[0] = (128-(128 - pixel[0])*red_low_ratio); }
                else if(pixel[0] > 128){ newPixel[0] = (128+(pixel[0]-128)*red_high_ratio); }
                if(pixel[1] < 128){ newPixel[1] = (128-(128 - pixel[1])*green_low_ratio); }
                else if(pixel[1] > 128){ newPixel[1] = (128+(pixel[1]-128)*green_high_ratio); }
                if(pixel[2] < 128){ newPixel[2] = (128-(128 - pixel[2])*blue_low_ratio); }
                else if(pixel[2] > 128){ newPixel[2] = (128+(pixel[2]-128)*blue_high_ratio); }
                img.put(i,j, newPixel);
            }
        }
        return img;
    }

    //in rgb form
    public Mat changeChannel(Mat img, double r_p1_x, double g_p1_x, double b_p1_x, double r_p1_y, double g_p1_y, double b_p1_y, double r_p2_x, double g_p2_x, double b_p2_x, double r_p2_y, double g_p2_y, double b_p2_y, boolean flip){
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(img, channels);

        double r_slope = (r_p2_y - r_p1_y)/(r_p2_x - r_p1_x);
        double r_val = r_p2_y - r_slope*r_p2_x;
        double g_slope =  (g_p2_y - g_p1_y)/(g_p2_x - g_p1_x);
        double g_val = g_p2_y - g_slope*g_p2_x;
        double b_slope =  (b_p2_y - b_p1_y)/(b_p2_x - b_p1_x);
        double b_val = b_p2_y - b_slope*b_p2_x;

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
        File photoFile = new File(this.getExternalFilesDir(null), "Image" + n + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            Bitmap bitmap = currentBitmap;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        n++;
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

