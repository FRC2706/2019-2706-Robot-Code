package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
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
     * Setpoints for cargo, in encoder ticks.
     */
    private static final int[] CARGO_SETPOINTS = {
            0, // Cargo ground pickup
            6500, // lowest cargo
            34311, // med cargo
            Config.MAX_LIFT_ENCODER_TICKS // highest cargo.
    };

    /**
     * Setpoints for hatches, in encoder ticks.
     */
    private static final int[] HATCH_SETPOINTS = {
            0, // Loading station pickup
            499, // lowest hatch deploy
            27138, // middle hatch
            Config.MAX_LIFT_ENCODER_TICKS // highest hatch
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
        setupTalonConfig();
    }

    @Override
    protected void initDefaultCommand() {
    }

    /**
     * Sets up the talon configuration.
     */
    private void setupTalonConfig() {
        liftMotor.setNeutralMode(NeutralMode.Brake);
        liftMotor.configFactoryDefault(Config.CAN_LONG);
        liftMotor.setInverted(Config.INVERT_LIFT_MOTOR);

        liftMotor.configPeakCurrentLimit(Config.MAX_LIFT_CURRENT, Config.CAN_LONG);
        liftMotor.configContinuousCurrentLimit(Config.CONTINUOUS_CURRENT_LIMIT);
        liftMotor.configPeakCurrentDuration(Config.CURRENT_LIMIT_THRESHOLD_MS);
        liftMotor.enableCurrentLimit(Config.ENABLE_LIFT_CURRENT_LIMIT);

        liftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_LONG);
        liftMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_LONG);
        liftMotor.setSensorPhase(Config.ENABLE_LIFT_SUM_PHASE.value());
        liftMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Config.CAN_LONG);
        liftMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Config.CAN_LONG);
        liftMotor.configNeutralDeadband(Config.LIFT_CLOSED_LOOP_DEADBAND.value());

        liftMotor.config_kP(0, Config.LIFT_P.value());
        liftMotor.config_kI(0, Config.LIFT_I.value());
        liftMotor.config_kD(0, Config.LIFT_D.value());

        liftMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);

        liftMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Config.CAN_LONG);
        enableLimitSwitch(true);

        enableLimit(true);

        // Set up the soft limit of encoder ticks.
        liftMotor.configForwardSoftLimitThreshold(Config.MAX_LIFT_ENCODER_TICKS, Config.CAN_LONG);
        liftMotor.configReverseSoftLimitThreshold(0, Config.CAN_LONG);

        liftMotor.configVoltageCompSaturation(12, Config.CAN_LONG);
        liftMotor.enableVoltageCompensation(true);

        liftMotor.configMotionCruiseVelocity((int) (Config.LIFT_MOTION_MAGIC_VELOCITY.value() / Config.LIFT_ENCODER_DPP / 10), Config.CAN_LONG);
        liftMotor.configMotionAcceleration((int) (Config.LIFT_MOTION_MAGIC_ACCELERATION.value() / Config.LIFT_ENCODER_DPP / 10), Config.CAN_LONG);

        liftMotor.configOpenloopRamp(Config.LIFT_VOLTAGE_RAMP_UP_PERIOD, Config.CAN_LONG);
    }

    private void enableLimitSwitch(final boolean enable) {
        liftMotor.configClearPositionOnLimitR(enable, Config.CAN_SHORT);
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
    public void periodic() {
        super.periodic();
        if (liftMotor.getSelectedSensorPosition() < 0) {
            zeroEncoderTicks();
        }

        if (DriverStation.getInstance().isEnabled()) {
            Log.d("Lift Encoders " + liftMotor.getSelectedSensorPosition());
            Log.d("Lift Rev Switch " + liftMotor.getSensorCollection().isRevLimitSwitchClosed());
            Log.d("Lift Fwd Switch " + liftMotor.getSensorCollection().isFwdLimitSwitchClosed());
            Log.d("Lift Current " + liftMotor.getOutputCurrent());
            Log.d("Lift Voltage " + liftMotor.getMotorOutputVoltage());
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
    private int[] getCurrentSetpoints() {
        final int[] setpoints;

        switch (getIntakeMode()) {
            case CARGO:
                setpoints = CARGO_SETPOINTS;
                break;
            case HATCH:
                setpoints = HATCH_SETPOINTS;
                break;
            default:
                setpoints = new int[0];
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
        setPositionEncoderTicks(maxSpeed, position / Config.LIFT_ENCODER_DPP);
    }

    /**
     * Sets the position for the talons to reach in encoder ticks.
     *
     * @param maxSpeed             The maximum speed at which to travel.
     * @param encoderTicksPosition The position, in encoder ticks.
     */
    public void setPositionEncoderTicks(final double maxSpeed, final double encoderTicksPosition) {
        enableLimit(true);
        enableLimitSwitch(true);
        liftMotor.configClosedLoopPeakOutput(0, maxSpeed);
        liftMotor.set(ControlMode.Position, encoderTicksPosition);
    }


    /**
     * Sets a position to for the talons to reach using motion magic.
     *
     * @param maxSpeed The maximum speed (from 0 to 1).
     * @param position The position in feet.
     */
    public void setPositionMotionMagic(final double maxSpeed, final double position) {
        setPositionMotionMagicEncoderTicks(maxSpeed, position / Config.LIFT_ENCODER_DPP);
    }

    /**
     * Sets a position for the talons to reach using motion magic in terms of encoder ticks.
     *
     * @param maxSpeed The max speed (from 0 to 1).
     * @param position The number of encoder ticks.
     */
    public void setPositionMotionMagicEncoderTicks(final double maxSpeed, final double position) {
        enableLimit(true);
        enableLimitSwitch(getLiftHeight() > 0);

        liftMotor.configClosedLoopPeakOutput(0, maxSpeed);
        liftMotor.set(ControlMode.MotionMagic, position);
    }

    /**
     * Sets a velocity for the talon motors.
     *
     * @param velocity Velocity in encoder ticks.
     */
    public void setVelocity(int velocity) {
        enableLimit(true);
        enableLimitSwitch(true);

        // If we're approaching either the top of the bottom of the lift, begin to slow down.
        final double liftHeight = getLiftHeight(); // Get lift height above bottom.
        // Get lift distance from top
        final double liftDistanceFromTop = Config.MAX_LIFT_ENCODER_TICKS * Config.LIFT_ENCODER_DPP - liftHeight;

        final boolean needToSlowDown = (liftDistanceFromTop < Config.LIFT_SLOWDOWN_RANGE_DOWN && velocity > 0) || (liftHeight < Config.LIFT_SLOWDOWN_RANGE_DOWN && velocity < 0);
        if (needToSlowDown) {
            int maxLiftSpeedAtThisHeight;
            // If we're going down.
            if (velocity < 0) {
                maxLiftSpeedAtThisHeight = -(int) (Config.LIFT_MAX_SPEED.value() * (liftHeight
                        / Config.LIFT_SLOWDOWN_RANGE_UP + 0.45));
                velocity = Math.max(velocity, maxLiftSpeedAtThisHeight);
            }
            // If we're going up.
            else {
                maxLiftSpeedAtThisHeight = (int) (Config.LIFT_MAX_SPEED.value() * (liftDistanceFromTop
                        / Config.LIFT_SLOWDOWN_RANGE_DOWN + 0.45));
                velocity = Math.min(velocity, maxLiftSpeedAtThisHeight);
            }
        }

        liftMotor.configClosedLoopPeakOutput(0, 1.0); // Peak output to max (1.0).
        liftMotor.set(ControlMode.Velocity, velocity);
    }

    /**
     * Sets the percent output for the talon lift motor.
     *
     * @param percentOutput Percent output, between -1 and 1
     */
    public void setPercentOutput(double percentOutput) {
        enableLimit(true);
        enableLimitSwitch(true);
        liftMotor.set(percentOutput);
    }

    /**
     * Overrides limits on the talons to go up with override.
     */
    public void overrideUp() {
        enableLimit(false);
        enableLimitSwitch(true);
        liftMotor.set(Config.LIFT_OVERRIDE_UP_SPEED);
    }

    public void overrideDown() {
        enableLimit(false);
        enableLimitSwitch(true);
        liftMotor.set(Config.LIFT_OVERRIDE_DOWN_SPEED);
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
    private static Pneumatics.IntakeMode getIntakeMode() {
        return Pneumatics.getInstance().getMode();
    }

    /**
     * Moves the lift to the given setpoint id.
     *
     * @param speed    The speed at which to move the lift.
     * @param setpoint The setpoint id, between 0 and 3.
     */
    public void moveToSetpoint(final double speed, final int setpoint) {
        final int[] currentSetpoints = getCurrentSetpoints();
        if (0 <= setpoint && setpoint <= currentSetpoints.length) {
            setPositionMotionMagicEncoderTicks(speed, currentSetpoints[setpoint]);
        }
    }

    /**
     * Determines if the lift has reached the given setpoint.
     *
     * @param setpoint The setpoint.
     * @return True if the lift has reached the setpoint, false otherwise.
     */
    public boolean hasReachedSetpoint(final int setpoint) {
        final int[] currentSetpoints = getCurrentSetpoints();

        boolean hasReachedSetpoint = false;
        if (0 <= setpoint && setpoint <= currentSetpoints.length) {
            // We're good as long as we're within 0.1 feet of the target position.
            hasReachedSetpoint = hasReachedPosition(currentSetpoints[setpoint]);
        }

        return hasReachedSetpoint;
    }

    /**
     * Determines if the robot has reached the given position.
     *
     * @param position The position in feet.
     * @return True if the lift is within a certain margin of error fo the position, false otherwise.
     */
    public boolean hasReachedPosition(final double position) {
        return Math.abs(getLiftHeightEncoderTicks() - position / Config.LIFT_ENCODER_DPP) < 750;
    }

    /**
     * Gets the current height of the lift, in feet.
     *
     * @return The lift height as measured by encoders, in feet.
     */
    public double getLiftHeight() {
        return liftMotor.getSelectedSensorPosition() * Config.LIFT_ENCODER_DPP;
    }

    /**
     * Gets the lift height in encoder ticks.
     *
     * @return The lift height in encoder ticks.
     */
    public int getLiftHeightEncoderTicks() {
        return liftMotor.getSelectedSensorPosition();
    }
}