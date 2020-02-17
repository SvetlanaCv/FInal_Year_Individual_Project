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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;
import android.widget.Button;
import android.os.Environment;

import com.example.myapplication.MainActivity.ActivityOneData;

import java.util.ListIterator;

public class Main2Activity extends AppCompatActivity implements Serializable {

    private static final String TAG = "Main2Activity";

    private Button continueButton;
    private ImageView imageView;
    private TextView constrast;
    private TextView saturation;
    private static TextView results;
    private static String[] stringArray;

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

        imageView = findViewById(R.id.imageView);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput(bitmapName));
            Utils.bitmapToMat(bitmap, mat);
            imageView.setImageBitmap(bitmap);
        }
        catch( FileNotFoundException e){}

        //constrast = findViewById(R.id.textView);
        //saturation = findViewById(R.id.textView2);
        results = findViewById(R.id.textView3);

        //int[] vals = detect_contrast_saturation(mat);

        //constrast.setText("Contrast: " + vals[0]);
        //saturation.setText("Saturation: " + vals[1]);

        LinkedList<String> filterList = new LinkedList<>();

        //populate list
        //if (vals[0] > 0 && vals[1] > 0) {
        filterList.push("Clarendon");
        //filterList.push("Gingham");
        filterList.push("rgb hist");
        filterList.push("Nashville");
        filterList.push("Original");
        //}

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

    public void next_Screen(String[] list, String name){
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("filter list", list);
        intent.putExtra("bitmap name", name);
        startActivity(intent);
    }

    public static void print_results(String[] list){
        String string = "";
        for(int i = 0; i<list.length-1;i++) {
            string += list[i];
            string += ", ";
        }
        string += list[list.length-1];
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