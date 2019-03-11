package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.AbsoluteRotateWithGyro;
import ca.team2706.frc.robot.commands.drivebase.CurvatureDriveWithJoystick;
//import ca.team2706.frc.robot.commands.drivebase.DriverAssistLaser;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision.DriverAssistVisionTarget;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

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
    public OI(Joystick driverStick, Joystick controlStick) {
        // Joystick for driving the robot around
        this.driverStick = driverStick;

        // The Joystick for controlling the mechanisms of the robot
        this.controlStick = controlStick;

        driveCommand = new CurvatureDriveWithJoystick(driverStick, Config.CURVATURE_DRIVE_FORWARD, true,
                Config.CURVATURE_CURVE_SPEED, false, Config.SLOW_MODE);

        // Set subsystem default commands
        DriveBase.getInstance().setDefaultCommand(driveCommand);

        // The button to use to interrupt the robots current command
        new FluidButton(driverStick, Config.INTERRUPT_BUTTON).whenPressed(new InstantCommand(Robot::interruptCurrentCommand));

        // Operator controls
        // Operator controls
        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_CARGO_AND_LOADING_BINDING)
                .whenPressed(new DriverAssistVision(DriverAssistVisionTarget.CARGO_AND_LOADING));
        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_ROCKET_BINDING)
                .whenPressed(new DriverAssistVision(DriverAssistVisionTarget.ROCKET));
        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_BALL_BINDING)
                .whenPressed(new DriverAssistVision(DriverAssistVisionTarget.BALL));
        //new FluidButton(driverStick, Config.DRIVER_ASSIST_LASER_BINDING)
        //        .whenPressed(new DriverAssistLaser());
        new FluidButton(driverStick, Config.FACE_FORWARD_BINDING)
                .whenPressed(new AbsoluteRotateWithGyro(0.6, 90, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_RIGHT_BINDING)
                .whenPressed(new AbsoluteRotateWithGyro(0.6, 0, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_LEFT_BINDING)
                .whenPressed(new AbsoluteRotateWithGyro(0.6, 180, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_BACK_BINDING)
                .whenPressed(new AbsoluteRotateWithGyro(0.6, 270, Integer.MAX_VALUE));
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
