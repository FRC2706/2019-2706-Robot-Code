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
            0, // Bottom for ground pickup
            2.291667, // lowest in feet
            4.625, // med in feet
            6.958333 // highest in feet
    };

    private static final double[] HATCH_SETPOINTS = {
            0, // Loading station pickup // TODO get goodo height for this.
            1.583, // lowest in feet
            3.917, // med in feet
            6.25 // highest in feet
    };

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

        setupTalonConfig();
    }

    /**
     * Sets up the talon configuration.
     */
    private void setupTalonConfig() {
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

        liftMotor.configVoltageCompSaturation(10, Config.CAN_LONG);
        liftMotor.enableVoltageCompensation(true);

        liftMotor.configMotionCruiseVelocity((int) (Config.LIFT_MOTION_MAGIC_VELOCITY.value() / Config.LIFT_ENCODER_DPP / 10), Config.CAN_LONG);
        liftMotor.configMotionAcceleration((int)(Config.LIFT_MOTION_MAGIC_ACCELERATION.value() / Config.LIFT_ENCODER_DPP / 10), Config.CAN_LONG);
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
        liftMotor.set(ControlMode.MotionMagic, position / Config.LIFT_ENCODER_DPP);
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
    public void setPercentOutput(double percentOutput) {
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

    /**
     * Moves the lift to the given setpoint id.
     *
     * @param speed    The speed at which to move the lift.
     * @param setpoint The setpoint id, between 0 and 3.
     */
    public void moveToSetpoint(final double speed, final int setpoint) {
        final double[] currentSetpoints = getCurrentSetpoints();
        if (0 <= setpoint && setpoint <= currentSetpoints.length) {
            setPosition(speed, currentSetpoints[setpoint]);
        }
    }

    /**
     * Determines if the lift has reached the given setpoint.
     * @param setpoint The setpoint.
     * @return True if the lift has reached the setpoint, false otherwise.
     */
    public boolean hasReachedSetpoint(final int setpoint) {
        final double[] currentSetpoints = getCurrentSetpoints();

        boolean hasReachedSetpoint = false;
        if (0 <= setpoint && setpoint <= currentSetpoints.length) {
            hasReachedSetpoint = liftMotor.getSelectedSensorPosition() == currentSetpoints[setpoint] / Config.LIFT_ENCODER_DPP / 10;
        }

        return hasReachedSetpoint;
    }
}