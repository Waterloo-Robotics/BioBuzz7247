package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Autonomous(name="Auto_2026_2027", group="Robot")
public class Auto_2026_2027 extends OpMode {

    //---Declare OpMode members.---//
    private DcMotor motorFR = null;
    private DcMotor motorFL = null;
    private DcMotor motorBR = null;
    private DcMotor motorBL = null;
    private DcMotor flywheel;
    private DcMotor transfer;
    private DcMotor intake;
    // IMU Stuff

    private double yaw;
    private ElapsedTime runtime = new ElapsedTime();

    GoBildaPinpointDriver pinpoint;

    @Override
    public void init()
    {
        // Get a reference to the sensor
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        motorFR = hardwareMap.get(DcMotor.class, "motorFR");
        motorFL = hardwareMap.get(DcMotor.class, "motorFL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // Configure the sensor
        configurePinpoint();

        // Set the location of the robot - this should be the place you are starting the robot from
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);
        motorBR.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void loop()
    {
        if(gamepad1.a){
            // You could use readings from April Tags here to give a new known position to the pinpoint
            pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        }
        pinpoint.update();
        Pose2D pose2D = pinpoint.getPosition();

        yaw = - pose2D.getHeading(AngleUnit.DEGREES);

        double robotXDestination = 1;
        double robotYDestination = 10;
        double robotYawDesired = 0;

        double robotXPosition = pose2D.getX(DistanceUnit.INCH);
        double robotYPosition = pose2D.getY(DistanceUnit.INCH);

        // Get error
        double robotPinpointXError = Math.max (0.5, robotXDestination - robotXPosition);
        double robotPinpointYError = Math.max (0.5, robotYDestination - robotYPosition);
        double robotYawError = robotYawDesired - pose2D.getHeading(AngleUnit.DEGREES);

        //Auto Druve vase!
        auto_drivebase(robotPinpointXError, robotPinpointYError, robotYawError);

        // telememtery
        telemetry.addData("X coordinate (IN)", pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y coordinate (IN)", pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading angle (DEGREES)", pose2D.getHeading(AngleUnit.DEGREES));
    }

    public void configurePinpoint(){

        pinpoint.setOffsets(-84.0, -168.0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();
    }

    public void auto_drivebase(double drive_Input, double strafe_Input, double driveTurn){

        double theta = Math.atan2(drive_Input, strafe_Input);
        double r = Math.hypot(strafe_Input, drive_Input);

        theta = AngleUnit.normalizeRadians(theta - yaw);

        double drive_Mod = r * Math.sin(theta);
        double strafe_Mod = r * Math.cos(theta);

        //Translate inputs to outputs for motors
        motorBL.setPower (drive_Mod + driveTurn - strafe_Mod);
        motorBR.setPower (drive_Mod - driveTurn + strafe_Mod);
        motorFL.setPower (drive_Mod + driveTurn + strafe_Mod);
        motorFR.setPower (drive_Mod - driveTurn - strafe_Mod);

        // Telemetry
        telemetry.addData("X Error", drive_Input);
        telemetry.addData("Y Error", strafe_Input);

        if (drive_Input < 0.5 && strafe_Input < 0.5);
        {
            stop();
        }
    }
}
