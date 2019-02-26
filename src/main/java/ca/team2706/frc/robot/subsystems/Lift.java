package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem that controls the elevator on the robot
 */
public class Lift extends Subsystem {

    /**
     * The lift motor controller.
     */
    public WPI_TalonSRX liftMotor;

    /**
     * Limit switch used to zero the lift when in the furthest downwards position.
     */
    public DigitalInput liftLimitSwitch;

    private static final double[] CARGO_SETPOINTS = {
            2.291667, // lowest in feet
            4.625, // med in feet
            6.958333 // highest in feet
    };

    private static final double[] HATCH_SETPOINTS = {
            1.583, // lowest in feet
            3.917, // med in feet
            6.25 // highest in feet
    };

    private static final double[] CARGO_LOWER_SETPOINTS;

    static {
        CARGO_LOWER_SETPOINTS = new double[HATCH_SETPOINTS.length];

        for (int i = 0; i < HATCH_SETPOINTS.length; i++) {
            CARGO_LOWER_SETPOINTS[i] = HATCH_SETPOINTS[i];
        }
    }

    private final double MAX_HEIGHT = CARGO_SETPOINTS[2]; // TODO we don't want to set this as the topmost height, we want it to be the actual lift's limit.

    private double heightGoal = 1.0;

    private boolean loweringForHatch = false; //if the lift is lowering in order to deploy a hatch

    private static Lift currentInstance;

    /**
     * Initialises a new Elevator object
     *
     * @return the new Elevator instance
     */
    public static Lift getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new Lift instance, if there isn't an instance already.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new Lift();
        }
    }

    /**
     * Initializes a new lift object.
     */
    private Lift() {
        liftMotor = new WPI_TalonSRX(Config.LIFT_MOTOR_ID);
        liftLimitSwitch = new DigitalInput(Config.LIFT_DIGITAL_INPUT_ID);
        liftMotor.setNeutralMode(NeutralMode.Brake);

        liftMotor.enableCurrentLimit(Config.ENABLE_LIFT_CURRENT_LIMIT);

        resetTalonConfiguration();
    }

    /**
     * Sets up the talon configuration.
     */
    private void resetTalonConfiguration() {
        liftMotor.configFactoryDefault(Config.CAN_LONG);
        liftMotor.configPeakCurrentLimit(2, Config.CAN_LONG);
        liftMotor.setInverted(Config.INVERT_LIFT_MOTOR);
    }

    @Override
    public void initDefaultCommand() { }

    /**
     * Updates the setpoint for the PID controller
     *
     * @param speed The speed at which the motor should move, from 0 to 1.
     * @param setpoint The setpoint location in feet.
     */
    public void setDesiredSetpoint(final double speed, final double setpoint) {
        liftMotor.configClosedLoopPeakOutput(0, speed);
        liftMotor.set(ControlMode.Position, setpoint / Config.LIFT_ENCODER_DPP);
    }

    /**
     * Determines what setpoints we're currently going to be using.
     * @return The current setpoints, sorted from lowest to highest.
     */
    private double[] getCurrentSetpoints() {
        final double[] setpoints;

        switch (getIntakeMode()) {
            case CARGO:
                setpoints = CARGO_SETPOINTS;
                break;
            case HATCH:
                setpoints = HATCH_SETPOINTS;
                break;
            default:
                setpoints = new double[0];
                break;
        }

        return setpoints;
    }

    /**
     * Stopping the lift
     */
    public void stop() {
        if (!loweringForHatch) {
            liftMotor.set(0);
        } else {
            Intake.getInstance().retractPlunger(); // Moving the plunger in.
            Intake.getInstance().raiseIntake();
            loweringForHatch = false;
        }
    }

    /**
     * Gets the current intake mode.
     * @return The intake mode
     */
    private static Intake.IntakeMode getIntakeMode() {
        return Intake.getInstance().getMode();
    }
}