package com.example.lamanna.opencv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class Processing {
    static Mat cropped;

    public static Mat onCameraFrame(Mat Matinput) {

        Mat grayMat= new Mat();
        Imgproc.cvtColor(Matinput, grayMat, Imgproc.COLOR_BGR2GRAY);
        Mat blurMat = new Mat();
        Imgproc.GaussianBlur(grayMat, blurMat, new Size(5,5), 0);
        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(blurMat, thresh, 255,1,1,11,2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        Imgproc.findContours(thresh, contours, hier, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hier.release();

        MatOfPoint2f biggest = new MatOfPoint2f();
        double max_area = 0;
        for (MatOfPoint i : contours) {
            double area = Imgproc.contourArea(i);
            if (area > 100) {
                MatOfPoint2f m = new MatOfPoint2f(i.toArray());
                double peri = Imgproc.arcLength(m, true);
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(m, approx, 0.02 * peri, true);
                if (area > max_area && approx.total() == 4) {
                    biggest = approx;
                    max_area = area;
                }
            }
        }

        // find the outer box
        Mat displayMat = new Mat();
        Imgproc.cvtColor(Matinput, displayMat, Imgproc.COLOR_BGRA2RGBA);
        Point[] points = biggest.toArray();
        cropped = new Mat();
        int t = 3;
        if (points.length >= 4) {
            // draw the outer box
            Imgproc.line(displayMat, new Point(points[0].x, points[0].y), new Point(points[1].x, points[1].y), new Scalar(255, 0, 0), 2);
            Imgproc.line(displayMat, new Point(points[1].x, points[1].y), new Point(points[2].x, points[2].y), new Scalar(255, 0, 0), 2);
            Imgproc.line(displayMat, new Point(points[2].x, points[2].y), new Point(points[3].x, points[3].y), new Scalar(255, 0, 0), 2);
            Imgproc.line(displayMat, new Point(points[3].x, points[3].y), new Point(points[0].x, points[0].y), new Scalar(255, 0, 0), 2);
            // crop the image
            Rect R = new Rect(new Point(points[0].x - t, points[0].y - t), new Point(points[2].x + t, points[2].y + t));
            if (displayMat.width() > 1 && displayMat.height() > 1) {
                cropped = new Mat(displayMat, R);
            }
        }


//        int SUDOKU_SIZE = 9;
//        int IMAGE_WIDTH = cropped.width();
//        int IMAGE_HEIGHT = cropped.height();
//        double PADDING = IMAGE_WIDTH/25;
//        int HSIZE = IMAGE_HEIGHT/SUDOKU_SIZE;
//        int WSIZE = IMAGE_WIDTH/SUDOKU_SIZE;
//
//        for (int y = 0, iy = 0; y <= IMAGE_HEIGHT - HSIZE ; y+= HSIZE,iy++) {
//            for (int x = 0, ix = 0; x <= IMAGE_WIDTH - WSIZE; x += WSIZE, ix++) {
//                int cx = (x + WSIZE / 2);
//                int cy = (y + HSIZE / 2);
//                Point p1 = new Point(cx - PADDING, cy - PADDING);
//                Point p2 = new Point(cx + PADDING, cy + PADDING);
//                Rect R = new Rect(p1, p2);
//                Mat digit_cropped = new Mat(cropped.clone(), R);
//                Imgproc.GaussianBlur(digit_cropped,digit_cropped,new Size(5,5),0);
//            }
//        }

        return cropped;


    }
}