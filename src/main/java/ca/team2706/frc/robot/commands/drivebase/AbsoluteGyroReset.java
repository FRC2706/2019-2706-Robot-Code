package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

public class AbsoluteGyroReset extends Command {

    /**
     * Type of target that robot is aligned with (CARGO_AND_LOADING or ROCKET)
     */
    private DriverAssistVisionTarget target;

    /**
     * Creates absolute gyro reset object
     *
     */
    public AbsoluteGyroReset(DriverAssistVisionTarget target) {
        this.target = target;
    }

    @Override
    public void initialize() {
        System.out.println("AGR: initialize() called");
        double angRobotHeadingCurrent_Field = DriveBase.getInstance().getAbsoluteHeading() % 360;
        System.out.println("DAV: angRobotHeadingCurrent_Field: " + angRobotHeadingCurrent_Field);
        double updatedAngle = DriverAssistVision.computeAngRobotHeadingFinal_Field(angRobotHeadingCurrent_Field, target);
        DriveBase.getInstance().resetAbsoluteGyro(updatedAngle);
        System.out.println("DAV: Absolute gyro angle updated to: " + updatedAngle);
    }

    @Override
    public boolean isFinished() {
        return (true);
    }
}

