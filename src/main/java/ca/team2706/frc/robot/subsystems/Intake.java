package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The subsystem which comtrols the intake for cargo and hatches
 */
public class Intake extends Subsystem {
    private static Intake currentInstance;

    public WPI_TalonSRX intakeMotor;
    private AnalogInput irSensor;

    private DoubleSolenoid intakeLiftSolenoid;
    private DoubleSolenoid hatchEjectorSolenoid;

    /**
     * The state of the intake subsystem.
     */
    private IntakeMode currentMode;

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

    public Intake(WPI_TalonSRX intakeTalon, AnalogInput irSensor, DoubleSolenoid intakeLiftSolenoid, DoubleSolenoid hatchEjectorSolenoid) {
        this.intakeMotor = intakeTalon;
        this.irSensor = irSensor;
        this.intakeLiftSolenoid = intakeLiftSolenoid;
        this.hatchEjectorSolenoid = hatchEjectorSolenoid;

        this.intakeMotor.setNeutralMode(NeutralMode.Brake);
    }

    /**
     * Constructs a new Intake.
     */
    public Intake() {
        this(new WPI_TalonSRX(Config.INTAKE_MOTOR_ID),
                new AnalogInput(Config.CARGO_IR_SENSOR_ID),
                new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID),
                new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID));
    }

    /**
     * Gets the current intake mode, either hatch or cargo.
     * @return The intake's current mode.
     */
    public IntakeMode getMode() {
        return currentMode;
    }

    /**
     * Different states that the intake subsystem can be in, either
     * inhaling hatches or inhaling cargo.
     */
    public enum IntakeMode {
        CARGO, HATCH
    }

    @Override
    public void initDefaultCommand() {
    }

    /**
     * Getting the voltage from the IR sensor, returning 0 if not configured to deal with cargo.
     *
     * @return the voltage reading
     */
    public double readIr() {
        final double irValue;

        // We only want to be reading the real ir sensor if we're supposed to be dealing with cargo.
        if (currentMode == IntakeMode.CARGO) {
            irValue = irSensor.getVoltage();
        }
        // Otherwise we say 0 since we're dealing with hatches.
        else {
            irValue = 0;
        }

        return irValue;
    }

    /**
     * Spins the wheels to intake cargo.
     *
     * @param speed speed at which to change the wheels, in percentage.
     */
    public void inhaleCargo(double speed) {
        if (currentMode == IntakeMode.CARGO) {
            intakeMotor.set(speed * Config.MAX_INTAKE_SPEED);
        }
    }

    /**
     * Spins the wheels to eject cargo.
     *
     * @param speed Speed at which to spin the wheels, in percentage.
     */
    public void exhaleCargo(double speed) {
        if (currentMode == IntakeMode.CARGO) {
            speed = -(speed * Config.MAX_INTAKE_SPEED);
            intakeMotor.set(speed);
        }
    }

    /**
     * Stop motors
     */
    public void stop() {
        intakeMotor.set(0);
    }

    /**
     * Check if the intake has a cargo within it
     *
     * @return whether the intake has cargo or not
     */
    public boolean isCargoInMechanism() {
        return readIr() > Config.CARGO_CAPTURED_IR_VOLTAGE.value();
    }

    /**
     * Lowers the intake arms, in preparation for inhaling cargo.
     */
    public void lowerIntake() {
        // We don't want to lower the intake onto the plunger.
        if (isPlungerStowed()) {
            intakeLiftSolenoid.set(DoubleSolenoid.Value.kForward);
            currentMode = IntakeMode.CARGO;
        }
    }

    /**
     * Raises the intake arms in preparation for manipulating hatches.
     */
    public void raiseIntake() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
        currentMode = IntakeMode.HATCH;
    }

    /**
     * Extends the hatch deployment cylinder
     */
    public void ejectHatch() {
        if (currentMode == IntakeMode.HATCH) {
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
        }
    }

    /**
     * Retracts the hatch deployment cylinder
     */
    public void retractPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    /**
     * Determines if the hatch ejector (plunger) is in the inward position (stowed) or in the outward position (not stowed).
     *
     * @return True if the plunger is stowed, false otherwise.
     */
    public boolean isPlungerStowed() {
        return hatchEjectorSolenoid.get() == DoubleSolenoid.Value.kReverse;
    }

}