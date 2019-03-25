package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Sendables;
import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem for operating climber motors.
 */
public class ClimberMotor extends Subsystem {
    private static ClimberMotor currentInstance;

    /**
     * Gets or creates the current ClimberMotor instance.
     *
     * @return The current instance of the ClimberMotor class.
     */
    public static ClimberMotor getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new instance of the ClimberMotor subsystem.
     *
     * @return The status after initialization of the subsystem.
     */
    public static SubsystemStatus init() {
        if (currentInstance == null) {
            currentInstance = new ClimberMotor();
        }

        return currentInstance.getStatus();
    }

    private WPI_TalonSRX climberMotor;

    private SubsystemStatus status;

    /**
     * Constructs a new climber motor instance with default motor id.
     */
    private ClimberMotor() {
        this(new WPI_TalonSRX(Config.CLIMBER_MOTOR_ID));
    }

    /**
     * Constructs a new climber motor with the given talon motor controller.
     *
     * @param motor The motor controller object.
     */
    private ClimberMotor(final WPI_TalonSRX motor) {
        this.climberMotor = motor;
        addChild("Climber Motor", this.climberMotor);
        addChild("Climber Encoders", Sendables.newTalonEncoderSendable(this.climberMotor));
        this.status = configTalonMotor();
    }

    /**
     * Configures the talon motor.
     *
     * @return The status after initializing talons.
     */
    private SubsystemStatus configTalonMotor() {
        SubsystemStatus status = SubsystemStatus.OK;

        if (SubsystemStatus.checkError(climberMotor.configFactoryDefault(Config.CAN_LONG))) {
            Log.e("Climber motor not working.");
            status = SubsystemStatus.maxError(SubsystemStatus.ERROR, status);
        }


        climberMotor.setNeutralMode(NeutralMode.Brake);
        climberMotor.setInverted(Config.INVERT_CLIMBER_MOTOR);

        climberMotor.configPeakCurrentLimit(Config.CLIMBER_PEAK_CURRENT_LIMIT, Config.CAN_LONG);
        climberMotor.configContinuousCurrentLimit(Config.CLIMBER_CONTINUOUS_CURRENT_LIMIT);
        climberMotor.configPeakCurrentDuration(Config.CLIMBER_CURRENT_LIMIT_THRESHOLD_MS);
        climberMotor.enableCurrentLimit(Config.ENABLE_CLIMBER_CURRENT_LIMIT);

        if (SubsystemStatus.checkError(climberMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_LONG))) {
            Log.e("Climber encoders unreachable.");
            status = SubsystemStatus.maxError(status, SubsystemStatus.ERROR);
        }

        climberMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_LONG);
        climberMotor.setSensorPhase(Config.ENABLE_CLIMBER_SUM_PHASE.value());
        climberMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Config.CAN_LONG);
        climberMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Config.CAN_LONG);
        climberMotor.configNeutralDeadband(Config.CLIMBER_CLOSED_LOOP_DEADBAND.value());

        if (SubsystemStatus.checkError(climberMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Config.CAN_LONG))) {
            Log.e("Reverse climber limit switch unreachable.");
            status = SubsystemStatus.maxError(status, SubsystemStatus.WORKABLE);
        }

        climberMotor.configVoltageCompSaturation(12, Config.CAN_LONG);
        climberMotor.enableVoltageCompensation(true);

        climberMotor.configForwardSoftLimitThreshold(Config.MAX_CLIMBER_ENCODER_TICKS, Config.CAN_LONG);
        climberMotor.configReverseSoftLimitThreshold(0, Config.CAN_LONG);

        climberMotor.configOpenloopRamp(Config.CLIMBER_OPEN_LOOP_RAMP.value(), Config.CAN_LONG);

        return status;
    }

    /**
     * Gets the subsystem's status after initialization. This should indicate whether or not there
     * were any errors or problems that could affect robot operation.
     *
     * @return The subsystem status.
     */
    private SubsystemStatus getStatus() {
        return status;
    }

    /**
     * Runs the climber motor forward, making the robot climb.
     */
    public void runMotorForward() {
        if (getStatus() != SubsystemStatus.ERROR) {
            climberMotor.set(ControlMode.PercentOutput, Config.CLIMBER_FORWARD_SPEED.value());
        }
    }

    /**
     * Retracts the climber mechanisms by running the motor backwards.
     */
    public void runMotorBackward() {
        if (getStatus() != SubsystemStatus.ERROR) {
            climberMotor.set(ControlMode.PercentOutput, -Config.CLIMBER_REVERSE_SPEED.value());
        }
    }

    /**
     * Gets the current reading on the climber encoders for the distance travelled.
     *
     * @return The current reading of the encoder's position, in ticks.
     */
    public int getClimberTicks() {
        return climberMotor.getSelectedSensorPosition(0);
    }

    /**
     * Determines if the climber motor has travelled far enough to launch the robot using the pushers.
     *
     * @return True if the motor has travelled far enough, false otherwise.
     */
    public boolean isClimberReadyForPistons() {
        return getClimberTicks() >= Config.CLIMBER_SUFFICIENT_HEIGHT_ENCODER_TICKS;
    }

    /**
     * Stops the climber motor.
     */
    public void stopMotor() {
        climberMotor.set(ControlMode.PercentOutput, 0);
    }

    @Override
    protected void initDefaultCommand() {
    }
}
