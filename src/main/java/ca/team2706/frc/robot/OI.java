package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.CurvatureDriveWithJoystick;
import ca.team2706.frc.robot.commands.intake.EjectConditional;
import ca.team2706.frc.robot.commands.intake.arms.LowerArmsSafely;
import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.intake.arms.RaiseArms;
import ca.team2706.frc.robot.commands.intake.cargo.RunIntakeOnJoystick;
import ca.team2706.frc.robot.commands.lift.MoveLiftOnJoystick;
import ca.team2706.frc.robot.commands.lift.MoveLiftOnOverride;
import ca.team2706.frc.robot.commands.lift.MoveLiftToSetpoints;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
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

        driveCommand = new CurvatureDriveWithJoystick(driverStick, Config.CURVATURE_DRIVE_FORWARD, true,
                Config.CURVATURE_CURVE_SPEED, false, Config.SLOW_MODE);

        // Set subsystem default commands
        DriveBase.getInstance().setDefaultCommand(driveCommand);

        // Operator controls.
        new FluidButton(controlStick, Config.INTAKE_BACKWARD_BINDING)
                .whileHeld(new RunIntakeOnJoystick(controlStick, Config.INTAKE_BACKWARD_BINDING, false));
        new FluidButton(controlStick, Config.INTAKE_FORWARD_BINDING)
                .whileHeld(new RunIntakeOnJoystick(controlStick, Config.INTAKE_FORWARD_BINDING, true));
        new FluidButton(controlStick, Config.MOVE_LIFT_BINDING)
                .whileHeld(new MoveLiftOnJoystick(controlStick, Config.MOVE_LIFT_BINDING));
        new FluidButton(controlStick, Config.LIFT_ARMS_BINDING)
                .whenPressed(new RaiseArms());
        new FluidButton(controlStick, Config.LOWER_ARMS_BINDING)
                .whenPressed(new LowerArmsSafely());
        new FluidButton(controlStick, Config.OVERRIDE_LIFT_DOWN_BINDING)
                .whileHeld(new MoveLiftOnOverride(false));
        new FluidButton(controlStick, Config.OVERRIDE_LIFT_UP_BINDING)
                .whileHeld(new MoveLiftOnOverride(true));
        new FluidButton(controlStick, Config.LIFT_FIRST_SETPOINT_BINDING)
                .whileHeld(new MoveLiftToSetpoints(0));
        new FluidButton(controlStick, Config.LIFT_SECOND_SETPOINT_BINDING)
                .whileHeld(new MoveLiftToSetpoints(1));
        new FluidButton(controlStick, Config.LIFT_THIRD_SETPOINT_BINDING)
                .whileHeld(new MoveLiftToSetpoints(2));
        new FluidButton(controlStick, Config.LIFT_FOURTH_SETPOINT_BINDING)
                .whileHeld(new MoveLiftToSetpoints(3));
        new FluidButton(controlStick, Config.MANUAL_PISTON_BINDING)
                .whenPressed(new MovePlunger(!Intake.getInstance().isPlungerStowed()));
        new FluidButton(controlStick, Config.EJECT_BINDING)
                .whileHeld(new EjectConditional());
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
