package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.operatorFeedback.rumbler.BasicRumble;
import ca.team2706.frc.robot.operatorFeedback.rumbler.Rumbler;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Kyle Anderson
 */
public class OI {

    private final Joystick driverJoystick;

    /**
     * Gets the driver joystick.
     * @return The driver Joystick object.
     */
    public Joystick getDriverJoystick() {
        return driverJoystick;
    }

    /**
     * Gets the operator joystick.
     * @return The operator Joystick object.
     */
    public Joystick getOperatorJoystick() {
        return operatorJoystick;
    }

    private final Joystick operatorJoystick;

    /**
     * Constructs a new OI object with the default joysticks.
     */
    public OI() {
        this(new Joystick(0), new Joystick(1));
    }

    private static OI currentInstance;

    /**
     * Constructs a new OI object with the given joysticks.
     * @param driverJoystick The driver joystick.
     * @param operatorJoystick The operator joystick object.
     */
    public OI(Joystick driverJoystick, Joystick operatorJoystick) {
        this.driverJoystick = driverJoystick;
        this.operatorJoystick = operatorJoystick;
    }

    public static OI getInstance() {
        if (currentInstance == null) {
            currentInstance = new OI();
        }
        return currentInstance;
    }
}
