package com.example.myapplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "MainActivity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    private Button               selectPhoto;
    private Button               removeFilter;
    private Button               testImage;
    private Button               test;
    private ImageView            imageView;
    private TextView             testResults;
    Uri                          imageUri;
    Uri                          testUri;

    private static final int     PICK_IMAGE = 1;
    private static final int     TEST = 2;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                    //mOpenCvCameraView.setOnTouchListener(MainActivity.this);
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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        testResults = findViewById(R.id.textView);

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.capture);

        selectPhoto = findViewById(R.id.button);
        removeFilter = findViewById(R.id.button2);
        testImage = findViewById(R.id.button3);
        test = findViewById(R.id.button4);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(PICK_IMAGE);
            }
        });
        removeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFilter();
            }
        });
        testImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(TEST);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test();
            }
        });
    }

    private void test(){
        Mat imageMat = new Mat();
        Mat testMat = new Mat();
        try {
            InputStream image_stream = this.getContentResolver().openInputStream(testUri);
            Bitmap testBitmap = BitmapFactory.decodeStream(image_stream);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap imageBitmap = drawable.getBitmap();
            Utils.bitmapToMat(imageBitmap, imageMat);
            Utils.bitmapToMat(testBitmap, testMat);
        }
        catch( IOException e ){ }
        int columns = imageMat.cols();
        int rows = imageMat.rows();
        double matching = 0.0;
        double notMatching = 0.0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                double[] imagePixels = imageMat.get(i,j);
                double[] testPixels = testMat.get(i,j);
                if(imagePixels[0] <= testPixels[0]+25 &&
                        imagePixels[0] >= testPixels[0]-25 &&
                        imagePixels[1] <= testPixels[1]+25 &&
                        imagePixels[1] >= testPixels[1]-25 &&
                        imagePixels[2] <= testPixels[2]+25 &&
                        imagePixels[2] >= testPixels[2]-25){
                    matching++;
                }
                else notMatching++;
            }
        }
        double total = matching + notMatching;
        double percentage = (int) (matching/total * 100.0);
        testResults.setText(percentage + "%");
    }

    private void removeFilter(){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);

        Mat conv = hue_saturation(mat, 1f, 1.25f);
        Mat conv2 = brightness_contrast(conv, 1.2f, -30f);

        //add cyan
        Mat finalImg = new Mat();
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(conv2, channels);
        channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, 1.25);
        channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, 1.25);
        Core.merge(channels, finalImg);

        Utils.matToBitmap(finalImg, bitmap);
        createImageFromBitmap(bitmap);
    }

    public static Mat brightness_contrast(Mat image, float a, float b){
        Mat freshMat = new Mat();
        Mat freshMat2 = new Mat();
        image.convertTo(freshMat, CvType.CV_8UC4, a);

        Scalar scalar = new Scalar(b,b,b,b);
        Core.add(freshMat, scalar, freshMat2);

        return freshMat2;
    }

    public static Mat hue_saturation(Mat image, float a, float b){
        Mat freshMat = new Mat();
        Imgproc.cvtColor(image,freshMat,Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(freshMat,freshMat,Imgproc.COLOR_BGR2HSV);
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(freshMat, channels);
        channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, a);
        channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, b);

        Core.merge(channels, freshMat);
        Imgproc.cvtColor(freshMat,freshMat,Imgproc.COLOR_HSV2BGR);
        Imgproc.cvtColor(freshMat,freshMat,Imgproc.COLOR_BGR2BGRA);

        return freshMat;
    }

    public static Mat interpolation(float[] curve, float[] originalValue){
        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
        for(int i=0; i<256; i++){
            int j=0;
            float a = i;
            while (a>originalValue[j]){
                j++;
            }
            if(a == originalValue[j]){
                lut.put(1, i, curve[j]);
                continue;
            }
            float slope = ((curve[j] - curve[j-1]))/((originalValue[j] - originalValue[j-1]));
            float constant = curve[j] - slope * originalValue[j];
            lut.put(1,i, (slope * a + constant));
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
    }


    private void openGallery(int num) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, num);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
            else if(requestCode == TEST){
                testUri = data.getData();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}