package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

public class LimelightProcessingModule {

    Telemetry telemetry;
    public Limelight3A limelight;

    public LimelightProcessingModule(Limelight3A limelight, Telemetry telemetry)
    {
        this.telemetry = telemetry;
        this.limelight = limelight;
        this.limelight.pipelineSwitch(0);
    }

    public Pose2D limelightResult() {
        LLResult llResult = this.limelight.getLatestResult();

        if (llResult != null && llResult.isValid())
        {
            List<LLResultTypes.FiducialResult> results = llResult.getFiducialResults();
            if (results != null && !results.isEmpty()) {
                Pose3D robot_pose = results.get(0).getRobotPoseTargetSpace();

                double tx = robot_pose.getPosition().toUnit(DistanceUnit.INCH).x;
                double ty = robot_pose.getPosition().toUnit(DistanceUnit.INCH).y;
                double horizontalDist = Math.hypot(tx, ty);
                double rot = robot_pose.getOrientation().getYaw(AngleUnit.DEGREES);

                return new Pose2D(DistanceUnit.INCH, horizontalDist, ty, AngleUnit.DEGREES, rot);
            }
        }
        return null;
    }
}