package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.PneumaticState;
import ca.team2706.frc.robot.commands.climber.actions.MoveClimberPistons;
import ca.team2706.frc.robot.commands.climber.sequences.Climb;
import ca.team2706.frc.robot.commands.drivebase.AbsoluteRotateWithGyro;
import ca.team2706.frc.robot.commands.drivebase.CurvatureDriveWithJoystick;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision;
import ca.team2706.frc.robot.commands.drivebase.DriverAssistVision.DriverAssistVisionTarget;
import ca.team2706.frc.robot.commands.intake.AfterEjectConditional;
import ca.team2706.frc.robot.commands.intake.EjectConditional;
import ca.team2706.frc.robot.commands.intake.arms.LowerArmsSafely;
import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.intake.arms.RaiseArmsSafely;
import ca.team2706.frc.robot.commands.intake.cargo.AutoIntakeCargo;
import ca.team2706.frc.robot.commands.intake.cargo.RunIntakeOnJoystick;
import ca.team2706.frc.robot.commands.lift.*;
import ca.team2706.frc.robot.commands.ringlight.ToggleRingLight;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.input.ETrigger;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.input.FluidTrigger;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

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
    public final Command liftCommand;

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
    public OI(Joystick driverStick, Joystick controlStick) {
        // Joystick for driving the robot around
        this.driverStick = driverStick;

        // The Joystick for controlling the mechanisms of the robot
        this.controlStick = controlStick;

        driveCommand = new CurvatureDriveWithJoystick(driverStick, Config.CURVATURE_DRIVE_FORWARD, true,
                Config.CURVATURE_CURVE_SPEED, false, Config.SLOW_MODE);
        liftCommand = new HoldLift();

        // Set subsystem default commands
        DriveBase.getInstance().setDefaultCommand(driveCommand);
        Lift.getInstance().setDefaultCommand(liftCommand);

        // ---- Operator controls ----
        new FluidButton(controlStick, Config.INTAKE_BACKWARD_BINDING, Config.CONTROLLER_DEADBAND)
                .whenHeld(new RunIntakeOnJoystick(controlStick, Config.INTAKE_BACKWARD_BINDING, false));
        new FluidButton(controlStick, Config.INTAKE_FORWARD_BINDING, Config.CONTROLLER_DEADBAND)
                .whenHeld(new RunIntakeOnJoystick(controlStick, Config.INTAKE_FORWARD_BINDING, true));
        new FluidButton(controlStick, Config.MOVE_LIFT_BINDING, Config.CONTROLLER_DEADBAND)
                .whenHeld(new MoveLiftJoystickVelocity(controlStick, Config.MOVE_LIFT_BINDING, Config.OVERRIDE_LIFT_BINDING));
        new FluidButton(controlStick, Config.LIFT_ARMS_BINDING)
                .whenPressed(new RaiseArmsSafely());
        new FluidButton(controlStick, Config.LOWER_ARMS_BINDING)
                .whenPressed(new LowerArmsSafely());
        new FluidButton(controlStick, Config.CLIMBER_BINDING)
                .whenHeld(new Climb());
        new FluidButton(controlStick, Config.RETRACT_CLIMBER_BINDING)
                .whenPressed(new MoveClimberPistons(() -> PneumaticState.STOWED));
        new ETrigger() {
            @Override
            public boolean get() {
                return FluidTrigger.areAnyActive(controlStick,
                        XboxValue.XBOX_POV_DOWN,
                        XboxValue.XBOX_POV_DOWN_LEFT,
                        XboxValue.XBOX_POV_DOWN_RIGHT,
                        XboxValue.XBOX_POV_LEFT,
                        XboxValue.XBOX_POV_RIGHT,
                        XboxValue.XBOX_POV_UP,
                        XboxValue.XBOX_POV_UP_LEFT,
                        XboxValue.XBOX_POV_UP_RIGHT);
            }
        }.runWhileActive(new MoveLiftToSetpointOnPOV(controlStick));
        new FluidButton(controlStick, Config.MANUAL_PISTON_BINDING)
                .whenPressed(new MovePlunger());
        FluidButton button = new FluidButton(controlStick, Config.EJECT_BINDING);
        LiftPosition position = new LiftPosition();
        button.whenHeld(new EjectConditional(position));
        button.whenReleased(new AfterEjectConditional(position::getPosition));
        new FluidButton(controlStick, Config.TOGGLE_RING_LIGHT_BINDING)
                .whenPressed(new ToggleRingLight());
        new FluidButton(controlStick, Config.SLIGHTLY_LIFT_LIFT_BINDING)
                .whenPressed(new MoveLiftToPosition(0.7, () -> Lift.getInstance().getLiftHeight() + 0.9));

        // ---- Driver controls ----

        // The button to use to interrupt the robots current command
        new FluidButton(driverStick, Config.INTERRUPT_BUTTON)
                .whenPressed(new InstantCommand(Robot::interruptCurrentCommand));
//        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_CARGO_AND_LOADING_BINDING)
//                .whenHeld(new DriverAssistVision(DriverAssistVisionTarget.CARGO_AND_LOADING));
//        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_ROCKET_BINDING)
//                .whenHeld(new DriverAssistVision(DriverAssistVisionTarget.ROCKET));
//        new FluidButton(driverStick, Config.DRIVER_ASSIST_VISION_BALL_BINDING)
//                .whenHeld(new DriverAssistVision(DriverAssistVisionTarget.BALL));
        new FluidButton(driverStick, Config.FACE_FORWARD_BINDING)
                .whenHeld(new AbsoluteRotateWithGyro(0.6, 90, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_RIGHT_BINDING)
                .whenHeld(new AbsoluteRotateWithGyro(0.6, 0, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_LEFT_BINDING)
                .whenHeld(new AbsoluteRotateWithGyro(0.6, 180, Integer.MAX_VALUE));
        new FluidButton(driverStick, Config.FACE_BACK_BINDING)
                .whenHeld(new AbsoluteRotateWithGyro(0.6, 270, Integer.MAX_VALUE));
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
