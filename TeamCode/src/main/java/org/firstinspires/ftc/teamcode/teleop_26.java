package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.LimelightProcessingModule;
import org.firstinspires.ftc.teamcode.Table2D;

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
    private Limelight3A limelight;
    private LimelightProcessingModule llModule;

    boolean intakeOn = false;
    boolean transferOn = false;
    boolean flywheelOn = false;
    boolean autoTargeting = true;
    private double flywheelPresetPower;
    private double currentFlywheelPower;
    private float[] distance = {22, 30, 35, 40, 44, 52, 56, 69, 81, 125, 126};
    private float[] flywheelPower = {0.45f, 0.48f, 0.50f, 0.52f, 0.54f, 0.57f, 0.60f, 0.65f, 0.70f, 0.85f, 0.85f};
    private Table2D flywheel_speed_table = new Table2D(distance, flywheelPower);


    // IMU Stuff
    IMU imu;
    private double yaw;

    @Override
    public void init() {

        flywheelPresetPower = 0.53;
        currentFlywheelPower = flywheelPresetPower;
        telemetry.addData("Status", "Initialized");

        // Wheel Hardware Map
        motorFR = hardwareMap.get(DcMotor.class, "motorFR");
        motorFL = hardwareMap.get(DcMotor.class, "motorFL");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");

        // Robot Components Hardware Map
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        intake = hardwareMap.get(DcMotor.class, "intake");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        llModule = new LimelightProcessingModule(limelight, telemetry);
        limelight.start();

        // IMU - FIELD CENTRIC STUFF!! //
        imu = hardwareMap.get(IMU.class, "imu");


        // NOTE: If the logo of control hub that says "Control Hub" is facing right ex: robot is facing forward and its on its right side. . then tell the firectiong its facing right.
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;

        // Defining stuff frfr.. I think? - Brayyten
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Initializing the IMU's orientation..
        // Note: if you choose two conflicting directions, this initialization will cause a code exception.
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        motorFL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);
        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorBR.setDirection(DcMotor.Direction.FORWARD);

        flywheel.setDirection(DcMotor.Direction.REVERSE);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
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

        if (gamepad1.bWasPressed() || gamepad2.bWasPressed()) {
            autoTargeting = !autoTargeting;
        }

        Pose2D pose = llModule.limelightResult();
        float limelight_distance = 0;

        if (autoTargeting && pose != null) {
            limelight_distance = (float) -pose.getX(DistanceUnit.INCH);
            currentFlywheelPower = Range.clip(flywheel_speed_table.Lookup(limelight_distance), 0.0, 1.0);
        } else if (!autoTargeting) {
            currentFlywheelPower = flywheelPresetPower;
        }

        telemetry.addData("Auto Targeting", autoTargeting);
        telemetry.addData("Limelight Distance", limelight_distance);
        telemetry.addData("Flywheel Power", currentFlywheelPower);

        // call upon imu
        displayIMU();

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


    }

    public void flywheel()
    {
//        flywheelOn = gamepad1.right_bumper;

        // Flywheel on
        if(flywheelOn){
            flywheel.setPower(currentFlywheelPower);
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
            yaw = -orientation.getYaw(AngleUnit.RADIANS);

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

}