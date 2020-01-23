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
    static {
        System.loadLibrary("NativeImageProcessor");
    }
    private static final String TAG = "MainActivity";

    public class ImageData implements Serializable{
        byte[] bmpArray;

        ImageData(byte[] bitmap){
            bmpArray = bitmap;
        }
    }

    private Button selectPhoto;
    private Button removeFilter;
    private Button testImage;
    private Button test;
    private ImageView imageView;
    private TextView testResults;
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

        //testResults = findViewById(R.id.textView);

        //imageView = findViewById(R.id.imageView);
        //imageView.setImageResource(R.drawable.capture);

        selectPhoto = findViewById(R.id.button);
        //removeFilter = findViewById(R.id.button2);
        //testImage = findViewById(R.id.button3);
        //test = findViewById(R.id.button4);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(PICK_IMAGE);
            }
        });
        /*
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
        */
    }

    private void test() {
        Mat imageMat = new Mat();
        Mat testMat = new Mat();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        Utils.bitmapToMat(imageBitmap, imageMat);
        Utils.bitmapToMat(testBitmap, testMat);

        int percent = detect_contrast(imageMat);

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

    private void removeFilter() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Mat mat = new Mat();
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);

        Mat conv = hue_saturation(mat, 1f, 1.5f);
        Mat conv2 = brightness_contrast(conv, 1.2f, -30f);

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
    }

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

    public static int detect_contrast(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);
        int rows = image.rows();
        int cols = image.cols();
        double lo_hi = 0;
        double mid = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(i, j);
                if (pixel[2] > 180 || pixel[2] < 100) lo_hi++;
                else mid++;
            }
        }
        double val = (lo_hi) / (lo_hi + mid) * 100;
        return (int) val;
    }

    public static Mat apply_mask(Mat image, double[] mask, int originalWeight) {
        int cols = image.cols();
        int rows = image.rows();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] pixel = image.get(i, j);
                double[] newPixel = {(mask[0] + pixel[0] * originalWeight) / (originalWeight + 1),
                        (mask[1] + pixel[1] * originalWeight) / (originalWeight + 1),
                        (mask[2] + pixel[2] * originalWeight) / (originalWeight + 1)};
                image.put(i, j, newPixel);
            }
        }
        return image;
    }

    public static Mat brightness_contrast(Mat image, float a, float b) {
        Mat freshMat = new Mat();
        Mat freshMat2 = new Mat();
        image.convertTo(freshMat, CvType.CV_8UC4, a);

        Scalar scalar = new Scalar(b, b, b, b);
        Core.add(freshMat, scalar, freshMat2);

        return freshMat2;
    }

    public static Mat hue_saturation(Mat image, float a, float b) {
        Mat freshMat = new Mat();
        Imgproc.cvtColor(image, freshMat, Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(freshMat, freshMat, Imgproc.COLOR_BGR2HSV);
        ArrayList<Mat> channels = new ArrayList<>(3);
        Core.split(freshMat, channels);
        channels.get(0).convertTo(channels.get(0), CvType.CV_8UC1, a);
        channels.get(1).convertTo(channels.get(1), CvType.CV_8UC1, b);

        Core.merge(channels, freshMat);
        Imgproc.cvtColor(freshMat, freshMat, Imgproc.COLOR_HSV2BGR);
        Imgproc.cvtColor(freshMat, freshMat, Imgproc.COLOR_BGR2BGRA);

        return freshMat;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri imageUri = data.getData();
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    Intent intent = new Intent(this, Main2Activity.class);
                    intent.putExtra("image data", byteArray);
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