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

public class GrayProcessor implements VisionProcessor {
    int wh;
    int ht;
    int index;
    Rect roi;
    MatOfPoint largestcontour;
    List<MatOfPoint> contours = new ArrayList<>();
    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // Not useful in this case, but we do need to implement it either way
        wh = width;
        ht = height;
        roi = new Rect(new Point(Math.round(wh/4), Math.round(ht/4)), new Point(Math.round(0.75*wh), Math.round(0.75*ht)));
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Mat roiMat = frame.submat(roi);
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(roiMat, roiMat, Imgproc.COLOR_RGB2HSV);
        Core.inRange(frame, new Scalar(10,130,130), new Scalar(16, 255, 255), frame);
        Core.inRange(roiMat, new Scalar(10,130,130), new Scalar(16, 255, 255), roiMat);
        Mat hierarchy = new Mat();
        Imgproc.findContours(roiMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double area = 0;
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > area) {
                largestcontour = contour;
                area = Imgproc.contourArea(contour);
            }
        }
        index = contours.indexOf(largestcontour);
        return null; // No context object
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
        // Not useful either
        Paint rectangle = new Paint();
        rectangle.setColor(Color.WHITE);
        rectangle.setStyle(Paint.Style.STROKE);
        rectangle.setStrokeWidth(scaleCanvasDensity * 4);
        canvas.drawRect(makeGraphicsRect(roi, scaleBmpPxToCanvasPx), rectangle);
        Rect largestcountourrect = new Rect(new Point(Imgproc.boundingRect(largestcontour).x+ roi.x, Imgproc.boundingRect(largestcontour).y + roi.y), new Point(Imgproc.boundingRect(largestcontour).width + roi.width, Imgproc.boundingRect(largestcontour).height + roi.height));
        canvas.drawRect(makeGraphicsRect(largestcountourrect, scaleBmpPxToCanvasPx), rectangle);
    }
}