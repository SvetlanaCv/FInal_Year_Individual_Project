package com.example.myapplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;

import org.opencv.android.Utils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Button;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import android.provider.MediaStore.Images;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.lang.Object;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;
import android.widget.TextView;

import com.zomato.photofilters.geometry.BezierSpline;
import com.zomato.photofilters.geometry.Point;
import com.zomato.photofilters.imageprocessors.ImageProcessor;
import com.zomato.photofilters.imageprocessors.SubFilter;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "MainActivity";

    public class ActivityOneData implements Serializable{
        byte[] bmpArray;

        ActivityOneData(byte[] bitmap){
            bmpArray = bitmap;
        }
    }

    private Button selectPhoto;
    Uri imageUri;
    Bitmap testBitmap;
    private Point[] rgbKnots;
    private Point[] greenKnots;
    private Point[] redKnots;
    private Point[] blueKnots;
    private int[] rgb;
    private int[] r;
    private int[] g;
    private int[] b;

    private static final int PICK_IMAGE = 1;
    private static final int TEST = 2;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
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

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        selectPhoto = findViewById(R.id.button);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(PICK_IMAGE);
            }
        });
    }
    /*
    private void test() {
        Mat imageMat = new Mat();
        Mat testMat = new Mat();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        Utils.bitmapToMat(imageBitmap, imageMat);
        Utils.bitmapToMat(testBitmap, testMat);

        int ImgCols = imageMat.cols();
        int ImgRows = imageMat.rows();
        int TestCols = testMat.cols();
        int TestRows = testMat.rows();
        int rows = 0;
        int cols = 0;
        if (ImgRows > TestRows) {
            Size size = new Size(TestRows, TestCols);
            rows = TestRows;
            cols = TestCols;
            Imgproc.resize(imageMat, imageMat, size);
        } else {
            Size size = new Size(ImgRows, ImgCols);
            rows = ImgRows;
            cols = ImgCols;
            Imgproc.resize(testMat, testMat, size);
        }
        double matching = 0.0;
        double notMatching = 0.0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] imagePixels = imageMat.get(i, j);
                double[] testPixels = testMat.get(i, j);
                if (imagePixels[0] <= testPixels[0] + 25 &&
                        imagePixels[0] >= testPixels[0] - 25 &&
                        imagePixels[1] <= testPixels[1] + 25 &&
                        imagePixels[1] >= testPixels[1] - 25 &&
                        imagePixels[2] <= testPixels[2] + 25 &&
                        imagePixels[2] >= testPixels[2] - 25) {
                    matching++;
                } else notMatching++;
            }
        }
        double total = matching + notMatching;
        double percentage = (int) (matching / total * 100.0);
        testResults.setText(percentage + "%");
    }


    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }*/

    private void openGallery(int num) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, num);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri imageUri = data.getData();
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(this, Main2Activity.class);

                    /*
                    File file = this.getExternalFilesDir("imageBitmap" + ".png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    */
                    String filename = "image";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    FileOutputStream fo = openFileOutput(filename, Context.MODE_PRIVATE);
                    fo.write(bytes.toByteArray());
                    // remember close file output
                    fo.close();

                    intent.putExtra("image name", filename);
                    startActivity(intent);
                }
                catch(IOException e){}

                /*
                int w = bitmap.getWidth();
                int h = bitmap.getWidth();
                if (w > 1000) {
                    w = w / 2;
                    h = h / 2;
                } else if (w > 1500) {
                    w = w / 3;
                    h = h / 3;
                } else if (w > 2000) {
                    w = w / 4;
                    h = h / 4;
                } else if (w > 2500) {
                    w = w / 5;
                    h = h / 5;
                }
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, w, h, false));
                */
            } else if (requestCode == TEST) {
                try {
                    InputStream image_stream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
                    int w = bitmap.getWidth();
                    int h = bitmap.getWidth();
                    if (w > 1000) {
                        w = w / 2;
                        h = h / 2;
                    } else if (w > 1500) {
                        w = w / 3;
                        h = h / 3;
                    } else if (w > 2000) {
                        w = w / 4;
                        h = h / 4;
                    }
                    testBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);

                } catch (FileNotFoundException e) {
                }
            }
        }
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