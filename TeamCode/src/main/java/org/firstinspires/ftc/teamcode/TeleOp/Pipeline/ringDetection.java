package org.firstinspires.ftc.teamcode.TeleOp.Pipeline;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ringDetection implements VisionProcessor {
    int wh, ht, index;
    Rect ROI;
    MatOfPoint largestContour;
    List<MatOfPoint> contours = new ArrayList<>();
    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        wh = width;
        ht = height;
        ROI = new Rect(new Point(Math.round((float) wh /4), Math.round((float) ht /4)), new Point(Math.round(0.75*wh), Math.round(0.75*ht)));
    }
    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        detectLargestContours(isolateImage(frame, new Scalar(7,100,100), new Scalar(16, 255, 255), false));
        return null;
    }

    private void detectLargestContours(Mat isolateMat) {
        Mat hierarchy = new Mat();
        contours.clear();
        largestContour = null;
        Imgproc.findContours(isolateMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double area = 10;
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > area) {
                largestContour = contour;
                area = Imgproc.contourArea(contour);
            }
        }
    }
    private Mat isolateImage(Mat frame, Scalar lowerBound, Scalar upperBound, boolean hsv_frame) {
        Mat isolateMat = frame.submat(ROI);
        Imgproc.cvtColor(isolateMat, isolateMat, Imgproc.COLOR_RGB2HSV);
        Core.inRange(isolateMat, lowerBound, upperBound, isolateMat);
        if (hsv_frame) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);
            Core.inRange(frame, lowerBound, upperBound, frame);
        }
        return isolateMat;
    }
    private android.graphics.Rect makeGraphicsRect(Rect rect, float scaleBmpPxToCanvasPx) {
        int left = Math.round(rect.x * scaleBmpPxToCanvasPx);
        int top = Math.round(rect.y * scaleBmpPxToCanvasPx);
        int right = left + Math.round(rect.width * scaleBmpPxToCanvasPx);
        int bottom = top + Math.round(rect.height * scaleBmpPxToCanvasPx);
        return new android.graphics.Rect(left, top, right, bottom);
    }
    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Paint rectangle = new Paint();
        rectangle.setColor(Color.WHITE);
        rectangle.setStyle(Paint.Style.STROKE);
        rectangle.setStrokeWidth(scaleCanvasDensity * 2);
        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(15);
        if (largestContour != null) {
            Rect largestcountourrect = Imgproc.boundingRect(largestContour);
            largestcountourrect.x += ROI.x;
            largestcountourrect.y += ROI.y;
            canvas.drawRect(makeGraphicsRect(largestcountourrect, scaleBmpPxToCanvasPx), rectangle);
            canvas.drawText("Ring Detected", Math.round((largestcountourrect.x + (float) largestcountourrect.width /2) * scaleBmpPxToCanvasPx) - 40, Math.round((largestcountourrect.y + (float) largestcountourrect.height /2) * scaleBmpPxToCanvasPx), text);
            canvas.drawRect(makeGraphicsRect(ROI, scaleBmpPxToCanvasPx), rectangle);
        } else {
            rectangle.setColor(Color.RED);
            canvas.drawRect(makeGraphicsRect(ROI, scaleBmpPxToCanvasPx), rectangle);
        }
    }
}