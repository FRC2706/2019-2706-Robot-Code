package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The subsystem which controls the intake for cargo and hatches
 */
public class Intake extends Subsystem {
    private static Intake currentInstance;

    private WPI_VictorSPX intakeMotor;
    private AnalogInput irSensor;

    private final SubsystemStatus status;

    /**
     * Gets the current instance of this subsystem, creating it if it doesn't exist.
     *
     * @return The subsystem's instance.
     */
    public static Intake getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new instance of this subsystem, if it hasn't been initialized.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new Intake();
        }
    }

    /**
     * Constructs a new intake subsystem with the given parameters.
     *
     * @param intakeTalon The motor controller object for the intake motor.
     * @param irSensor    The ir sensor analog input object.
     */
    private Intake(WPI_VictorSPX intakeTalon, AnalogInput irSensor) {
        this.intakeMotor = intakeTalon;
        this.irSensor = irSensor;

        SubsystemStatus status = SubsystemStatus.OK;

        if(this.intakeMotor.configFactoryDefault(Config.CAN_LONG) != ErrorCode.OK) {
            Log.e("Intake Victor not functioning");
            status = SubsystemStatus.ERROR;
        }

        this.status = status;

        this.intakeMotor.setNeutralMode(NeutralMode.Brake);

        addChild("IR Sensor", irSensor);
        addChild("Intake Motor", intakeTalon);
    }

    /**
     * Constructs a new Intake.
     */
    private Intake() {
        this(new WPI_VictorSPX(Config.INTAKE_MOTOR_ID),
                new AnalogInput(Config.CARGO_IR_SENSOR_ID));
    }

    public SubsystemStatus getStatus() {
        return status;
    }

    @Override
    public void initDefaultCommand() {
    }

    /**
     * Getting the voltage from the IR sensor.
     *
     * @return the voltage reading
     */
    public double readIr() {
        return irSensor.getAverageVoltage();
    }

    /**
     * Spins the intake wheels forward both to intake and eject cargo.
     *
     * @param percentSpeed speed at which to change the wheels, between 0 and 1.
     */
    public void runIntakeForward(final double percentSpeed) {
        if (Pneumatics.getInstance().getMode() == Pneumatics.IntakeMode.CARGO) {
            intakeMotor.set(ControlMode.PercentOutput, Math.abs(percentSpeed * Config.MAX_INTAKE_SPEED));
        }
    }

    /**
     * Spins the intake wheels backwards.
     *
     * @param percentSpeed Speed at which to spin the wheels, between 0 and 1.
     */
    public void runIntakeBackward(final double percentSpeed) {
        if (Pneumatics.getInstance().getMode() == Pneumatics.IntakeMode.CARGO) {
            intakeMotor.set(ControlMode.PercentOutput, -(Math.abs(percentSpeed * Config.MAX_INTAKE_SPEED)));
        }
    }

    /**
     * Stop motors
     */
    public void stop() {
        intakeMotor.set(ControlMode.PercentOutput, 0);
    }

    /**
     * Check if the intake has a cargo within it
     *
     * @return whether the intake has cargo or not
     */
    public boolean isCargoInMechanism() {
        return readIr() > Config.CARGO_CAPTURED_IR_MIN_VOLTAGE.value();
    }

    /**
     * Determines if the cargo in the mechanisms is positioned well. Used for automatically intaking cargo.
     *
     * @return True if the cargo is positioned well, false otherwise.
     */
    public boolean isCargoPositionedWell() {
        return Math.abs(readIr() - Config.CARGO_CAPTURED_IDEAL_IR_VOLTAGE.value()) <= 0.01;
    }

}