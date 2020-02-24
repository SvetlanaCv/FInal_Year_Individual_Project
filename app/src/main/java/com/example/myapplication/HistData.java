package com.example.myapplication;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HistData {

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

    float[] rHistDataRgb;
    float[] gHistDataRgb;
    float[] bHistDataRgb;

    float[] rHistDataHsv;
    float[] gHistDataHsv;
    float[] bHistDataHsv;

    HistData(Mat img){
        List<Mat> bgrPlanes = new ArrayList<>();

        Mat mat = img.clone();
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
        Core.split(mat, bgrPlanes);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2HSV);
        List<Mat> hsv_planes = new ArrayList<>();
        Core.split(mat, hsv_planes);

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

        rHistDataRgb = new float[(int) (r_hist_rgb.total() * r_hist_rgb.channels())];
        r_hist_rgb.get(0, 0, rHistDataRgb);
        gHistDataRgb = new float[(int) (g_hist_rgb.total() * g_hist_rgb.channels())];
        g_hist_rgb.get(0, 0, gHistDataRgb);
        bHistDataRgb = new float[(int) (b_hist_rgb.total() * b_hist_rgb.channels())];
        b_hist_rgb.get(0, 0, bHistDataRgb);

        rHistDataHsv = new float[(int) (r_hist_hsv.total() * r_hist_hsv.channels())];
        r_hist_hsv.get(0, 0, rHistDataHsv);
        gHistDataHsv = new float[(int) (g_hist_hsv.total() * g_hist_hsv.channels())];
        g_hist_hsv.get(0, 0, gHistDataHsv);
        bHistDataHsv = new float[(int) (b_hist_hsv.total() * b_hist_hsv.channels())];
        b_hist_hsv.get(0, 0, bHistDataHsv);

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
    }
}
