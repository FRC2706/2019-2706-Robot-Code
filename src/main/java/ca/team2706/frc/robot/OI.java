package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.ArcadeDriveWithJoystick;
import ca.team2706.frc.robot.commands.intake.ExhaleCargo;
import ca.team2706.frc.robot.commands.intake.InhaleCargo;
import ca.team2706.frc.robot.commands.lift.MoveLiftOnJoystickPID;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

/**
 * This class is the glue that binds the controls on the physical operator interface to the commands
 * and command groups that allow control of the robot.
 */
public class OI {
    /**
     * Joystick for driving the robot around
     */
    private final Joystick driverStick;

    /**
     * Joystick for controlling the mechanisms of the robot
     */
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

    /**
     * Initializes a new instance of the OI class.
     */
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

        driveCommand = new ArcadeDriveWithJoystick(driverStick, Config.ARCADE_DRIVE_FORWARD, true,
                Config.ARCADE_DRIVE_ROTATE, false);
        // Set subsystem default commands
        DriveBase.getInstance().setDefaultCommand(driveCommand);

        new FluidButton(controlStick, Config.INTAKE_BINDING)
                .whileHeld(new InhaleCargo(driverStick, FluidButton.getPort(Config.INTAKE_BINDING).getPort()));
        new FluidButton(controlStick, Config.EXHALE_BINDING)
                .whileHeld(new ExhaleCargo(controlStick, FluidButton.getPort(Config.EXHALE_BINDING).getPort()));
        new FluidButton(controlStick, Config.MOVE_LIFT_BINDING)
                .whileHeld(new MoveLiftOnJoystickPID(controlStick, FluidButton.getPort(Config.MOVE_LIFT_BINDING).getPort()));
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
