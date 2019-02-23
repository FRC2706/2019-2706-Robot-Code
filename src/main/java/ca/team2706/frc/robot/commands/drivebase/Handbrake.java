package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Puts the robot in brake mode when enabled
 */
public class Handbrake extends Command {

    private boolean brake;

    @Override
    public void initialize() {
        if (!DriveBase.getInstance().isBrakeMode()) {
            DriveBase.getInstance().setBrakeMode(true);
            brake = true;
        } else {
            brake = false;
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    public void end() {
        if (brake) {
            DriveBase.getInstance().setBrakeMode(false);
        }
    }
}
