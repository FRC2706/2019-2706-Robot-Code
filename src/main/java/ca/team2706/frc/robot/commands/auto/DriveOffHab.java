package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.drivebase.DriveForwardWithTime;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveOffHab extends CommandGroup {

    public DriveOffHab() {
        addSequential(new DriveForwardWithTime(2.5, 0.4));
    }
}
