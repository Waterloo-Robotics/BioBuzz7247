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
    IMU imu;
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

        // IMU - FIELD CENTRIC STUFF!! //
        imu = hardwareMap.get(IMU.class, "imu");

        // Configure the sensor
        configurePinpoint();

        // Set the location of the robot - this should be the place you are starting the robot from
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        // NOTE: If the logo of control hub that says "Control Hub" is facing right ex: robot is facing forward and its on its right side. . then tell the firectiong its facing right.
//        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
//        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;

        // Defining stuff frfr.. I think? - Brayyten
//        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);
        motorBR.setDirection(DcMotor.Direction.FORWARD);

        // flywheel.setDirection(DcMotor.Direction.FORWARD);
        // transfer.setDirection(DcMotor.Direction.FORWARD);
        // intake.setDirection(DcMotor.Direction.FORWARD);

        // Initializing the IMU's orientation..
        // Note: if you choose two conflicting directions, this initialization will cause a code exception.
//        imu.initialize(new IMU.Parameters(orientationOnRobot));
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

        displayIMU();

        //Auto Druve vase!
        auto_drivebase();

        // telememtery
        telemetry.addData("X coordinate (IN)", pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y coordinate (IN)", pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading angle (DEGREES)", pose2D.getHeading(AngleUnit.DEGREES));
    }

    public void displayIMU()
    {
        {
            //telemetry.addData("Hub orientation", "Logo=%s   USB=%s\n ", logoDirection, usbDirection);

            // Check to see if heading reset is requested
            if (gamepad1.options) {
                telemetry.addData("Yaw", "Resetting\n");
                imu.resetYaw();
            } else {
                telemetry.addData("Yaw", "Press options (menu) on Gamepad to reset\n");
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);

            // Retrieve Rotational Angles and Velocities for IMU
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);

            // field centric
            yaw = orientation.getYaw(AngleUnit.RADIANS);

            // IMU Telemetry
            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
            telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
            telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
            telemetry.addData("Yaw (Z) velocity", "%.2f Deg/Sec", angularVelocity.zRotationRate);
            telemetry.addData("Pitch (X) velocity", "%.2f Deg/Sec", angularVelocity.xRotationRate);
            telemetry.addData("Roll (Y) velocity", "%.2f Deg/Sec", angularVelocity.yRotationRate);
            telemetry.update();
        }
    }
    public void configurePinpoint(){

        pinpoint.setOffsets(-84.0, -168.0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();
    }

    public void auto_drivebase(){
        pinpoint.update();

        double robotXDestination = 1;
        double robotYDestination = 10;


        Pose2D pose2D = pinpoint.getPosition();
        double robotXPosition = pose2D.getX(DistanceUnit.INCH);
        double robotYPosition = pose2D.getY(DistanceUnit.INCH);

        // Get error
        double robotPinpointXError = robotXDestination - robotXPosition;
        double robotPinpointYError = robotYDestination - robotYPosition;

        double drive_Input = gamepad1.left_stick_y;
        double strafe_Input = -gamepad1.left_stick_x;
        double driveTurn = gamepad1.right_stick_x;

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
        telemetry.addData("X Error", robotPinpointXError);
        telemetry.addData("Y Error", robotPinpointYError);

        if (robotPinpointXError > 0.5 && robotPinpointYError > 0.5);
        {
            stop();
        }
    }
}
