package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {

    private static Climber currentInstance;

    /**
     * Climber motor object.
     */
    private WPI_TalonSRX climberMotor;

    /**
     * Gets the current instance of the climber subsystem
     * @return The current climber instance.
     */
    public static Climber getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new instance of the climber subsystem, if it has not been not been initialized.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new Climber();
        }
    }

    /**
     * Constructs a new climber instance with default talon motors and such.
     */
    private Climber() {
        this(new WPI_TalonSRX(Config.CLIMBER_MOTOR_ID));
    }

    /**
     * Constructs a new climber instance with the given climber motor.
     * @param climberMotor The climber motor.
     */
    public Climber(final WPI_TalonSRX climberMotor) {
        this.climberMotor = climberMotor;
    }

    /**
     * Configures the talon motor.
     */
    private void configTalonMotor() {
        climberMotor.configFactoryDefault(Config.CAN_LONG);
        climberMotor.setNeutralMode(NeutralMode.Brake);
        climberMotor.setInverted(Config.INVERT_CLIMBER_MOTOR);

        climberMotor.configPeakCurrentLimit(Config.CLIMBER_PEAK_CURRENT_LIMIT, Config.CAN_LONG);
        climberMotor.configContinuousCurrentLimit(Config.CLIMBER_CONTINUOUS_CURRENT_LIMIT);
        climberMotor.configPeakCurrentDuration(Config.CLIMBER_CURRENT_LIMIT_THRESHOLD_MS);
        climberMotor.enableCurrentLimit(Config.ENABLE_CLIMBER_CURRENT_LIMIT);

        climberMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Config.CAN_LONG);
        climberMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Config.CAN_LONG);

        climberMotor.configVoltageCompSaturation(12, Config.CAN_LONG);
        climberMotor.enableVoltageCompensation(true);

        climberMotor.configOpenloopRamp(Config.LIFT_VOLTAGE_RAMP_UP_PERIOD, Config.CAN_LONG);
    }

    /**
     * Runs the climber motor forward, making the robot climb.
     */
    public void runClimberUp() {
        climberMotor.set(ControlMode.PercentOutput, 1.0);
    }

    /**
     * Retracts the climber mechanisms by running the motor backwards.
     */
    public void retractClimber() {
        climberMotor.set(ControlMode.PercentOutput, -1.0);
    }

    @Override
    protected void initDefaultCommand() {
    }
}
