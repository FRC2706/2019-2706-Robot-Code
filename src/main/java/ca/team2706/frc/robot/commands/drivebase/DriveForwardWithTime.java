package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Drives in a straight line for an amount of time
 */
public class DriveForwardWithTime extends TimedCommand {

    final double speed;

    /**
     * Creates a drive forward with time command
     *
     * @param timeout Makes it stop the command after a time in seconds
     * @param speed The speed for the robot to drive
     */
    public DriveForwardWithTime(double timeout, double speed) {
        super(timeout, DriveBase.getInstance());
        this.speed = speed;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setOpenLoopVoltageMode();
        DriveBase.getInstance().setBrakeMode(true);
    }

    @Override
    public void execute() {
        DriveBase.getInstance().arcadeDrive(speed, 0, false);
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}
