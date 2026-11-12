
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

// IMU Imports
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;


@TeleOp(name="teleop_26", group="Iterative OpMode")
public class teleop_26 extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor motorFL = null;
    private DcMotor motorFR = null;
    private DcMotor motorBL = null;
    private DcMotor motorBR = null;

    // Robot components
    private DcMotor flywheel;
    private DcMotor transfer;
    private DcMotor intake;

    boolean intakeOn = false;
    boolean transferOn = false;
    boolean flywheelOn = false;
    private double flywheelPower;

    // IMU Stuff
//    IMU imu;


    // PP stuff
    GoBildaPinpointDriver pinpoint;

    private double yaw;

    @Override
    public void init() {
        flywheelPower = 0.53;
        telemetry.addData("Status", "Initialized");

        // Wheel Hardware Map
        motorFR = hardwareMap.get(DcMotor.class, "motorFR");
        motorFL = hardwareMap.get(DcMotor.class, "motorFL");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");

        // Robot Components Hardware Map
        flywheel = hardwareMap.get(DcMotor.class, "flywheelMotor");
        transfer = hardwareMap.get(DcMotor.class, "transferMotor");
        intake = hardwareMap.get(DcMotor.class, "intakeMotor");

        // Pinpoint IMU!
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // IMU - FIELD CENTRIC STUFF!! //
//        imu = hardwareMap.get(IMU.class, "imu");



        // Configure the PP
        configurePinpoint();


        if(gamepad1.options){
            // You could use readings from April Tags here to give a new known position to the pinpoint
            pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
        }
        pinpoint.update();
        Pose2D pose2D = pinpoint.getPosition();


//        // NOTE: If the logo of control hub that says "Control Hub" is facing right ex: robot is facing forward and its on its right side. . then tell the firectiong its facing right.
//        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
//        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;
//
//        // Defining stuff frfr.. I think? - Brayyten
//        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Initializing the IMU's orientation..
        // Note: if you choose two conflicting directions, this initialization will cause a code exception.
//        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);
        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorBR.setDirection(DcMotor.Direction.FORWARD);

        flywheel.setDirection(DcMotor.Direction.REVERSE);


        // Set the location of the robot - this should be the place you are starting the robot from
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");

        // PP Telemetry
        telemetry.addLine("Press option (Menu) button to reset the position");
        telemetry.addData("X coordinate (IN)", pose2D.getX(DistanceUnit.INCH));
        telemetry.addData("Y coordinate (IN)", pose2D.getY(DistanceUnit.INCH));
        telemetry.addData("Heading angle (DEGREES)", pose2D.getHeading(AngleUnit.DEGREES));
    }


    // Code to run REPEATEDLY after the driver hits INIT, but before they hit START
    @Override
    public void init_loop() {

    }

    // * Code to run ONCE when the driver hits START
    @Override
    public void start() {
        runtime.reset();
    }


    // * Code to run REPEATEDLY after the driver hits START but before they hit STOP
    // Stuff below is how you call on stuff!!
    @Override
    public void loop() {

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double yPower = gamepad1.left_stick_y;
        double turnPower = gamepad1.right_stick_x;
        double xPower = gamepad1.left_stick_x;

        // Field Centric stuff
        //double x_Mod = yPower * Math.cos(yaw) + xPower * Math.sin(yaw);
        //double y_Mod = xPower * Math.cos(yaw) - yPower * Math.sin(yaw);

        double x_Mod = -(xPower * Math.cos(yaw) + yPower * Math.sin(yaw));
        double y_Mod = -xPower * Math.sin(yaw) + yPower * Math.cos(yaw);

        // Setup a variable for each drive wheel to save power level for telemetry
        //double leftPower;
        //double rightPower;

        // Send calculated power to wheels
        motorFR.setPower(y_Mod - turnPower - x_Mod);
        motorBR.setPower(y_Mod - turnPower + x_Mod);
        motorFL.setPower(y_Mod + turnPower - x_Mod);
        motorBL.setPower(y_Mod + turnPower + x_Mod);
//        motorFR.setPower(yPower - turnPower - xPower);
//        motorBR.setPower(yPower - turnPower + xPower);
//        motorFL.setPower(yPower + turnPower + xPower);
//        motorBL.setPower(yPower + turnPower - xPower);

        // call upon imu
//        displayIMU();

        // call upon robot components
        flywheel();
        intake();
        transfer();

        // Transfer activate
        if (gamepad1.left_trigger > 0.5 || gamepad2.left_trigger > 0.5)
        {
            transferOn = true;
        } else {
            transferOn= false;
        }

        if (gamepad1.right_trigger > 0.5 || gamepad2.left_trigger > 0.5)
        {
            intakeOn = !intakeOn;
        } else {
            intakeOn = false;
        }

        if (gamepad1.rightBumperWasReleased() || gamepad2.rightBumperWasReleased()) {
            flywheelOn = !flywheelOn;
        }

        if (gamepad1.dpadDownWasPressed()) {
            flywheelPower = .60;
        }
        if (gamepad1.dpadUpWasPressed()) {
            flywheelPower = 0.55;
        }
    }

    public void flywheel()
    {
//        flywheelOn = gamepad1.right_bumper;

        // Flywheel on
        if(flywheelOn){
            flywheel.setPower(flywheelPower);
        }
        else{
            flywheel.setPower(0);
        }

        // Intake activate
    }

    public void intake() //Intake Function
    {
//        intakeOn = gamepad1.left_bumper;
        if(intakeOn){
            intake.setPower(1);
        }
        else {
            intake.setPower(0);
        }
    }
    public void transfer()
    {
        if(transferOn) {
            transfer.setPower(1);
        } else {
            transfer.setPower(0);
        }
    }

    public void configurePinpoint(){

        pinpoint.setOffsets(-84.0, -168.0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        // X and Y positions
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();

//         Retrieve Rotational Angles and Velocities for IMU
            YawPitchRollAngles orientation = pinpoint.getRobotYawPitchRollAngles();
            AngularVelocity angularVelocity = pinpoint.getRobotAngularVelocity(AngleUnit.DEGREES);

            // field centric
            yaw = -orientation.getYaw(AngleUnit.RADIANS);

            // IMU Telemetry
            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
            telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
            telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
            telemetry.update();
    }



    // This code uses IMU from the Control Hub
//    public void displayIMU()
//    {
//        {
//            //telemetry.addData("Hub orientation", "Logo=%s   USB=%s\n ", logoDirection, usbDirection);
//
//            // Check to see if heading reset is requested
//            if (gamepad1.options) {
//                telemetry.addData("Yaw", "Resetting\n");
//                imu.resetYaw();
//            } else {
//                telemetry.addData("Yaw", "Press options (menu) on Gamepad to reset\n");
//            }
//
//            // Show the elapsed game time and wheel power.
//            telemetry.addData("Status", "Run Time: " + runtime.toString());
//            //telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
//
//            // Retrieve Rotational Angles and Velocities for IMU
//            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
//            AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);
//
//            // field centric
//            yaw = -orientation.getYaw(AngleUnit.RADIANS);
//
//            // IMU Telemetry
//            telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
//            telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
//            telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
//            telemetry.addData("Yaw (Z) velocity", "%.2f Deg/Sec", angularVelocity.zRotationRate);
//            telemetry.addData("Pitch (X) velocity", "%.2f Deg/Sec", angularVelocity.xRotationRate);
//            telemetry.addData("Roll (Y) velocity", "%.2f Deg/Sec", angularVelocity.yRotationRate);
//            telemetry.update();
//        }
//    }

}
