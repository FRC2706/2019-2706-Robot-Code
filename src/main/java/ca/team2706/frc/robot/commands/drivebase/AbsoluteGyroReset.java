package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Resets the absolute gyro heading of the robot to a target
 */
public class AbsoluteGyroReset extends InstantCommand {

    /**
     * Type of target that robot is aligned with (CARGO_AND_LOADING or ROCKET)
     */
    private DriverAssistVisionTarget target;

    /**
     * Creates absolute gyro reset object
     *
     * @param target The type of target that the robot is aligned with
     */
    public AbsoluteGyroReset(DriverAssistVisionTarget target) {
        this.target = target;
    }

    @Override
    public void initialize() {
        Log.d("AGR: initialize() called");
        double angRobotHeadingCurrent_Field = DriveBase.getInstance().getAbsoluteHeading() % 360;
        if (angRobotHeadingCurrent_Field < 0.0) {
            angRobotHeadingCurrent_Field += 360.0;
        }
        Log.d("AGR: angRobotHeadingCurrent_Field: " + angRobotHeadingCurrent_Field);
        double updatedAngle = DriverAssistVision.computeAngRobotHeadingFinal_Field(angRobotHeadingCurrent_Field, target);
        DriveBase.getInstance().resetAbsoluteGyro(updatedAngle);
        Log.d("AGR: Absolute gyro angle updated to: " + updatedAngle);
    }
}

