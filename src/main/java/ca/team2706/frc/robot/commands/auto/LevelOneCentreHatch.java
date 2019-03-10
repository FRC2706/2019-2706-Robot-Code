package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.commands.drivebase.StraightDriveGyro;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class LevelOneCentreHatch extends CommandGroup {

    public LevelOneCentreHatch() {
        addSequential(new MotionMagic(1.0, 11, 10));
        addSequential(new PlaceHatchAuto());
        addSequential(new StraightDriveGyro(1, -2, 10));
    }
}
