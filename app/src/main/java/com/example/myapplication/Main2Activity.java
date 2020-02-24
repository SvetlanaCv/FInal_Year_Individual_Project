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
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;
import android.widget.Button;

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
        filterList.add("rgb hist");

        imageView = findViewById(R.id.imageView);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Utils.bitmapToMat(bitmap, mat);
            imageView.setImageBitmap(bitmap);
        }
        catch( FileNotFoundException e){}
        results = findViewById(R.id.textView3);

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
        HistData data = new HistData(img);

        //Gingham, RGB
        if (data.r_val_rgb[0] <= 5 && data.r_val_rgb[9] <= 5 && data.g_val_rgb[0] <= 5 && data.g_val_rgb[9] <= 5 && data.b_val_rgb[0] <= 5 && data.b_val_rgb[9] <= 35 && data.r_out_rgb <= 235 && data.b_out_rgb <= 235 && data.g_out_rgb <= 235 && data.r_in_rgb >= 15 && data.g_in_rgb >= 15 && data.b_in_rgb >= 15) filterList.add("Gingham");
        else {
            //Nashville, RGB
            if (data.b_val_rgb[0] < 10 && data.b_val_rgb[1] < 30 && data.b_val_rgb[9] < 10)
                filterList.add("Nashville");

            //Clarendon, RGB & HSV
            if (Math.round(data.rHistDataRgb[255]) < 120 && Math.round(data.gHistDataRgb[0]) < 100
                    && Math.round(data.rHistDataHsv[0]) < 100 && data.b_val_hsv[5] < 50 && data.b_val_hsv[6] < 40 && data.b_val_hsv[7] < 25)
                filterList.add("Clarendon");

            //Perpetua, RGB & HSV
            if (data.g_in_rgb > 5 && Math.round(data.bHistDataRgb[255]) < 100 && Math.round(data.rHistDataRgb[0]) < 5 && Math.round(data.gHistDataRgb[0]) < 5 && Math.round(data.bHistDataRgb[0]) < 300
                    && data.r_in_hsv > 5 && data.g_in_hsv < 10 && Math.round(data.rHistDataHsv[0]) == 0 && Math.round(data.gHistDataHsv[0]) < 150
                    && data.g_val_hsv[8] < 50 && data.g_val_hsv[9] < 40 && data.b_val_hsv[7] < 40 && data.b_val_hsv[6] < 45 && data.b_val_hsv[5] < 35)
                filterList.add("Perpetua");

            //Crema, HSV & RGB
            if (data.g_val_hsv[9] <= 30 && data.g_val_hsv[8] <= 30 && data.g_val_hsv[7] < 100 && Math.round(data.rHistDataHsv[0]) < 5 && Math.round(data.gHistDataHsv[0]) < 400 && Math.round(data.rHistDataHsv[255]) < 100 && Math.round(data.gHistDataHsv[255]) < 100
                    && Math.round(data.gHistDataRgb[255]) < 50 && Math.round(data.bHistDataRgb[255]) <= 1)
                filterList.add("Crema");

            //Rise, HSV && RGB
            if (Math.round(data.rHistDataHsv[0]) < 5 && Math.round(data.gHistDataHsv[0]) < 200 && Math.round(data.gHistDataHsv[255]) < 50 && data.r_val_hsv[0] < 10 && data.r_in_hsv > 5 && data.g_val_hsv[8] < 40 && data.g_val_hsv[9] <= 5
                    && Math.round(data.rHistDataRgb[0]) <= 0 && Math.round(data.bHistDataRgb[0]) < 50)
                filterList.add("Rise");

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