package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.CurvatureDriveWithJoystick;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * This class is the glue that binds the controls on the physical operator interface to the commands
 * and command groups that allow control of the robot.
 */
public class OI {
    // Joystick for driving the robot around
    private final Joystick driverStick;

    // Joystick for controlling the mechanisms of the robot
    private final Joystick controlStick;

    /**
     * Current instance of the OI class.
     */
    private static OI currentInstance;

    public final Command driveCommand;

    /**
     * Gets the current instance of the OI class.
     *
     * @return The current instance of OI.
     */
    public static OI getInstance() {
        init();
        return currentInstance;
    }

    public static void init() {
        if (currentInstance == null) {
            currentInstance = new OI();
        }
    }

    /**
     * Initializes Oi using the two default real joysticks
     */
    private OI() {
        this(new Joystick(0), new Joystick(1));
    }

    /**
     * Initializes Oi with non-default joysticks
     *
     * @param driverStick  The driver joystick to use
     * @param controlStick The operator joystick to use
     */
    private OI(Joystick driverStick, Joystick controlStick) {
        // Joystick for driving the robot around
        this.driverStick = driverStick;

        // The Joystick for controlling the mechanisms of the robot
        this.controlStick = controlStick;

        driveCommand = new CurvatureDriveWithJoystick(driverStick, Config.CURVATURE_DRIVE_FORWARD, true,
                Config.CURVATURE_CURVE_SPEED, false, Config.SLOW_MODE);

        // Set subsystem default commands
        DriveBase.getInstance().setDefaultCommand(driveCommand);
    }

    /**
     * Gets the driver joystick.
     *
     * @return The driver joystick.
     */
    public Joystick getDriverStick() {
        return driverStick;
    }

    /**
     * Gets the operator joystick.
     *
     * @return The operator joystick.
     */
    public Joystick getControlStick() {
        return controlStick;
    }
}
