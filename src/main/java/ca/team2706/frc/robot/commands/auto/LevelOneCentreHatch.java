package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.commands.drivebase.StraightDriveGyro;
import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Drives off level one and places a hatch
 */
public class LevelOneCentreHatch extends CommandGroup {

    /**
     * Creates command to drive off level one hab and place hatch on cargo ship
     */
    public LevelOneCentreHatch() {
        // 14.9 - robot_length (38 inches)
        addSequential(new MotionMagic(() -> 1.0, () -> 12.5, () -> 10, () -> -10.0 * OI.getInstance().getDriverStick().getRawAxis(XboxValue.XBOX_RIGHT_STICK_X.getPort())), 4);
        addSequential(new PlaceHatchAuto());
        addSequential(new StraightDriveGyro(1, -2, 10));
    }
}
