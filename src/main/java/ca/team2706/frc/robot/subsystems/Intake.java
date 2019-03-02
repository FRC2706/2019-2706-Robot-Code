package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The subsystem which controls the intake for cargo and hatches
 */
public class Intake extends Subsystem {
    private static Intake currentInstance;

    private VictorSPX intakeMotor;
    private AnalogInput irSensor;

    private DoubleSolenoid intakeLiftSolenoid;
    private DoubleSolenoid hatchEjectorSolenoid;

    /**
     * The current status of the intake arms, whether they're in hatch mode or in cargo mode.
     */
    private IntakeMode mode = IntakeMode.HATCH; // Always start in hatch mode.

    /**
     * True if the plunger is out, false otherwise.
     */
    private boolean plungerExtended = false;

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
     * @param intakeTalon          The motor controller object for the intake motor.
     * @param irSensor             The ir sensor analog input object.
     * @param intakeLiftSolenoid   The intake's lift solenoid object.
     * @param hatchEjectorSolenoid The pneumatic piston hatch ejector solenoid.
     */
    private Intake(VictorSPX intakeTalon, AnalogInput irSensor, DoubleSolenoid intakeLiftSolenoid, DoubleSolenoid hatchEjectorSolenoid) {
        this.intakeMotor = intakeTalon;
        this.irSensor = irSensor;
        this.intakeLiftSolenoid = intakeLiftSolenoid;
        this.hatchEjectorSolenoid = hatchEjectorSolenoid;

        this.intakeMotor.setNeutralMode(NeutralMode.Brake);
    }

    /**
     * Constructs a new Intake.
     */
    private Intake() {
        this(new VictorSPX(Config.INTAKE_MOTOR_ID),
                new AnalogInput(Config.CARGO_IR_SENSOR_ID),
                new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID),
                new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID));
    }

    /**
     * Gets the current intake mode, either hatch or cargo.
     *
     * @return The intake's current mode.
     */
    public IntakeMode getMode() {
        return mode;
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
        if (getMode() == IntakeMode.CARGO) {
            irValue = irSensor.getVoltage();
        }
        // Otherwise we say 0 since we're dealing with hatches.
        else {
            irValue = 0;
        }

        return irValue;
    }

    /**
     * Spins the intake wheels forward both to intake and eject cargo.
     *
     * @param percentSpeed speed at which to change the wheels, between 0 and 1.
     */
    public void runIntakeForward(final double percentSpeed) {
        if (getMode() == IntakeMode.CARGO) {
            intakeMotor.set(ControlMode.PercentOutput, Math.abs(percentSpeed * Config.MAX_INTAKE_SPEED));
        }
    }

    /**
     * Spins the intake wheels backwards.
     *
     * @param percentSpeed Speed at which to spin the wheels, between 0 and 1.
     */
    public void runIntakeBackward(final double percentSpeed) {
        if (getMode() == IntakeMode.CARGO) {
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
        return readIr() > Config.CARGO_CAPTURED_IR_VOLTAGE.value();
    }

    /**
     * Lowers the intake arms, in preparation for inhaling cargo.
     */
    public void lowerIntake() {
        // We don't want to lower the intake onto the plunger.
        if (isPlungerStowed()) {
            intakeLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
            mode = IntakeMode.CARGO;
        }
    }

    /**
     * Raises the intake arms in preparation for manipulating hatches.
     */
    public void raiseIntake() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kForward);
        mode = IntakeMode.HATCH;
    }

    /**
     * Turns off the intake arms pneumatics.
     */
    public void stopArmsPneumatics() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Extends the hatch deployment cylinder
     */
    public void deployPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kReverse);
        plungerExtended = true;
    }

    /**
     * Retracts the hatch deployment cylinder
     */
    public void retractPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
        plungerExtended = false;
    }

    /**
     * Turns the pneumatics for the plunger piston off.
     */
    public void stopPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Determines if the hatch ejector (plunger) is in the inward position (stowed) or in the outward position (not stowed).
     *
     * @return True if the plunger is stowed, false otherwise.
     */
    public boolean isPlungerStowed() {
        return !plungerExtended;
    }

}