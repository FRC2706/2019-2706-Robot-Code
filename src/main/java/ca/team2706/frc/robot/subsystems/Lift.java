package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.*;
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
            CARGO_LOWER_SETPOINTS[i] = HATCH_SETPOINTS[i] - 0.5;
        }
    }

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

        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_LONG);
        liftMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.CTRE_MagEncoder_Relative, Config.CAN_LONG);
        liftMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_LONG);
        liftMotor.setSensorPhase(Config.ENABLE_LIFT_SUM_PHASE.value());
        liftMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Config.CAN_LONG);
        liftMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Config.CAN_LONG);
        liftMotor.configNeutralDeadband(Config.LIFT_CLOSED_LOOP_DEADBAND.value());

        liftMotor.config_kP(0, Config.LIFT_P.value());
        liftMotor.config_kP(0, Config.LIFT_I.value());
        liftMotor.config_kP(0, Config.LIFT_D.value());

        liftMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);

        liftMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Config.CAN_LONG);
        liftMotor.configClearPositionOnLimitR(true, Config.CAN_LONG);

        enableLimit(true);

        // Set up the soft limit of encoder ticks.
        liftMotor.configForwardSoftLimitThreshold(Config.MAX_LIFT_ENCODER_TICKS, Config.CAN_LONG);
        liftMotor.configReverseSoftLimitThreshold(0, Config.CAN_LONG);
    }

    /**
     * Enables the limit on the lift so the lift won't go past certain points on the lift.
     *
     * @param enable True to enable, false otherwise.
     */
    private void enableLimit(final boolean enable) {
        liftMotor.configForwardSoftLimitEnable(enable);
        liftMotor.configReverseSoftLimitEnable(enable);
    }

    @Override
    public void initDefaultCommand() {
    }

    @Override
    public void periodic() {
        super.periodic();
        if (liftMotor.getSelectedSensorPosition() < 0) {
            zeroEncoderTicks();
        }
    }

    /**
     * Zeros the lift encoder ticks.
     */
    private void zeroEncoderTicks() {
        liftMotor.setSelectedSensorPosition(0);
    }

    /**
     * Determines what setpoints we're currently going to be using.
     *
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
     * Sets the position for the talons to reach.
     *
     * @param maxSpeed Maximum speed at which to travel, from 0 to 1.
     * @param position The position, in feet.
     */
    public void setPosition(final double maxSpeed, final double position) {
        enableLimit(true);
        liftMotor.configClosedLoopPeakOutput(0, maxSpeed);
        liftMotor.set(ControlMode.PercentOutput, position / Config.LIFT_ENCODER_DPP);
    }

    /**
     * Sets a velocity for the talon motors.
     *
     * @param velocity Velocity in ft/s
     */
    public void setVelocity(final double velocity) {
        enableLimit(true);
        liftMotor.configClosedLoopPeakOutput(0, 1.0); // Peak output to max (1.0).
        liftMotor.set(ControlMode.Velocity, velocity / Config.LIFT_ENCODER_DPP / 10.0);
    }

    /**
     * Sets the percent output for the talon lift motor.
     *
     * @param percentOutput Percent output, between -1 and 1
     */
    public void setPercentOuput(double percentOutput) {
        enableLimit(true);
        liftMotor.set(percentOutput);
    }

    /**
     * Overrides limits on the talons to go up with override.
     */
    public void overrideUp() {
        liftMotor.set(Config.LIFT_OVERRIDE_UP_SPEED);
    }

    public void overrideDown() {
        if (!liftLimitSwitch.get()) {
            enableLimit(false);
            liftMotor.set(-Config.LIFT_OVERRIDE_DOWN_SPEED);
        }
    }

    /**
     * Stopping the lift
     */
    public void stop() {
        liftMotor.set(0);
    }

    /**
     * Gets the current intake mode.
     *
     * @return The intake mode
     */
    private static Intake.IntakeMode getIntakeMode() {
        return Intake.getInstance().getMode();
    }
}