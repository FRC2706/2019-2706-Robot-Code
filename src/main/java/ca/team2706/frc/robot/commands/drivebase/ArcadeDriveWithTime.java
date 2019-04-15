package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Drives in a straight line for an amount of time
 */
public class ArcadeDriveWithTime extends TimedCommand {

    final double forward;
    private final double rotationSpeed;

    /**
     * Creates a drive forward with time command
     *
     * @param timeout       Makes it stop the command after a time in seconds
     * @param forward       The speed for the robot to drive
     * @param rotationSpeed
     */
    public ArcadeDriveWithTime(double timeout, double forward, double rotationSpeed) {
        super(timeout, DriveBase.getInstance());
        this.forward = forward;
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setOpenLoopVoltageMode();
        DriveBase.getInstance().setBrakeMode(true);
    }

    @Override
    public void execute() {
        DriveBase.getInstance().arcadeDrive(forward, rotationSpeed, false);
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}
