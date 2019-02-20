package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Allows the robot to drive using tank controls
 */
public class TankDriveWithJoystick extends TankDrive {

    public TankDriveWithJoystick(Joystick joy1, int axis1, boolean invert1,
                                   Joystick joy2, int axis2, boolean invert2) {
        super(() -> sign(joy1.getRawAxis(axis1), invert1), () -> sign(joy2.getRawAxis(axis2), invert2),
                Config.TELEOP_SQUARE_JOYSTICK_INPUTS, Config.TELEOP_BRAKE);


    }

    public TankDriveWithJoystick(Joystick joy, int axis1, boolean invert1, int axis2, boolean invert2) {
        this(joy, axis1, invert1, joy, axis2, invert2);
    }

    @Override
    public boolean isFinished() {
        // The command should only finish when cancelled from somewhere else
        return false;
    }
    /**
     * Conditionally negates a number with a boolean
     *
     * @param number The number to negate
     * @param sign   True to negate the number
     * @return The negated number
     */
    private static double sign(double number, boolean sign) {
        if (sign) {
            return -number;
        } else {
            return number;
        }
    }
}
