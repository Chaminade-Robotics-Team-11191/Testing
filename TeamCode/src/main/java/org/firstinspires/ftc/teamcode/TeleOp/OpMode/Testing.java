package org.firstinspires.ftc.teamcode.TeleOp.OpMode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.TeleOp.Pipeline.ringDetection;
import org.firstinspires.ftc.vision.VisionPortal;

public class Testing extends LinearOpMode {
    VisionPortal visionPortal;
    ringDetection ring;
    @Override
    public void runOpMode() throws InterruptedException {
        visionPortal = new VisionPortal.Builder()
                .addProcessor(ring)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCamera(BuiltinCameraDirection.FRONT)
                .build();
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            telemetry.update();
        }
        visionPortal.close();
    }
}