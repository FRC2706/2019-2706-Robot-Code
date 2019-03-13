package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.drivebase.DriveForwardWithTime;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Drives off the hab platform
 */
public class DriveOffHab extends CommandGroup {

    /**
     * Creates command to drive off the hab platform
     */
    public DriveOffHab() {
        addSequential(new DriveForwardWithTime(2.5, 0.4));
    }
}
