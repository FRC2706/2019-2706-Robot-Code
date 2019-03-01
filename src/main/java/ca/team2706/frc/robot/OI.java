package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.ArcadeDriveWithJoystick;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision;
import ca.team2706.frc.robot.commands.drivebase.TankDrive;
import ca.team2706.frc.robot.commands.drivebase.TankDriveWithJoystick;
import ca.team2706.frc.robot.commands.drivebase.CurvatureDriveWithJoystick;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.input.FluidButton;
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

    public final FluidButton buttonDriverAssistVisionCargoAndLoading;
    public final FluidButton buttonDriverAssistVisionRocket;
    public final FluidButton buttonDriverAssistLaser;

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
     public OI(Joystick driverStick, Joystick controlStick) {
        // Joystick for driving the robot around
        this.driverStick = driverStick;

        // The Joystick for controlling the mechanisms of the robot
        this.controlStick = controlStick;

        driveCommand = new CurvatureDriveWithJoystick(driverStick, Config.CURVATURE_DRIVE_FORWARD, true,
                Config.CURVATURE_CURVE_SPEED, false, Config.SLOW_MODE);

        buttonDriverAssistVisionCargoAndLoading = new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_CARGO_AND_LOADING);
        buttonDriverAssistVisionCargoAndLoading.whenPressed(new DriverAssistVision(true, false));
        buttonDriverAssistVisionRocket = new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_ROCKET);
        buttonDriverAssistVisionRocket.whenPressed(new DriverAssistVision(false, true));
        buttonDriverAssistLaser = new FluidButton(driverStick, Config.DRIVER_ASSIST_LASER);

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

    /**
     * Gets a boolean indicating if the DriverAssistVisionCargoAndLoading button is pressed.
     *
     * @return True if the DriverAssistVisionCargoAndLoading button is pressed, false otherwise.
     */
    public boolean getButtonDriverAssistVisionCargoAndLoading() {
        return buttonDriverAssistVisionCargoAndLoading.get();
    }

    /**
     * Gets a boolean indicating if the DriverAssistLaser button is pressed.
     *
     * @return True if the DriverAssistLaser button is pressed, false otherwise.
     */
    public boolean getButtonDriverAssistLaser() {
        return buttonDriverAssistLaser.get();
    }

    /**
     * Gets a boolean indicating if the DriverAssistVisionRocket button is pressed.
     *
     * @return True if the DriverAssistVisionRocket button is pressed, false otherwise.
     */
    public boolean getButtonDriverAssistVisionRocket() {
        return buttonDriverAssistVisionRocket.get();
    }
}
