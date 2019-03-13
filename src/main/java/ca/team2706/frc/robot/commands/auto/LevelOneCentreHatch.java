package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.commands.drivebase.DriveForwardWithTime;
import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.commands.drivebase.MotionMagicVision;
import ca.team2706.frc.robot.commands.drivebase.StraightDriveGyro;
import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class LevelOneCentreHatch extends CommandGroup {

    public LevelOneCentreHatch() {
        // 14.9 - robot_length (38 inches)
        addSequential(new PrintCommand("Level One Centre auto"));
        addSequential(new MotionMagic(() -> 1.0, () -> 12.5, () -> 10, () -> -10.0 * OI.getInstance().getDriverStick().getRawAxis(XboxValue.XBOX_RIGHT_STICK_X.getPort())), 4);
        addSequential(new PlaceHatchAuto());
        addSequential(new StraightDriveGyro(1, -2, 10));
    }
}
