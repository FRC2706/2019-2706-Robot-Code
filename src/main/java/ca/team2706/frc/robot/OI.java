package ca.team2706.frc.robot;

import java.lang.reflect.Field;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.Config.XBOX_VALUE;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.PrintCommand;



/**
 * This class is the glue that binds the controls on the physical operator interface to the commands
 * and command groups that allow control of the robot.
 */
// Operator Interface
public class OI {

    

    // Joystick for driving the robot around
    private final Joystick driverStick;

    // Joystick for controlling the mechanisms of the robot
    private final Joystick controlStick;

    public Joystick getDriverJoystick() {
        return driverStick;
    }

    public Joystick getOperatorJoystick() {
        return controlStick;
    }

    /**
     * Initializes Oi using the two default real joysticks
     */
    private OI() {
        this(new Joystick(0), new Joystick(1));
    }

    // The current instance of the OI object
    private static OI currentInstance;

    public static OI getInstance() {
        if (currentInstance == null) {
            init();
        }

        return currentInstance;
    }

    /**
     * Initializes a new OI object.
     */
    public static void init() {
        currentInstance = new OI();
    }

    

    /**
     * Initializes Oi with non-default joysticks
     * 
     * @param driverStick The driver joystick to use
     * @param controlStick The operator joystick to use
     */
    private OI(Joystick driverStick, Joystick controlStick) {
        // Joystick for driving the robot around
        this.driverStick = driverStick;

        // The Joystick for controlling the mechanisms of the robot
        this.controlStick = controlStick;

        JoystickButton joystickButton = new FluidJoystickButton(driverStick, Config.testAction);

        PrintCommand printCommand = new PrintCommand("Command!");

        joystickButton.whenPressed(printCommand);

        joystickButton.close();

        

    }

} 