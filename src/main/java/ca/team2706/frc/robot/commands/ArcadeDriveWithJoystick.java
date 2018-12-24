package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Drives the robot using joystick axes for driving forward and rotation
 */
public class ArcadeDriveWithJoystick extends ArcadeDrive {

    /**
     * Drives using axes that are on separate joysticks
     *
     * @param joy1 The joystick that the first axis is on
     * @param axis1 The first axis
     * @param joy2 The joystick that the second axis is on
     * @param axis2 The second axis
     */
    public ArcadeDriveWithJoystick(Joystick joy1, int axis1, Joystick joy2, int axis2) {
        super(() -> joy1.getRawAxis(axis1), () -> joy2.getRawAxis(axis2),
                Config.TELEOP_SQUARE_JOYSTICK_INPUTS, Config.TELEOP_BRAKE);
    }

    /**
     * Drives using axes that are on the same joystick
     *
     * @param joy The joystick with the axes
     * @param axis1 The first axis
     * @param axis2 The second axis
     */
    public ArcadeDriveWithJoystick(Joystick joy, int axis1, int axis2) {
        this(joy, axis1, joy, axis2);
    }

    @Override
    public boolean isFinished() {
        // The command should only finish when cancelled from somewhere else
        return false;
    }
}
