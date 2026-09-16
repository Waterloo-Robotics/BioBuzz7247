package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="pomegranate", group="LinearOpMode")
public class pomegranate extends OpMode {
    public DcMotor dragonfruit;
    public Servo guava;
    public DcMotor guputen;
    public Servo kidzbob;
public void init() {
    dragonfruit = hardwareMap.get(DcMotor.class, "dragonfruit");
    guava = hardwareMap.get(Servo.class, "guava");
    guputen = hardwareMap.get(DcMotor.class, "guputen");
    kidzbob = hardwareMap.get(Servo.class, "kidzbob");
}


public void loop() {


    if (gamepad1.a) {
        guputen.setPower(1);
    }

    if (gamepad1.b) {
        guputen.setPower(0);
    }
    if (gamepad1.leftBumperWasPressed()) {
        dragonfruit.setPower(1);
    }
    if (gamepad1.rightBumperWasPressed()) {
        dragonfruit.setPower(0);
    }
    if (gamepad1.y) {
        guava.setPosition(1);
    }
    if(gamepad1.x) {
        guava.setPosition(0);
    }

    if (gamepad1.dpad_up) {
        kidzbob.setPosition(1);
    }

    if (gamepad1.dpad_down) {
        kidzbob.setPosition(0);
    }




}

}
